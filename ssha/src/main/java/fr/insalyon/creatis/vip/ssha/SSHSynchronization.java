/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;

/**
 * Bean object representing an SSH synchronization.
 *
 * @author Tristan Glatard, Nouha Boujelben
 */
public class SSHSynchronization extends Synchronization {

    private final String sshUserName;
    private final String sshHostName;
    private final String sshDirectoryName;
    private final int port;
   

    /**
     *
     * @param email
     * @param validated
     * @param authFailed
     * @param syncedLFCDir
     * @param userName
     * @param hostName
     * @param directoryName
     * @param port
     */
    public SSHSynchronization(String email, boolean validated, boolean authFailed, String syncedLFCDir,String transfertType, String userName, String hostName, String directoryName, int port) {
        super(email, validated, authFailed, syncedLFCDir,transfertType);
        this.sshUserName = userName;
        this.sshHostName = hostName;
        this.sshDirectoryName = directoryName;
        this.port = port;
    }

    /**
     *
     * @return
     */
    public String getSSHUserName() {
        return sshUserName;
    }

    /**
     *
     * @return
     */
    public String getSSHHostName() {
        return sshHostName;
    }

    /**
     *
     * @return
     */
    public String getSSHDirectoryName() {
        return sshDirectoryName;
    }

    /**
     *
     * @return
     */
    public int getSSHPort() {
        return port;
    }
}
