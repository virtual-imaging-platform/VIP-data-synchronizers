/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

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
import java.io.IOException;
import java.io.InputStream;
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
    public HashMap<String, String> listFiles(String dir) throws SyncException {
        connect();
        try {
            HashMap<String, String> map = new HashMap<String, String>();
            for (String s : sendCommand("for i in `find " + remoteDir + "/" + dir + " -type f`; do echo -n $i\" ; \"; (md5sum $i 2>/dev/null || echo error) | awk '{print $1}'; done").split("\n")) {
                if (!s.equals("")) {
                    if (s.split(";").length != 2) {
                        throw new SyncException("Wrong file list: " + s);
                    }
                    map.put(s.split(";")[0].trim().replaceAll("//", "/").replaceAll(remoteDir, ""), s.split(";")[1].trim());
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
    public List<Synchronization> getSynchronization() throws SyncException {
        return SSHMySQLDAO.getInstance(jdbcUrl, username, password).getSynchronizations();
    }

    @Override
    public String getRevision(String remoteFile) throws SyncException {
        connect();
        String realRemotePath = (remoteDir + "/" + remoteFile).replaceAll("//", "/");
        // logger.info("getting revision of file "+realRemotePath);
        String res = sendCommand("(md5sum " + realRemotePath + " 2>/dev/null || echo error) | awk '{print $1}';");
        disconnect();
        return res;
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
            ex.printStackTrace();
            throw new SyncException(ex);
        }
    }

    private void connect() {
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
            java.util.logging.Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
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
            try {
                commandOutput = channel.getInputStream();
            } catch (IOException ex) {
                throw new SyncException(ex);
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
        }
    }

    @Override
    public boolean isMustWaitBeforeNextSynchronization(Synchronization ua) {
        try {
            return SSHMySQLDAO.getInstance(jdbcUrl, username, password).isMustWaitBeforeNextSynchronization(ua);
        } catch (SyncException ex) {
            java.util.logging.Logger.getLogger(SSHDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public int getNumberSynchronizationFailed(Synchronization ua) {
        try {
            return SSHMySQLDAO.getInstance(jdbcUrl, username, password).getNumberSynchronizationFailed(ua);
        } catch (SyncException ex) {
            java.util.logging.Logger.getLogger(SSHDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public void updateTheEarliestNextSynchronization(Synchronization ua, long duration) {
        try {
            SSHMySQLDAO.getInstance(jdbcUrl, username, password).updateTheEarliestNextSynchronistation(ua, duration);
        } catch (SyncException ex) {
            java.util.logging.Logger.getLogger(SSHDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateNumberSynchronizationFailed(Synchronization ua, int number) {
        try {
            SSHMySQLDAO.getInstance(jdbcUrl, username, password).updateNumberSynchronizationFailed(ua, number);
        } catch (SyncException ex) {
            java.util.logging.Logger.getLogger(SSHDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public double getNbSecondFromConfigFile() {
        return ConfigFile.getInstance().getNbSecond();
    }

}
