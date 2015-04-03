/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.synchronizedcommons.business;

/**
 * Exception class.
 * @author Tristan Glatard
 */
public class SyncException extends Exception {
     public SyncException(String message) {
        super(message);
    }

    public SyncException(Throwable thrwbl) {
        super(thrwbl);
    }
}
