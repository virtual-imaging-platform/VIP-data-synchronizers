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

import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDevice;
import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;

/**
 * A class with utility methods to manipulate paths. There are different types
 * of paths: short lfc path (relative to the synced LFC directory), full lfc
 * paths, and short sync path (short sync path == short lfc path). But there is
 * no full sync path (full sync path is only known to the SyncedDevice), local
 * path
 *
 * @author Tristan Glatard
 */
public class PathUtils {

    /**
     * Gets the basename of the path.
     *
     * @param path
     * @return
     */
    public static String getName(String path) {
        return cleanse(path.substring(path.lastIndexOf("/")));
    }

    /**
     * Gets the directory name of the path.
     *
     * @param remotePath
     * @return
     */
    public static String getDirFromPath(String remotePath) {
        return cleanse(remotePath.substring(0, remotePath.lastIndexOf("/")));
    }

    /**
     * Gets the local path where a file will be downloaded. The local path is
     * the temporary directory + the LFC long path
     *
     * @param s the LFC long path
     * @param sd
     * @return
     */
    public static String getLocalPathFromLFCLong(String s, SyncedDevice sd) throws SyncException {
        return cleanse(sd.getTempDir() + "/" + s);
    }

    public static String getLocalPathFromSyncShort(String s, SyncedDevice sd, Synchronization sy) throws SyncException {
        return cleanse(getLocalPathFromLFCLong(getLFCLongFromSyncShort(s, sy), sd));
    }

    public static String getSyncShortFromLFCLong(String s, Synchronization ua) {
        return cleanse(getLFCShortFromLFCLong(s, ua));
    }

    public static String getSyncShortFromLFCShort(String s) {
        return cleanse(s);
    }

    public static String getLFCShortFromLFCLong(String s, Synchronization ua) {
        return cleanse(s.replaceAll(ua.getSyncedLFCDir(), ""));
    }

    public static String getLFCLongFromLFCShort(String s, Synchronization ua) {
        return cleanse(ua.getSyncedLFCDir() + "/" + s);
    }

    public static String getLFCLongFromSyncShort(String s, Synchronization ua) {
        return cleanse(ua.getSyncedLFCDir() + "/" + s);
    }

    /**
     * Removes double '/' from a path.
     *
     * @param s
     * @return
     */
    public static String cleanse(String s) {
        return s.replaceAll("//", "/");
    }
}
