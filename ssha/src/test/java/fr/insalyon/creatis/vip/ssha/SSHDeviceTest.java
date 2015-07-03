/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Nouha Boujelben
 */
public class SSHDeviceTest {

    static Synchronization ua;

    public SSHDeviceTest() {

    }

    @BeforeClass
    public static void onceExecutedBeforeAll() {
        ua = new Synchronization("nouha.boujelben@creatis.insa-lyon.fr", true, false, "/grid/biomed/creatis/vip/data/users/nouha_boujelben/nouha_ssh");
    }

    /**
     * Test of getNumberSynchronizationFailed method, of class SSHDevice.
     */
    @Test
    public void testUpdateNumberSynchronizationFailed() {
        System.setProperty("logfile.name", "./ssha.log");
        System.out.println("updateNumberSynchronizationFailed");
        int number = 4;
        SSHDevice instance = new SSHDevice(ConfigFile.getInstance().getPrivKeyFile(), ConfigFile.getInstance().getPrivKeyPass(), ConfigFile.getInstance().getLOCAL_TEMP(), ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        instance.updateNumberSynchronizationFailed(ua, number);
        assertEquals(number, instance.getNumberSynchronizationFailed(ua));
    }

    /**
     * Test of updateTheEarliestNextSynchronization method, of class SSHDevice.
     *
     * * @throws
     * fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException
     */
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void testUpdateTheEarliestNextSynchronization() throws SyncException {
        System.out.println("testUpdateTheEarliestNextSynchronization");
        thrown.expect(SyncException.class);
        System.setProperty("logfile.name", "./ssha.log");
        final Logger logger = Logger.getLogger(SSHDeviceTest.class);

        SSHDevice instance = new SSHDevice(ConfigFile.getInstance().getPrivKeyFile(), ConfigFile.getInstance().getPrivKeyPass(), ConfigFile.getInstance().getLOCAL_TEMP(), ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        instance.updateTheEarliestNextSynchronization(ua, 100000);
        logger.info(SSHMySQLDAO.getInstance(ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword()).getTheEarliestNextSynchronistation(ua));
        throw new SyncException("");

    }

    /**
     * Test of mustWaitBeforeNextSynchronization method, of class SSHDevice.
     */
    @Test
    public void testMustWaitBeforeNextSynchronization() {
        System.out.println("mustWaitBeforeNextSynchronization");
        SSHDevice instance = new SSHDevice(ConfigFile.getInstance().getPrivKeyFile(), ConfigFile.getInstance().getPrivKeyPass(), ConfigFile.getInstance().getLOCAL_TEMP(), ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        boolean expResult = false;
        boolean result = instance.mustWaitBeforeNextSynchronization(ua);
        assertEquals(expResult, result);

    }

    /**
     * Test of updateNumberSynchronizationFailed method, of class SSHDevice.
     */
    /**
     * Test of getSlotTimeFromConfigFile method, of class SSHDevice.
     */
    @Test
    public void testGetNumberOfMinuteFromConfigFile() {
        System.out.println("getNumberOfMinuteFromConfigFile");
        SSHDevice instance = new SSHDevice(ConfigFile.getInstance().getPrivKeyFile(), ConfigFile.getInstance().getPrivKeyPass(), ConfigFile.getInstance().getLOCAL_TEMP(), ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        Double expResult = 0.0512;
        Double result = instance.getSlotTimeFromConfigFile();
        assertEquals(expResult, result);

    }

    @Rule
    public final ExpectedException thrown2 = ExpectedException.none();

    @Test
    public void testGetSynchronizations() throws SyncException {
        thrown.expect(SyncException.class);
        SSHMySQLDAO sSHMySQLDAO = SSHMySQLDAO.getInstance(ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        assertEquals(2, sSHMySQLDAO.getSynchronizations().size());
        throw new SyncException("get synchronizations failed");
    }

}
