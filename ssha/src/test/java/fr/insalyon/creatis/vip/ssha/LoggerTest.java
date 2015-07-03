/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import java.io.File;
import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nouha Boujelben
 */
public class LoggerTest {
    
    public LoggerTest() {
    }
   
    /**
     * Test of main method, of class Agent.
     */
    @Test
    public void testLogger() throws Exception {
       System.setProperty("logfile.name", "./ssha.log");
       File f1=new File("./ssha.log");
       long l1=f1.length(); 
       final Logger logger = Logger.getLogger(LoggerTest.class);
       logger.info("write in the logger file");
       File f2=new File("./ssha.log");  
       assertTrue("Error, random is to high", f2.length() > l1);
    }
    
}
