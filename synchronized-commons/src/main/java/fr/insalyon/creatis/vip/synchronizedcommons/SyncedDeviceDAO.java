/*
Copyright 2015

CREATIS
CNRS UMR 5220 -- INSERM U1044 -- Université Lyon 1 -- INSA Lyon

Authors

Nouha Boujelben (nouha.boujelben@creatis.insa-lyon.fr)
Tristan Glatard (tristan.glatard@creatis.insa-lyon.fr)

This software is a daemon for file synchronization between SFTP
servers and the LCG File Catalog (LFC).

This software is governed by the CeCILL-B license under French law and
abiding by the rules of distribution of free software.  You can use,
modify and/ or redistribute the software under the terms of the
CeCILL-B license as circulated by CEA, CNRS and INRIA at the following
URL "http://www.cecill.info".

As a counterpart to the access to the source code and rights to copy,
modify and redistribute granted by the license, users are provided
only with a limited warranty and the software's author, the holder of
the economic rights, and the successive licensors have only limited
liability.

In this respect, the user's attention is drawn to the risks associated
with loading, using, modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean that it is complicated to manipulate, and that also
therefore means that it is reserved for developers and experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards
their requirements in conditions enabling the security of their
systems and/or data to be ensured and, more generally, to use and
operate it in the same conditions as regards security.

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-B license and that you accept its terms.
*/

package fr.insalyon.creatis.vip.synchronizedcommons;

import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import java.sql.Timestamp;
import java.util.List;

/**
 * DAO interface.
 *
 * @author Tristan Glatard, Nouha Boujelben
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
