/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons.business;

import fr.insalyon.creatis.grida.client.GRIDAClient;
import fr.insalyon.creatis.grida.client.GRIDAClientException;
import fr.insalyon.creatis.grida.common.bean.GridData;
import fr.insalyon.creatis.vip.synchronizedcommons.FileProperties;
import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * A utility class to interact with the LFC.
 *
 * @author Tristan Glatard
 * @author Nouha Boujelben
 */
public class LFCUtils {

    private final String gridaHost;
    private final int gridaPort;
    private final String gridaProxy;

    private final GRIDAClient gc; // will be used for all operations

    private static final Logger logger = Logger.getLogger(LFCUtils.class);

    /**
     * Constructor.
     *
     * @param gridaHost the host of the GRIDA server configured for the LFC.
     * @param gridaPort the port of the GRIDA server configured for the LFC.
     * @param gridaProxy the proxy to be used.
     *
     */
    public LFCUtils(String gridaHost, int gridaPort, String gridaProxy) {
        this.gridaHost = gridaHost;
        this.gridaPort = gridaPort;
        this.gridaProxy = gridaProxy;
        gc = new GRIDAClient(gridaHost, gridaPort, gridaProxy);
    }

    /**
     * Recursively lists a directory of the LFC.
     *
     * @param path short path of the directory to list
     * @param s synchronization
     * @return a map of <key,value> where key is a short LFC path, and value is
     * the revision of key.
     * @throws SyncException
     */
    public HashMap<String, FileProperties> listLFCDir(String path, Synchronization s) throws SyncException {

        HashMap<String, FileProperties> entries = new HashMap<String, FileProperties>();
        String longPath = PathUtils.getLFCLongFromLFCShort(path, s);
        List<GridData> gds = null;
        try {
            gds = gc.getFolderData(longPath, true, true);

        } catch (GRIDAClientException e) {
            throw new SyncException(e);
        }
        if (gds == null) {
            return null;
        }
        for (GridData gd : gds) {
            if (gd.getType() == GridData.Type.Folder) {
                HashMap<String, FileProperties> map = listLFCDir(path + "/" + gd.getName(), s);
                if (map != null) {
                    entries.putAll(map);
                }
            } else {
                String entry = (path + "/" + gd.getName()).replaceAll(s.getSyncedLFCDir(), "/").replaceAll("//", "/");
                if (!ignore(entry)) {
                    //add revision the size of file in this List
                    entries.put(entry, new FileProperties(gd.getLength(), gd.getComment()));
                    logger.error(gd.getLength() + "         " + gd.getComment());

                }
            }
        }
        return entries;
    }

    /**
     * Sets the revision of an LFN.
     *
     * @param lfn
     * @param revision
     * @throws SyncException
     */
    public void setRevision(String lfn, String revision) throws SyncException {
        try {
            gc.setComment(lfn, revision);
        } catch (GRIDAClientException ex) {
            throw new SyncException(ex);
        }
    }

    /**
     * Get size of file in the LFC.
     *
     * @param lfn the lfn of the file
     * @throws SyncException
     */
    public String getFileSize(String lfn) throws SyncException {
        try {
            return gc.getFileSize(lfn);
        } catch (GRIDAClientException ex) {
            throw new SyncException(ex);
        }
    }

    /**
     * Copies a local file to the LFC
     *
     * @param localPath
     * @param remoteDir
     * @throws SyncException
     */
    public void copyToLfc(String localPath, String remoteDir) throws SyncException {
        try {
            //create dir in lfc
            if (!gc.exist(remoteDir)) {
                gc.createFolder(remoteDir.substring(0, remoteDir.lastIndexOf("/")), remoteDir.substring(remoteDir.lastIndexOf("/")));
            }
        } catch (GRIDAClientException ex) {
            throw new SyncException(ex);
        }
        try {
            //copy to lfc
            gc.uploadFile(localPath, remoteDir.replaceAll("//", "/"));
        } catch (GRIDAClientException ex) {
            throw new SyncException(ex);
        }
    }

    /**
     * Copies a file from the LFC to a local directory.
     *
     * @param lfcPath
     * @param localDir
     * @param s
     * @throws SyncException
     */
    public void getLFCFile(String lfcPath, String localDir, Synchronization s) throws SyncException {
        try {
            gc.getRemoteFile((s.getSyncedLFCDir() + "/" + lfcPath).replaceAll("//", "/"), localDir.replaceAll("//", "/"));
        } catch (GRIDAClientException ex) {
            throw new SyncException(ex);
        }
    }

    /**
     * Deletes a file from the LFC.
     *
     * @param lfcPath
     * @param s
     * @throws SyncException
     */
    public void deleteFromLFC(String lfcPath, Synchronization s) throws SyncException {
        try {
            gc.delete((s.getSyncedLFCDir() + "/" + lfcPath).replaceAll("//", "/"));
        } catch (GRIDAClientException ex) {
            throw new SyncException(ex);
        }
    }

    /**
     * Private methods
     */
    private static boolean ignore(String entry) {
        //the second test detects whether this is a temp file waiting to be renamed by GASW
        return entry.contains("uploadTest") || entry.matches("^.*[a-zA-Z]{32}");
    }
}
