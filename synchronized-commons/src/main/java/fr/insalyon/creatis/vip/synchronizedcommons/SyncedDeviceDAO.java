/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons;

import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import java.util.List;

/**
 * DAO interface.
 * @author Tristan Glatard, Nouha Boujelben
 */
public interface SyncedDeviceDAO {
    
    /**
     * Sets the 'validated' flag for a synchronization.
     * @param s
     * @throws SyncException 
     */
     public  void validateSynchronization(Synchronization s) throws SyncException;
     
     /**
      * Sets the 'synchronizationfailed' flag for a synchronization.
      * @param s
      * @throws SyncException 
      */
     public  void setSynchronizationFailed(Synchronization s) throws SyncException;
     
     
     /**
      * Sets the 'synchronisationfailed' flag for a synchronization.
      * @param s
      * @throws SyncException 
      */
     public  void setSynchronizationNotFailed(Synchronization s) throws SyncException;
     
     /**
      * Gets all the synchronizations.
      * @return
      * @throws SyncException 
      */
     public  List<Synchronization> getSynchronizations() throws SyncException;
}
