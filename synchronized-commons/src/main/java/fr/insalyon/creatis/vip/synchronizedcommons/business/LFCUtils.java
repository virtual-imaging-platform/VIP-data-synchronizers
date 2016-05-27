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

import fr.insalyon.creatis.grida.client.GRIDAClient;
import fr.insalyon.creatis.grida.client.GRIDAClientException;
import fr.insalyon.creatis.grida.common.bean.GridData;
import fr.insalyon.creatis.vip.synchronizedcommons.FileProperties;
import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
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
