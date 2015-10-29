/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons.business;

import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDevice;
import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
import fr.insalyon.creatis.vip.synchronizedcommons.TransfertType;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Tristan Glatard, Nouha Boujelben
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
        int countFiles = 0; //this will count how many files are synced for a user
        HashMap<String, String> remoteFiles = sd.listFiles("/");
        HashMap<String, String> lfcFiles = lfcu.listLFCDir("/", s);
        if (lfcFiles == null || remoteFiles == null) {
            return;
        }
        TransfertType transfertType = s.getTransfertType();
        switch (transfertType) {
            case DeviceToLFC:
                //SyncedDevice -> LFC
                logger.info("transfert files from device to LFC for user" + s.toString());
                transfertFilesFromSynchDeviceToLFC(s, sd, remoteFiles, lfcFiles, countFiles, syncedLFCDir, s.getDeleteFilesfromSource());
                break;
            case LFCToDevice:
                //LFC->SyncedDevice 
                logger.info("transfert files from LFC to device for user" + s.toString());
                transfertFilesFromLFCToSynchDevice(s, remoteFiles, lfcFiles, countFiles, syncedLFCDir, s.getDeleteFilesfromSource());
                break;
            case Synchronization:
                //SyncedDevice -> LFC
                logger.info("transfert files from device to LFC for user" + s.toString());
                transfertFilesFromSynchDeviceToLFC(s, sd, remoteFiles, lfcFiles, countFiles, syncedLFCDir, s.getDeleteFilesfromSource());
                //LFC->SyncedDevice
                logger.info("transfert files from LFC to device for user" + s.toString());
                transfertFilesFromLFCToSynchDevice(s, remoteFiles, lfcFiles, countFiles, syncedLFCDir, s.getDeleteFilesfromSource());

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

    private void transfertFilesFromSynchDeviceToLFC(Synchronization s, SyncedDevice sd, HashMap<String, String> remoteFiles, HashMap<String, String> lfcFiles, int countFiles, String syncedLFCDir, boolean deleteFilesFromSource) throws SyncException {

        for (Map.Entry<String, String> p : remoteFiles.entrySet()) {
            if (countFiles < fileLimit) {
                String syncedShortPath = PathUtils.cleanse(p.getKey()), remoteRevision = p.getValue();
                String lfcRev = lfcFiles.get(syncedShortPath);

                if (lfcRev == null) {
                    //file is in SyncedDevice but not in LFC: copy to lfc
                    logger.info("==> (new file)" + String.format("%s - %s - %s -%s / %s", s.getEmail(), syncedShortPath, syncedLFCDir, countFiles, fileLimit));
                    if (!ignorePath(syncedShortPath)) {
                        copyToLFC(syncedShortPath, s, remoteRevision);
                        if (deleteFilesFromSource) {
                            sd.deleteFile(syncedShortPath);
                        }
                        countFiles++;
                    } else {
                        logger.info("Ignoring file" + syncedShortPath);
                    }
                } else {
                    if (!remoteRevision.equals(lfcRev)) {
                        //(A)
                        //revisions disagree: SyncedDevice must be right because LFC doesn't generate revisions (files in a synchronied LFC directory cannot be modified).
                        logger.info(String.format("==x> (SyncedDevice:%s;LFC:%s) (%s) %s %s file count at this iteration is %s/%s", remoteRevision, lfcRev, s.getEmail(), syncedShortPath, syncedLFCDir, countFiles, fileLimit));
                        lfcu.deleteFromLFC("/" + syncedShortPath, s);
                        if (!ignorePath(syncedShortPath)) {
                            copyToLFC(syncedShortPath, s, remoteRevision);
                            if (deleteFilesFromSource) {
                                sd.deleteFile(syncedShortPath);
                            }
                            lfcFiles.remove(syncedShortPath);
                            lfcFiles.put(syncedShortPath, remoteRevision);
                            countFiles++;
                        } else {
                            logger.info(String.format("Ignoring file %s", syncedShortPath));
                        }

                    } else {
                        //revisions are identical: do nothing
                        //logger.info("== "+boxPath+" is in sync in LFC");
                    }
                }
            } else {
                logger.info(String.format("Reached %s files for user %s at this iteration: skipping", countFiles, s.getEmail()));
            }
        }
    }

    private void transfertFilesFromLFCToSynchDevice(Synchronization s, HashMap<String, String> remoteFiles, HashMap<String, String> lfcFiles, int countFiles, String syncedLFCDir, boolean deleteFilesFromSource) throws SyncException {

        for (Map.Entry<String, String> q : lfcFiles.entrySet()) {

            if (countFiles < fileLimit && !ignorePath(q.getKey())) {

                String lfcPath = q.getKey(), lfcRev = q.getValue();
                String remoteRevision = remoteFiles.get(lfcPath);

                if (remoteRevision == null) {
                    //file is in LFC but not in SyncedDevice
                    if (lfcRev.equals("")) {
                        //if LFC file has no revision: copy to SyncedDevice
                        logger.info(String.format("<== (new file) / (%s) %s (%s/%s)", s.getEmail(), lfcPath, countFiles, fileLimit));
                        createLocalDir(PathUtils.getDirFromPath(PathUtils.getLocalPathFromLFCLong(PathUtils.getLFCLongFromLFCShort(lfcPath, s), sd)));
                        lfcu.getLFCFile(lfcPath, PathUtils.getDirFromPath(PathUtils.getLocalPathFromLFCLong(PathUtils.getLFCLongFromLFCShort(lfcPath, s), sd)), s);//  lfcPath, sd),ua);
                        sd.putFile(PathUtils.getLocalPathFromLFCLong(PathUtils.getLFCLongFromLFCShort(lfcPath, s), sd), PathUtils.getSyncShortFromLFCShort(lfcPath));//"/"+PathUtils.removeSyncedDir(PathUtils.getL(lfcPath, sd),ua));
                        lfcu.setRevision(PathUtils.getLFCLongFromLFCShort(lfcPath, s), sd.getRevision(PathUtils.getSyncShortFromLFCShort(lfcPath)));
                        if (deleteFilesFromSource) {
                            lfcu.deleteFromLFC(lfcPath, s);
                            logger.info(lfcPath + " was removed from LFC");
                        }
                        countFiles++;
                    } else {
                        //file has a revision in LFC: it used to be in SyncedDevice but was removed: remove from LFC.
                        logger.info(String.format("==x (%s) %s/%s (%s/%s)", s.getEmail(), syncedLFCDir, lfcPath, countFiles, fileLimit));
                        lfcu.deleteFromLFC("/" + lfcPath, s);
                        countFiles++;
                    }

                } else {
                    if (lfcRev.equals("")) { //rev "" means the file is there but has no rev. It was never synced with the SyncedDevice.
                        //file is in LFC with no rev and it is also in SyncedDevice. Something wrong must have happened. Assumes syncedDevice is right.
                        logger.info(String.format("==x> (no revision in LFC) (%s) %s %s (%s/%s)", s.getEmail(), lfcPath, syncedLFCDir, countFiles, fileLimit));
                        lfcu.deleteFromLFC("/" + lfcPath, s);
                        copyToLFC(PathUtils.getSyncShortFromLFCShort(lfcPath), s, remoteRevision);
                        countFiles++;
                    } else {
                        if (!lfcRev.equals(remoteRevision)) {
                            //revisions disagree: do nothing, it should have been handled in (A)
                            logger.info(String.format("oops (SyncedDevice:%s;LFC:%s) (%s) %s: this is not supposed to happen at this point...", remoteRevision, lfcRev, s.getEmail(), lfcPath));
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
    }

}
