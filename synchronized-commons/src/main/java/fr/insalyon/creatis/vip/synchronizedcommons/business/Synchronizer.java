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
package fr.insalyon.creatis.vip.synchronizedcommons.business;

import fr.insalyon.creatis.vip.synchronizedcommons.FileProperties;
import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDevice;
import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
import fr.insalyon.creatis.vip.synchronizedcommons.TransferType;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author Tristan Glatard,
 * @author Nouha Boujelben
 *
 * A Synchronizer is a synchronization agent between an EGI Logical File Catalog
 * and another directory accessible through SSH or Dropbox, specified by the
 * SyncedDevice attribute. Files in a synchronized LFC directory cannot be
 * modified or deleted.
 */
public class Synchronizer extends Thread {

    private final SyncedDevice sd;
    private final int fileLimit;
    private final long sleepTimeMillis;
    private final LFCUtils lfcu;        // an object containing methods to transfer files to the synchronized LFC. 
    private static final Logger logger = Logger.getLogger(Synchronizer.class);

    /**
     * A constructor.
     *
     * @param sd the device that will be synchronized with the LFC
     * @param gridaHost the GRIDA host configured for the LFC
     * @param gridaPort the GRIDA port configured for the LFC
     * @param gridaProxy the proxy to access the LFC
     * @param fileLimit the maximal number of files to transfer at each
     * iteration of the synchronization loop.
     * @param sleepTimeMillis the time in milliseconds to sleep between two
     * synchronization loops.
     */
    public Synchronizer(SyncedDevice sd, String gridaHost, int gridaPort, String gridaProxy, int fileLimit, long sleepTimeMillis) {
        this.sd = sd;
        this.fileLimit = fileLimit;
        this.sleepTimeMillis = sleepTimeMillis;
        lfcu = new LFCUtils(gridaHost, gridaPort, gridaProxy);
    }

    /**
     * Creates a directory on the local file system.
     *
     * @param localDir the directory to create.
     * @throws SyncException
     */
    private void createLocalDir(String localDir) throws SyncException {

        File dir = new File(localDir.replaceAll("//", "/"));
        boolean successful = dir.mkdirs();
        if (dir != null && !dir.exists()) {
            if (!successful) {
                throw new SyncException("Cannot create local directory " + dir.getAbsolutePath());
            }
        }
    }

    /**
     * Main loop. Lists all the active synchronizations and triggers them.
     */
    @Override
    public void run() {
        List<Synchronization> synchronizations = null;

        while (true) {

            try {
                synchronizations = sd.getActiveSynchronization();
            } catch (SyncException ex) {
                logger.error("Cannot get user accounts: " + ex.getMessage());
            }

            for (Synchronization s : synchronizations) {
                try {
                    if (s.isValidated()) {
                        //if the Synchronization not failed restart the synchronization
                        if (!s.getSynchronizationFailed()) {
                            doSync(s);
                            //if synchronization failed 
                        } else if (s.getSynchronizationFailed()) {
                            //check the date of the earliest next synchronization and if it is after the current date this method return true
                            if (!sd.isMustWaitBeforeNextSynchronization(s)) {
                                //update the earlisNextSynchronisation with the ExponentialBackoff algorithm
                                updateExponentialBackoff(sd, s);
                                doSync(s);
                                //if the synchronization passed, set the number of failed synchronization to zero
                                sd.updateNumberSynchronizationFailed(s, 0);
                                //if the synchronization passed, set the synchronisation to not failed
                                sd.setSynchronizationNotFailed(s);
                            }
                        }
                    }
                } catch (SyncException ex) {
                    try {
                        sd.updateNumberSynchronizationFailed(s, sd.getNumberSynchronizationFailed(s) + 1);
                    } catch (SyncException ex1) {
                        logger.error("Cannot update NumberSynchronizationFailed " + ex1.getMessage());
                    }
                    try {
                        sd.setSynchronizationFailed(s);
                    } catch (SyncException ex2) {
                        logger.error("Cannot mark failed Synchronization for user " + s.toString() + ex2.getMessage());
                    }
                    logger.error("Problem synchronizing user account: " + s.toString() + ex.getMessage());
                }
            }

            try {
                Thread.sleep(this.sleepTimeMillis);
            } catch (InterruptedException ex) {
                logger.error(ex);
            }
        }
    }

    /**
     * Main synchronization method.
     *
     * @param s synchronization to synchronize.
     * @throws SyncException
     */
    private void doSync(Synchronization s) throws SyncException {
        sd.setSynchronization(s);
        String syncedLFCDir = s.getSyncedLFCDir();
        //this will count how many files are synced for a user
        int countFiles = 0;//this will count how many files are synced for a user
        String message = "Listing files for synchronization " + s.toString();
        if(s.isCheckFilesContent())
            message += " (this may take some time because file checksums are verified).";
        logger.info(message);
        HashMap<String, FileProperties> remoteFiles = sd.listFiles("/", s);
        HashMap<String, FileProperties> lfcFiles = lfcu.listLFCDir("/", s);
        if (lfcFiles == null || remoteFiles == null) {
            return;
        }
        TransferType transferType = s.getTransferType();
        switch (transferType) {
            case DeviceToLFC:
                //SyncedDevice -> LFC
                logger.info("Transfer files from device to LFC for user" + s.toString());
                transferFilesFromSynchDeviceToLFC(s, sd, remoteFiles, lfcFiles, syncedLFCDir, s.getDeleteFilesfromSource());
                break;
            case LFCToDevice:
                //LFC->SyncedDevice 
                logger.info("Transfer files from LFC to device for user" + s.toString());
                transferFilesFromLFCToSynchDevice(s, remoteFiles, lfcFiles, countFiles, syncedLFCDir, s.getDeleteFilesfromSource(), true);
                break;
            case Synchronization:
                //SyncedDevice -> LFC
                logger.info("Transfer files from device to LFC for user" + s.toString());
                transferFilesFromSynchDeviceToLFC(s, sd, remoteFiles, lfcFiles, syncedLFCDir, false);
                //LFC->SyncedDevice
                logger.info("Transfer files from LFC to device for user" + s.toString());
                transferFilesFromLFCToSynchDevice(s, remoteFiles, lfcFiles, countFiles, syncedLFCDir, s.getDeleteFilesfromSource(), false);
                break;
        }
    }

    /**
     * Copies a file from the SyncedDevice to the LFC.
     *
     * @param syncedShortPath the short path of the file in the SyncedDevice
     * @param s the synchronization
     * @param revision the revision of the file in the SyncedDevice
     * @throws SyncException
     */
    private void copyToLFC(String syncedShortPath, Synchronization s, String revision) throws SyncException {
        createLocalDir(PathUtils.getDirFromPath(PathUtils.getLocalPathFromSyncShort(syncedShortPath, sd, s)));
        sd.getFile(syncedShortPath, PathUtils.getDirFromPath(PathUtils.getLocalPathFromSyncShort(syncedShortPath, sd, s)));
        lfcu.copyToLfc(PathUtils.getLocalPathFromSyncShort(syncedShortPath, sd, s), PathUtils.getDirFromPath(PathUtils.getLFCLongFromSyncShort(syncedShortPath, s)));
        lfcu.setRevision(PathUtils.getLFCLongFromSyncShort(syncedShortPath, s), revision);
    }

    /**
     * Returns true if the file path has to be ignored.
     *
     * @param path file path to test
     * @return
     */
    boolean ignorePath(String path) {
        File f = new File(path);
        if (f.getName().startsWith(".")) {
            return true;
        }
        return false;
    }

    private void updateExponentialBackoff(SyncedDevice sd, Synchronization ua) throws SyncException {
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        sd.updateTheEarliestNextSynchronization(ua, (long) (currentTimestamp.getTime() + Math.pow(2, sd.getNumberSynchronizationFailed(ua)) * 1000 * sd.getNbSecondFromConfigFile()));

    }

    private void transferFilesFromSynchDeviceToLFC(Synchronization s, SyncedDevice sd, HashMap<String, FileProperties> remoteFiles, HashMap<String, FileProperties> lfcFiles, String syncedLFCDir, boolean deleteFilesFromSource) throws SyncException {
        int numberOfFilesTransferredToLFC = 0;
        long sizeOfFilesTransferredToLFC = 0;
        int numberOfFilesDeletedInLFC = 0;
        long sizeOfFilesDeletedInLFC = 0;
        for (Map.Entry<String, FileProperties> p : remoteFiles.entrySet()) {
            if (numberOfFilesTransferredToLFC < fileLimit) {
                String syncedShortPath = PathUtils.cleanse(p.getKey()), remoteRevision = p.getValue().getMd5sum();
                String lfcRev = null;
                if (lfcFiles.get(syncedShortPath) != null) {
                    lfcRev = lfcFiles.get(syncedShortPath).getMd5sum();
                }
                if (lfcRev == null) {
                    //file is in SyncedDevice but not in LFC: copy to lfc
                    logger.info("==> (New file)" + String.format("%s - %s - %s -%s / %s", s.getEmail(), syncedShortPath, syncedLFCDir, numberOfFilesTransferredToLFC, fileLimit));

                    if (!ignorePath(syncedShortPath)) {
                        copyToLFC(syncedShortPath, s, remoteRevision);
                        numberOfFilesTransferredToLFC++;
                        sizeOfFilesTransferredToLFC += p.getValue().getSize();
                        if (deleteFilesFromSource) {
                            sd.deleteFile(syncedShortPath);
                        }

                    } else {
                        logger.info("Ignoring file" + syncedShortPath);
                    }
                } else {
                    if (!remoteRevision.equals(lfcRev)) {
                        //(A)
                        //revisions disagree: SyncedDevice must be right because LFC doesn't generate revisions (files in a synchronied LFC directory cannot be modified).
                        logger.info(String.format("==x> (SyncedDevice:%s;LFC:%s) (%s) %s %s file count at this iteration is %s/%s", remoteRevision, lfcRev, s.getEmail(), syncedShortPath, syncedLFCDir, numberOfFilesTransferredToLFC, fileLimit));
                        lfcu.deleteFromLFC("/" + syncedShortPath, s);
                        numberOfFilesDeletedInLFC++;
                        sizeOfFilesDeletedInLFC += p.getValue().getSize();
                        if (!ignorePath(syncedShortPath)) {
                            copyToLFC(syncedShortPath, s, remoteRevision);
                            numberOfFilesTransferredToLFC++;
                            sizeOfFilesTransferredToLFC += p.getValue().getSize();
                            if (deleteFilesFromSource) {
                                sd.deleteFile(syncedShortPath);
                            }
                            lfcFiles.remove(syncedShortPath);
                            lfcFiles.put(syncedShortPath, new FileProperties(p.getValue().getSize(), remoteRevision));

                        } else {
                            logger.info(String.format("Ignoring file %s", syncedShortPath));
                        }

                    } else {
                        //revisions are identical: do nothing
                    }
                }
            } else {
                logger.info(String.format("Reached %s files for user %s at this iteration: skipping", numberOfFilesTransferredToLFC, s.getEmail()));
            }
        }
        sd.updateLFCMonitoringParams(s, numberOfFilesTransferredToLFC, sizeOfFilesTransferredToLFC, numberOfFilesDeletedInLFC, sizeOfFilesDeletedInLFC);

    }

    /**
     *
     * @param s
     * @param remoteFiles
     * @param lfcFiles
     * @param countFiles
     * @param syncedLFCDir
     * @param deleteFilesFromSource
     * @param checkFilesContent if it's true , calcule md5sum to check file
     * content
     * @param LFCIsRight Assumes LFC is right when detect modified files between
     * the source and the destination device.
     * @throws SyncException
     */
    private void transferFilesFromLFCToSynchDevice(Synchronization s, HashMap<String, FileProperties> remoteFiles, HashMap<String, FileProperties> lfcFiles,
            int countFiles, String syncedLFCDir, boolean deleteFilesFromSource, boolean LFCIsRight) throws SyncException {
        int numberOfFilesTransferredToDevice = 0;
        long sizeOfFilesTransferredToDevice = 0;
        int numberOfFilesDeletedInLFC = 0;
        long sizeOfFilesDeletedInLFC = 0;
        int numberOfFilesTransferredToLFC = 0;
        long sizeOfFilesTransferredToLFC = 0;
        int numberOfFilesDeletedInDevice = 0;
        long sizeOfFilesDeletedInDevice = 0;
        for (Map.Entry<String, FileProperties> q : lfcFiles.entrySet()) {

            if (countFiles < fileLimit && !ignorePath(q.getKey())) {

                String lfcPath = q.getKey(), lfcRev = q.getValue().getMd5sum();
                String remoteRevision;
                if (remoteFiles.get(lfcPath) != null) {
                    remoteRevision = remoteFiles.get(lfcPath).getMd5sum();
                } else {
                    remoteRevision = null;
                }

                if (remoteRevision == null) {
                    //file is in LFC but not in SyncedDevice
                    if (lfcRev.equals("") || LFCIsRight) {
                        //if LFC file has no revision: copy to SyncedDevice
                        logger.info(String.format("<== (new file) / (%s) %s (%s/%s)", s.getEmail(), lfcPath, countFiles, fileLimit));
                        copyFileFromLFCToDevice(s, lfcPath, deleteFilesFromSource, countFiles, q);
                        numberOfFilesTransferredToDevice++;
                        sizeOfFilesTransferredToDevice += q.getValue().getSize();

                    } else {
                        //file has a revision in LFC: it used to be in SyncedDevice but was removed: remove from LFCIsRight.
                        logger.info(String.format("==x (%s) %s/%s (%s/%s)", s.getEmail(), syncedLFCDir, lfcPath, countFiles, fileLimit));
                        lfcu.deleteFromLFC("/" + lfcPath, s);
                        numberOfFilesDeletedInLFC++;
                        sizeOfFilesDeletedInLFC += q.getValue().getSize();
                        countFiles++;
                    }

                } else {
                    if (lfcRev.equals("") && !LFCIsRight) { //rev "" means the file is there but has no rev. It was never synced with the SyncedDevice.
                        //file is in LFC with no rev and it is also in SyncedDevice. Something wrong must have happened. Assumes syncedDevice is right.
                        logger.info(String.format("==x> (no revision in LFC) (%s) %s %s (%s/%s)", s.getEmail(), lfcPath, syncedLFCDir, countFiles, fileLimit));
                        lfcu.deleteFromLFC("/" + lfcPath, s);
                        numberOfFilesDeletedInLFC++;
                        sizeOfFilesDeletedInLFC += q.getValue().getSize();
                        copyToLFC(PathUtils.getSyncShortFromLFCShort(lfcPath), s, remoteRevision);
                        numberOfFilesTransferredToLFC++;
                        sizeOfFilesTransferredToLFC += remoteFiles.get(lfcPath).getSize();
                        countFiles++;
                    } else {
                        if (!lfcRev.equals(remoteRevision)) {
                            //revisions disagree: if it's LFCIsRight and checkFilesContent so assumes LFC is right
                            if (LFCIsRight) {//
                                //delete file from device 
                                sd.deleteFile(PathUtils.cleanse(lfcPath));
                                numberOfFilesDeletedInDevice++;
                                sizeOfFilesDeletedInDevice += remoteFiles.get(lfcPath).getSize();
                                //transfer file from lfc to device
                                copyFileFromLFCToDevice(s, lfcPath, deleteFilesFromSource, countFiles, q);
                                numberOfFilesTransferredToDevice++;
                                sizeOfFilesTransferredToDevice += q.getValue().getSize();

                            } else {
                                //revisions disagree: do nothing, it should have been handled in (A)
                                logger.info(String.format("oops (SyncedDevice:%s;LFC:%s) (%s) %s: this is not supposed to happen at this point...", remoteRevision, lfcRev, s.getEmail(), lfcPath));
                            }
                        } else {
                            //revisions are the same: do nothing
                            //logger.info("== "+lfcPath+" is in sync in Dropbox");
                        }
                    }
                }
            } else {
                if (ignorePath(q.getKey())) {
                    logger.info(String.format("Ignoring file %s", q.getKey()));
                } else {
                    logger.info(String.format("Reached %s files for user %s at this iteration: skipping", countFiles, s.getEmail()));
                }
            }
        }
        sd.updateLFCMonitoringParams(s, numberOfFilesTransferredToLFC, sizeOfFilesTransferredToLFC, numberOfFilesDeletedInLFC, sizeOfFilesDeletedInLFC);
        sd.updateDeviceMonitoringParams(s, numberOfFilesTransferredToDevice, sizeOfFilesTransferredToDevice, numberOfFilesDeletedInDevice, sizeOfFilesDeletedInDevice);

    }

    private void copyFileFromLFCToDevice(Synchronization s, String lfcPath, boolean deleteFilesFromSource, int countFiles, Map.Entry<String, FileProperties> q) throws SyncException {

        createLocalDir(PathUtils.getDirFromPath(PathUtils.getLocalPathFromLFCLong(PathUtils.getLFCLongFromLFCShort(lfcPath, s), sd)));
        lfcu.getLFCFile(lfcPath, PathUtils.getDirFromPath(PathUtils.getLocalPathFromLFCLong(PathUtils.getLFCLongFromLFCShort(lfcPath, s), sd)), s);//  lfcPath, sd),ua);
        sd.putFile(PathUtils.getLocalPathFromLFCLong(PathUtils.getLFCLongFromLFCShort(lfcPath, s), sd), PathUtils.getSyncShortFromLFCShort(lfcPath));//"/"+PathUtils.removeSyncedDir(PathUtils.getL(lfcPath, sd),ua));
        lfcu.setRevision(PathUtils.getLFCLongFromLFCShort(lfcPath, s), sd.getRevision(PathUtils.getSyncShortFromLFCShort(lfcPath), s));
        if (deleteFilesFromSource) {
            lfcu.deleteFromLFC(lfcPath, s);
            logger.info(lfcPath + " was removed from LFC");
        }
        countFiles++;

    }

    public LFCUtils getLfcu() {
        return lfcu;
    }

}
