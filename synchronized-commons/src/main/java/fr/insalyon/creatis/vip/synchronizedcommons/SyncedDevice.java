/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons;

import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import java.util.HashMap;
import java.util.List;

/**
 * Interface for synchronized devices.
 *
 * @author Tristan Glatard, Nouha Boujelben
 */
public interface SyncedDevice {

    /**
     * Lists all the files in a remote directory, with their revisions.
     *
     * @param remoteDir short path of the remote directory.
     * @return
     * @throws SyncException
     */
    public HashMap<String, String> listFiles(String remoteDir) throws SyncException;

    /**
     * Copies a remote file to a local directory.
     *
     * @param remoteFile short path of the remote file
     * @param localDir local directory where to copy the file
     * @throws SyncException
     */
    public void getFile(String remoteFile, String localDir) throws SyncException;

    /**
     * delete a remote file.
     *
     * @param remoteFile short path of the remote file
     * @throws SyncException
     */
    public void deleteFile(String remoteFile) throws SyncException;

    /**
     * Copies a local file to a remote directory.
     *
     * @param localFile absolute path of the local file.
     * @param remoteDir short path of the remote directory.
     * @throws SyncException
     */
    public void putFile(String localFile, String remoteDir) throws SyncException;

    /**
     * Sets the 'Synchfailed' flag for the synchronization.
     *
     * @param s
     * @throws SyncException
     */
    public void setSynchronizationFailed(Synchronization s) throws SyncException;

    /**
     * Sets the 'SynchNotfailed' flag for the synchronization.
     *
     * @param s
     * @throws SyncException
     */
    public void setSynchronizationNotFailed(Synchronization s) throws SyncException;

    /**
     * Sets the 'validated' flag for the synchronization.
     *
     * @param s
     * @throws SyncException
     */
    public void validateSynchronization(Synchronization s) throws SyncException;

    /**
     * Gets all the synchronizations on this device.
     *
     * @return
     * @throws SyncException
     */
    public List<Synchronization> getActiveSynchronization() throws SyncException;

    /**
     * Gets the revision of a remote file.
     *
     * @param remoteFile short path of the remote file.
     * @return
     * @throws SyncException
     */
    public String getRevision(String remoteFile) throws SyncException;

    /**
     * Sets the synchronization to use.
     *
     * @param s
     */
    public void setSynchronization(Synchronization s) throws SyncException;

    /**
     * The temporary directory where to store files locally.
     *
     * @return
     */
    public String getTempDir() throws SyncException;

    /**
     * A string contained in the "authentication failed" error message.
     *
     * @return
     */
    public String getAuthFailedString() throws SyncException;

    /**
     *
     * @param ua
     * @return false if theEarliestNextSynchronization is before or equal to the
     * current date
     */
    public boolean isMustWaitBeforeNextSynchronization(Synchronization ua) throws SyncException;

    /**
     *
     * @param ua
     * @return the number of failed Synchronization
     */
    public int getNumberSynchronizationFailed(Synchronization ua) throws SyncException;

    /**
     *
     * @param ua
     * @param numberSynchFailed set the number of failed Synchronization
     */
    public void updateNumberSynchronizationFailed(Synchronization ua, int numberSynchFailed) throws SyncException;

    /**
     *
     * @param ua
     * @param duration the time will added to the current date to update the
     * "theEarliestNextSynchronization"
     */
    public void updateTheEarliestNextSynchronization(Synchronization ua, long duration) throws SyncException;

    /**
     *
     * @return The slot time used for the exponential back-off algorithm
     */
    public double getNbSecondFromConfigFile() throws SyncException;

}
