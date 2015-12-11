/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
import fr.insalyon.creatis.vip.synchronizedcommons.TransferType;

/**
 * Bean object representing an SSH synchronization.
 *
 * @author Tristan Glatard,
 * @author Nouha Boujelben
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
     * @param transferType
     * @param userName
     * @param hostName
     * @param directoryName
     * @param port
     */
    public SSHSynchronization(String email, boolean validated, boolean authFailed, String syncedLFCDir, TransferType transferType, String userName, String hostName, String directoryName, int port, boolean deleteFilesFromSource,boolean checkFilesContent,
            int numberOfFilesTransferredToLFC, long sizeOfFilesTransferredToLFC, int numberOfFilesTransferredToDevice, long sizeOfFilesTransferredToDevice, int numberOfFilesDeletedInLFC, long sizeOfFilesDeletedInLFC, int numberOfFilesDeletedInDevice, long sizeOfFilesDeletedInDevice) {
        super(email, validated, authFailed, syncedLFCDir, transferType, deleteFilesFromSource,checkFilesContent, numberOfFilesTransferredToLFC, sizeOfFilesTransferredToLFC, numberOfFilesTransferredToDevice, sizeOfFilesTransferredToDevice, numberOfFilesDeletedInLFC, sizeOfFilesDeletedInLFC, numberOfFilesDeletedInDevice, sizeOfFilesDeletedInDevice);
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
