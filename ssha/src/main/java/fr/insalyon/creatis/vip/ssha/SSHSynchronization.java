/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;

/**
 * Bean object representing an SSH synchronization.
 * @author Tristan Glatard
 */
public class SSHSynchronization extends Synchronization {
    
    private final String sshUserName;
    private final String sshHostName;
    private final String sshDirectoryName;
    private final int port;
    
    public SSHSynchronization(String email, boolean validated, boolean authFailed, String syncedLFCDir,String userName, String hostName, String directoryName, int port){
        super(email,validated,authFailed,syncedLFCDir);
        this.sshUserName = userName;
        this.sshHostName = hostName;
        this.sshDirectoryName = directoryName;
        this.port = port;
        }

    public String getSSHUserName() {
        return sshUserName;
    }

    public String getSSHHostName() {
        return sshHostName;
    }

    public String getSSHDirectoryName() {
        return sshDirectoryName;
    }

    public int getSSHPort() {
        return port;
    }
}
