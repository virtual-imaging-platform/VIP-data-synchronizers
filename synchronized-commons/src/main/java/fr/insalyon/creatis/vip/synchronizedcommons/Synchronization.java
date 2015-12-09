/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons;

/**
 * Bean that represents a synchronization.
 *
 * @author Tristan Glatard,
 * @author Nouha Boujelben
 */
public class Synchronization {

    private final String email;
    private boolean validated;
    private final boolean synchFailed;
    private final String syncedLFCDir;
    private final TransferType transferType;
    private final boolean deleteFilesfromSource;
    private int numberOfFilesTransferredToLFC;
    private long sizeOfFilesTransferredToLFC;
    private int numberOfFilesTransferredToDevice;
    private long sizeOfFilesTransferredToDevice;
    private int numberOfFilesDeletedInLFC;
    private long sizeOfFilesDeletedInLFC;
    private int numberOfFilesDeletedInDevice;
    private long sizeOfFilesDeletedInDevice;

    public Synchronization(String email, boolean validated, boolean synchFailed, String syncedLFCDir, TransferType transferType, boolean deleteFilesfromSource, int numberOfFilesTransferredToLFC, long sizeOfFilesTransferredToLFC, int numberOfFilesTransferredToDevice, long sizeOfFilesTransferredToDevice, int numberOfFilesDeletedInLFC, long sizeOfFilesDeletedInLFC, int numberOfFilesDeletedInDevice, long sizeOfFilesDeletedInDevice) {
        this.email = email;
        this.validated = validated;
        this.syncedLFCDir = syncedLFCDir;
        this.synchFailed = synchFailed;
        this.transferType = transferType;
        this.deleteFilesfromSource = deleteFilesfromSource;
        this.numberOfFilesTransferredToLFC = numberOfFilesTransferredToLFC;
        this.sizeOfFilesTransferredToLFC = sizeOfFilesTransferredToLFC;
        this.numberOfFilesTransferredToDevice = numberOfFilesTransferredToDevice;
        this.sizeOfFilesTransferredToDevice = sizeOfFilesTransferredToDevice;
        this.numberOfFilesDeletedInLFC = numberOfFilesDeletedInLFC;
        this.sizeOfFilesDeletedInLFC = sizeOfFilesDeletedInLFC;
        this.numberOfFilesDeletedInDevice = numberOfFilesDeletedInDevice;
        this.sizeOfFilesDeletedInDevice = sizeOfFilesDeletedInDevice;

    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getEmail() {
        return email;
    }

    public boolean isValidated() {
        return validated;
    }

    public String getSyncedLFCDir() {
        return syncedLFCDir;
    }

    public boolean getSynchronizationFailed() {
        return synchFailed;
    }

    public TransferType getTransferType() {
        return transferType;
    }

    public boolean getDeleteFilesfromSource() {
        return deleteFilesfromSource;
    }

    public int getNumberOfFilesTransferredToLFC() {
        return numberOfFilesTransferredToLFC;
    }

    public void setNumberOfFilesTransferredToLFC(int numberOfFilesTransferredToLFC) {
        this.numberOfFilesTransferredToLFC = numberOfFilesTransferredToLFC;
    }

    public long getSizeOfFilesTransferredToLFC() {
        return sizeOfFilesTransferredToLFC;
    }

    public void setSizeOfFilesTransferredToLFC(long sizeOfFilesTransferredToLFC) {
        this.sizeOfFilesTransferredToLFC = sizeOfFilesTransferredToLFC;
    }

    public int getNumberOfFilesTransferredToDevice() {
        return numberOfFilesTransferredToDevice;
    }

    public void setNumberOfFilesTransferredToDevice(int numberOfFilesTransferredToDevice) {
        this.numberOfFilesTransferredToDevice = numberOfFilesTransferredToDevice;
    }

    public long getSizeOfFilesTransferredToDevice() {
        return sizeOfFilesTransferredToDevice;
    }

    public void setSizeOfFilesTransferredToDevice(long sizeOfFilesTransferredToDevice) {
        this.sizeOfFilesTransferredToDevice = sizeOfFilesTransferredToDevice;
    }

    public int getNumberOfFilesDeletedInLFC() {
        return numberOfFilesDeletedInLFC;
    }

    public void setNumberOfFilesDeletedInLFC(int numberOfFilesDeletedInLFC) {
        this.numberOfFilesDeletedInLFC = numberOfFilesDeletedInLFC;
    }

    public long getSizeOfFilesDeletedInLFC() {
        return sizeOfFilesDeletedInLFC;
    }

    public void setSizeOfFilesDeletedInLFC(long sizeOfFilesDeletedInLFC) {
        this.sizeOfFilesDeletedInLFC = sizeOfFilesDeletedInLFC;
    }

    public int getNumberOfFilesDeletedInDevice() {
        return numberOfFilesDeletedInDevice;
    }

    public void setNumberOfFilesDeletedInDevice(int numberOfFilesDeletedInDevice) {
        this.numberOfFilesDeletedInDevice = numberOfFilesDeletedInDevice;
    }

    public long getSizeOfFilesDeletedInDevice() {
        return sizeOfFilesDeletedInDevice;
    }

    public void setSizeOfFilesDeletedInDevice(long sizeOfFilesDeletedInDevice) {
        this.sizeOfFilesDeletedInDevice = sizeOfFilesDeletedInDevice;
    }

    @Override
    public String toString() {
        return "{" + email + " ; " + syncedLFCDir + "}";
    }

}
