/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons;

import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import java.sql.Timestamp;
import java.util.List;

/**
 * DAO interface.
 *
 * @author Tristan Glatard
 * @author Nouha Boujelben
 */
public interface SyncedDeviceDAO {

    /**
     * Sets the 'validated' flag for a synchronization.
     *
     * @param s
     * @throws SyncException
     */
    public void validateSynchronization(Synchronization s) throws SyncException;

    /**
     * Sets the 'synchronizationfailed' flag for a synchronization.
     *
     * @param s
     * @throws SyncException
     */
    public void setSynchronizationFailed(Synchronization s) throws SyncException;

    /**
     * Sets the 'synchronisationfailed' flag for a synchronization.
     *
     * @param s
     * @throws SyncException
     */
    public void setSynchronizationNotFailed(Synchronization s) throws SyncException;

    /**
     * Gets all the synchronizations.
     *
     * @return
     * @throws SyncException
     */
    public List<Synchronization> getActiveSynchronizations() throws SyncException;

    /**
     * update the TheEarliestNextSynchronistation
     *
     * @param ua
     * @param duration
     * @throws SyncException
     */
    public void updateTheEarliestNextSynchronistation(Synchronization ua, long duration) throws SyncException;

    /**
     *
     * @return theEarliestNextSynchronistation
     * @param ua
     * @throws SyncException
     */
    public Timestamp getTheEarliestNextSynchronistation(Synchronization ua) throws SyncException;

    /**
     * @return false if theEarliestNextSynchronization is before or equal to the
     * current date
     * @param ua
     * @throws SyncException
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

}
