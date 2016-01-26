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
import java.util.HashMap;
import java.util.List;

/**
 * Interface for synchronized devices.
 *
 * @author Tristan Glatard,
 * @author Nouha Boujelben
 */
public interface SyncedDevice {

    /**
     * Lists all the files in a remote directory, with their revisions.
     *
     * @param remoteDir short path of the remote directory.
     * @return
     * @throws SyncException
     */
    public HashMap<String, FileProperties> listFiles(String remoteDir, Synchronization synchronization) throws SyncException;

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
    public String getRevision(String remoteFile, Synchronization synchronization) throws SyncException;

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

    public void updateLFCMonitoringParams(Synchronization ua, int numberOfFilesTransferredToLFC, long sizeOfFilesTransferredToLFC, int numberOfFilesDeletedInLFC, long sizeOfFilesDeletedInLFC) throws SyncException;

    public void updateDeviceMonitoringParams(Synchronization ua, int numberOfFilesTransferredToDevice, long sizeOfFilesTransferredToDevice, int numberOfFilesDeletedInDevice, long sizeOfFilesDeletedInDevice) throws SyncException;

}
