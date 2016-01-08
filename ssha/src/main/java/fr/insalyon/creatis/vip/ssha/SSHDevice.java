/*
 Copyright 2015

 CREATIS
 CNRS UMR 5220 -- INSERM U1044 -- Université Lyon 1 -- INSA Lyon

 Authors

 Nouha Boujelben (nouha.boujelben@creatis.insa-lyon.fr)
 Tristan Glatard (tristan.glatard@creatis.insa-lyon.fr)

 This software is a daemon for file synchronization between SFTP
 servers and the LCG File Catalog (LFC).

 This software is governed by the CeCILL-B license under French law and
 abiding by the rules of distribution of free software.  You can use,
 modify and/ or redistribute the software under the terms of the
 CeCILL-B license as circulated by CEA, CNRS and INRIA at the following
 URL "http://www.cecill.info".

 As a counterpart to the access to the source code and rights to copy,
 modify and redistribute granted by the license, users are provided
 only with a limited warranty and the software's author, the holder of
 the economic rights, and the successive licensors have only limited
 liability.

 In this respect, the user's attention is drawn to the risks associated
 with loading, using, modifying and/or developing or reproducing the
 software by the user in light of its specific status of free software,
 that may mean that it is complicated to manipulate, and that also
 therefore means that it is reserved for developers and experienced
 professionals having in-depth computer knowledge. Users are therefore
 encouraged to load and test the software's suitability as regards
 their requirements in conditions enabling the security of their
 systems and/or data to be ensured and, more generally, to use and
 operate it in the same conditions as regards security.

 The fact that you are presently reading this means that you have had
 knowledge of the CeCILL-B license and that you accept its terms.
 */
package fr.insalyon.creatis.vip.ssha;

import fr.insalyon.creatis.vip.synchronizedcommons.FileProperties;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDevice;
import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 * The SSH device.
 *
 * @author Tristan Glatard tristan.glatard@creatis.insa-lyon.fr,
 * @author Nouha Boujelben nouha.boujelben@creatis.insa-lyon.fr
 */
public class SSHDevice implements SyncedDevice {

    private Session session;

    //ssh config
    private SSHSynchronization account;
    private String remoteDir;
    private String hostname;
    private String user;
    private final String privateKeyFile;
    private final String privateKeyPass;
    private int port;
    //database config
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String LOCAL_TEMP_BASE;
    private String LOCAL_TEMP;

    private final static Logger logger = Logger.getLogger(SSHDevice.class);

    /**
     * Constructor.
     *
     * @param privateKeyFile the private key file used to connect to the SSH
     * device.
     * @param privateKeyPass the password of the private key.
     * @param LOCAL_TEMP_BASE the base directory where to store the downloaded
     * files.
     * @param jdbcUrl jdbc URL of the database containing the synchronizations.
     * @param dbUsername DB user name.
     * @param dbPassword DB password.
     */
    public SSHDevice(String privateKeyFile, String privateKeyPass,
            String LOCAL_TEMP_BASE, String jdbcUrl, String dbUsername, String dbPassword) {

        this.privateKeyFile = privateKeyFile;
        this.privateKeyPass = privateKeyPass;
        this.LOCAL_TEMP_BASE = LOCAL_TEMP_BASE;
        this.jdbcUrl = jdbcUrl;
        this.username = dbUsername;
        this.password = dbPassword;
    }

    @Override
    public String toString() {
        return user + "@" + hostname + ":" + port + "/" + remoteDir;
    }

    /*
     * Methods implementing the SyncedDevice interface.
     */
    @Override
    public void setSynchronization(Synchronization s) {
        SSHSynchronization ua = (SSHSynchronization) s;
        this.remoteDir = ua.getSSHDirectoryName();
        this.hostname = ua.getSSHHostName();
        this.user = ua.getSSHUserName();
        this.port = ua.getSSHPort();
        this.LOCAL_TEMP = LOCAL_TEMP_BASE + "/" + hostname + "-" + port + "-" + user + "/" + remoteDir;
    }

    @Override
    public HashMap<String, FileProperties> listFiles(String dir, Synchronization synchronization) throws SyncException {
        connect();
        try {
            HashMap<String, FileProperties> map = new HashMap<String, FileProperties>();
            String command;
            //this command return consecutively name, size of files and 0.
            String commandNotCheckFileContent = "find " + remoteDir + "/" + dir + " -type f -printf \"%p; %s; 0\\n\"";
            //this command return consecutively name, size and md5sum of files
            String commandMd5sum = "find " + remoteDir + "/" + dir + " -type f  -printf \"%p;%s;\" -exec md5sum {} \\; | awk '{$(NF--)=\"\"; print}'";
            if (SSHMySQLDAO.getInstance(jdbcUrl, username, password).isCheckFilesContent(synchronization)) {
                command = commandMd5sum;
            } else {
                command = commandNotCheckFileContent;
            }
            for (String s : sendCommand(command).split("\n")) {
                if (!s.equals("")) {
                    //add revision the size of file in this List
                    map.put(s.split(";")[0].trim().replaceAll("//", "/").replaceAll(remoteDir, ""), new FileProperties(Long.valueOf(s.split(";")[1].trim()), s.split(";")[2].trim()));

                }
            }
            return map;
        } finally {
            disconnect();
        }
    }

    @Override
    public void getFile(String remoteFile, String localDir) throws SyncException {
        connect();
        try {
            getFileConnected(remoteFile, localDir);
        } finally {
            disconnect();
        }
    }

    @Override
    public void putFile(String localFile, String remoteDir) throws SyncException {
        connect();
        try {
            putFileConnected(localFile, remoteDir);
        } finally {
            disconnect();
        }
    }

    /**
     *
     * @param remoteFile
     * @throws SyncException
     */
    @Override
    public void deleteFile(String remoteFile) throws SyncException {
        connect();
        try {
            deleteFileConnected(remoteFile);
        } finally {
            disconnect();
        }
    }

    @Override
    public void setSynchronizationFailed(Synchronization ua) throws SyncException {
        SSHMySQLDAO.getInstance(jdbcUrl, username, password).setSynchronizationFailed(ua);
    }

    @Override
    public void setSynchronizationNotFailed(Synchronization ua) throws SyncException {
        SSHMySQLDAO.getInstance(jdbcUrl, username, password).setSynchronizationNotFailed(ua);
    }

    @Override
    public void validateSynchronization(Synchronization ua) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Synchronization> getActiveSynchronization() throws SyncException {
        return SSHMySQLDAO.getInstance(jdbcUrl, username, password).getActiveSynchronizations();
    }

    @Override
    public String getRevision(String remoteFile, Synchronization synchronization) throws SyncException {
        String res;
        String realRemotePath = (remoteDir + "/" + remoteFile).replaceAll("//", "/");
        if (SSHMySQLDAO.getInstance(jdbcUrl, username, password).isCheckFilesContent(synchronization)) {
            connect();
            res = sendCommand("(md5sum " + realRemotePath + " 2>/dev/null || echo error) | awk '{print $1}'");
            disconnect();
        } else {
            res = "0";

        }
        return res.trim();
    }

    @Override
    public String getTempDir() {
        return this.LOCAL_TEMP;
    }

    @Override
    public String getAuthFailedString() {
        return "session is down";//Exception message returned by jsch when authentication fails.
    }

    /**
     * Private methods
     */
    private void getFileConnected(String remoteFile, String localDir) throws SyncException {
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.get(remoteDir + "/" + remoteFile, localDir);
            sftpChannel.exit();
        } catch (JSchException ex) {
            throw new SyncException(ex);
        } catch (SftpException ex) {
            throw new SyncException(ex);
        }
    }

    private void deleteFileConnected(String remoteFile) throws SyncException {
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            sftpChannel.rm(remoteDir + "/" + remoteFile);
            sftpChannel.exit();
        } catch (JSchException ex) {
            throw new SyncException(ex);
        } catch (SftpException ex) {
            throw new SyncException(ex);
        }
    }

    private void putFileConnected(String localFile, String relativeRemotePath) throws SyncException {
        try {

            sendCommand("mkdir -p " + this.remoteDir + "/" + relativeRemotePath.substring(0, relativeRemotePath.lastIndexOf("/")));

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            String destPath = (remoteDir + "/" + relativeRemotePath);
            sftpChannel.put(localFile, destPath);
            sftpChannel.exit();
        } catch (JSchException ex) {
            throw new SyncException(ex);
        } catch (SftpException ex) {
            throw new SyncException(ex);
        }
    }

    private void connect() throws SyncException {
        JSch jsch = new JSch();
        try {
            jsch.addIdentity(privateKeyFile, privateKeyPass);

        } catch (JSchException ex) {
            java.util.logging.Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            session = jsch.getSession(user, hostname, port);
            session.setConfig("StrictHostKeyChecking", "no");

            session.connect();
        } catch (JSchException ex) {
            throw new SyncException(ex);
        }
    }

    private void disconnect() {
        session.disconnect();
    }

    private String sendCommand(String command) throws SyncException {
        try {
            StringBuilder outputBuffer = new StringBuilder();
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.connect();
            InputStream commandOutput;
            InputStreamReader err;

            try {
                err = new InputStreamReader(((ChannelExec) channel).getErrStream());
            } catch (IOException ex) {
                throw new SyncException(ex);
            }

            try {
                commandOutput = channel.getInputStream();
            } catch (IOException ex) {
                throw new SyncException(ex);
            }

            String incomingLine = null;
            String lines = null;
            BufferedReader reader = new BufferedReader(err);
            while ((incomingLine = reader.readLine()) != null) {
                if (lines == null) {
                    lines = incomingLine;
                } else {
                    lines += incomingLine;
                }
            }
            if (lines != null) {
                logger.error("Remote command failed with error message " + lines);
                reader.close();
                throw new SyncException("Remote command failed with error message " + lines);
            }

            int readByte;
            try {
                readByte = commandOutput.read();
            } catch (IOException ex) {
                throw new SyncException(ex);
            }
            while (readByte != 0xffffffff) {
                outputBuffer.append((char) readByte);
                try {
                    readByte = commandOutput.read();
                } catch (IOException ex) {
                    throw new SyncException(ex);
                }
            }
            channel.disconnect();
            return outputBuffer.toString();
        } catch (JSchException ex) {
            throw new SyncException(ex);
        } catch (IOException ex) {
            throw new SyncException(ex);
        }
    }

    @Override
    public boolean isMustWaitBeforeNextSynchronization(Synchronization ua) throws SyncException {

        return SSHMySQLDAO.getInstance(jdbcUrl, username, password).isMustWaitBeforeNextSynchronization(ua);

    }

    @Override
    public int getNumberSynchronizationFailed(Synchronization ua) throws SyncException {

        return SSHMySQLDAO.getInstance(jdbcUrl, username, password).getNumberSynchronizationFailed(ua);

    }

    @Override
    public void updateTheEarliestNextSynchronization(Synchronization ua, long duration) throws SyncException {

        SSHMySQLDAO.getInstance(jdbcUrl, username, password).updateTheEarliestNextSynchronistation(ua, duration);

    }

    @Override
    public void updateNumberSynchronizationFailed(Synchronization ua, int number) throws SyncException {

        SSHMySQLDAO.getInstance(jdbcUrl, username, password).updateNumberSynchronizationFailed(ua, number);

    }

    @Override
    public double getNbSecondFromConfigFile() {

        return ConfigFile.getInstance().getNbSecond();

    }

    @Override
    public void updateLFCMonitoringParams(Synchronization ua, int numberOfFilesTransferredToLFC, long sizeOfFilesTransferredToLFC, int numberOfFilesDeletedInLFC, long sizeOfFilesDeletedInLFC) throws SyncException {
        SSHMySQLDAO.getInstance(jdbcUrl, username, password).updateLFCMonitoringParams(ua, numberOfFilesTransferredToLFC, sizeOfFilesTransferredToLFC, numberOfFilesDeletedInLFC, sizeOfFilesDeletedInLFC);
    }

    @Override
    public void updateDeviceMonitoringParams(Synchronization ua, int numberOfFilesTransferredToDevice, long sizeOfFilesTransferredToDevice, int numberOfFilesDeletedInDevice, long sizeOfFilesDeletedInDevice) throws SyncException {
        SSHMySQLDAO.getInstance(jdbcUrl, username, password).updateDeviceMonitoringParams(ua, numberOfFilesTransferredToDevice, sizeOfFilesTransferredToDevice, numberOfFilesDeletedInDevice, sizeOfFilesDeletedInDevice);
    }

}
