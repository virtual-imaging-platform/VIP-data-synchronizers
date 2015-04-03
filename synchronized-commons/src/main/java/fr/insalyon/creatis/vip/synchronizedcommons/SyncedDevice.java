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
 * @author Tristan Glatard
 */
public interface SyncedDevice {
   
    /**
     * Lists all the files in a remote directory, with their revisions.
     * @param remoteDir short path of the remote directory.
     * @return
     * @throws SyncException 
     */
    public HashMap<String,String> listFiles(String remoteDir) throws SyncException;
    
    /**
     * Copies a remote file to a local directory.
     * @param remoteFile short path of the remote file
     * @param localDir local directory where to copy the file
     * @throws SyncException 
     */
    public void getFile(String remoteFile, String localDir) throws SyncException;
    
    /**
     * Copies a local file to a remote directory.
     * @param localFile absolute path of the local file.
     * @param remoteDir short path of the remote directory.
     * @throws SyncException 
     */
    public void putFile(String localFile, String remoteDir) throws SyncException;
    
    /**
     * Sets the 'authfailed' flag for the synchronization.
     * @param s
     * @throws SyncException 
     */
    public void setAuthFailed(Synchronization s) throws SyncException;
    
    /**
     * Sets the 'validated' flag for the synchronization.
     * @param s
     * @throws SyncException 
     */
    public void validateSynchronization(Synchronization s) throws SyncException;
    
    /**
     * Gets all the synchronizations on this device.
     * @return 
     * @throws SyncException 
     */
    public List<Synchronization> getSynchronization() throws SyncException;
    
    /**
     * Gets the revision of a remote file.
     * @param remoteFile short path of the remote file.
     * @return
     * @throws SyncException 
     */
    public String getRevision(String remoteFile) throws SyncException;
    
    /**
     * Sets the synchronization to use.
     * @param s 
     */
    public void setSynchronization(Synchronization s);
    
    /**
     * The temporary directory where to store files locally.
     * @return 
     */
    public String getTempDir();
    
    /**
     * A string contained in the "authentication failed" error message.
     * @return 
     */
    public String getAuthFailedString();
    
}
