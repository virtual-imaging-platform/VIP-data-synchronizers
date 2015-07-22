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
    private final TransfertType transfertType;
    private final boolean deleteFilesfromSource;

    public Synchronization(String email, boolean validated, boolean synchFailed, String syncedLFCDir, TransfertType transfertType, boolean deleteFilesfromSource) {
        this.email = email;
        this.validated = validated;
        this.syncedLFCDir = syncedLFCDir;
        this.synchFailed = synchFailed;
        this.transfertType = transfertType;
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

    public TransfertType getTransfertType() {
        return transfertType;
    }

    public boolean getDeleteFilesfromSource() {
        return deleteFilesfromSource;
    }

    @Override
    public String toString() {
        return "{" + email + " ; " + syncedLFCDir + "}";
    }

}
