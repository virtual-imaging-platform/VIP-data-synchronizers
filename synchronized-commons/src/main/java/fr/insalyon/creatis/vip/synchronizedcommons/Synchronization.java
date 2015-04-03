/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons;

/**
 * Bean that represents a synchronization.
 *
 * @author Tristan Glatard
 */
public class Synchronization {

    private final String email;
    private boolean validated;
    private final boolean authFailed;
    private final String syncedLFCDir;

    public Synchronization(String email, boolean validated, boolean authFailed, String syncedLFCDir) {
        this.email = email;
        this.validated = validated;
        this.syncedLFCDir = syncedLFCDir;
        this.authFailed = authFailed;
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

    public boolean getAuthFailed() {
        return authFailed;
    }

    @Override
    public String toString() {
        return "{" + email + " ; " + syncedLFCDir + "}";
    }

}
