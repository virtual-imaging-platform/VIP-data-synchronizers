/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons.business;

import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDevice;
import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;

/**
 * A class with utility methods to manipulate paths.
 * There are different types
 * of paths: short lfc path (relative to the synced LFC directory), full lfc
 * paths, and short sync path (short sync path == short lfc path). But there is no
 * full sync path (full sync path is only known to the SyncedDevice), local path
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
    public static String getLocalPathFromLFCLong(String s, SyncedDevice sd) {
        return cleanse(sd.getTempDir() + "/" + s);
    }

    public static String getLocalPathFromSyncShort(String s, SyncedDevice sd, Synchronization sy) {
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
     * @param s
     * @return 
     */
    public static String cleanse(String s) {
        return s.replaceAll("//", "/");
    }
}
