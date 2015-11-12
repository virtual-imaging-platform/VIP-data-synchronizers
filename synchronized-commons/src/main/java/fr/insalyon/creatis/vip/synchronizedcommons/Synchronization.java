/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons;

/**
 * Bean that represents a synchronization.
 *
 * @author Tristan Glatard, Nouha Boujelben
 */
public class Synchronization {

    private final String email;
    private boolean validated;
    private final boolean synchFailed;
    private final String syncedLFCDir;
    private final TransferType transferType;
    private final boolean deleteFilesfromSource;

    public Synchronization(String email, boolean validated, boolean synchFailed, String syncedLFCDir, TransferType transferType, boolean deleteFilesfromSource) {
        this.email = email;
        this.validated = validated;
        this.syncedLFCDir = syncedLFCDir;
        this.synchFailed = synchFailed;
        this.transferType = transferType;
        this.deleteFilesfromSource = deleteFilesfromSource;

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

    @Override
    public String toString() {
        return "{" + email + " ; " + syncedLFCDir + "}";
    }

}
