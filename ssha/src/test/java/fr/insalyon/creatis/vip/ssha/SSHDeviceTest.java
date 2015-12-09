/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
import fr.insalyon.creatis.vip.synchronizedcommons.TransferType;
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

/* 

 To test this class you have to:
 **reconfigure the config file especially the :ssha.auth.privatekeyfile to point it to the specific file
 **paramters of the data base: ssha.db.jdbcurl, ssha.db.user, ssha.db.password

 **SSH TUNNEL FOR THE MYSQL SERVER
 ssh -L [LOCAL PORT]:localhost:3306 [USERNAME]@vip.creatis.insa-lyon.fr -f -N 
 **
 */
public class SSHDeviceTest {

    static Synchronization ua;

    public SSHDeviceTest() {

    }

    @BeforeClass
    public static void onceExecutedBeforeAll() {
        System.setProperty("logfile.name", "./ssha.log");
        ua = new SSHSynchronization("nouha.boujelben@creatis.insa-lyon.fr", true, false, "/grid/biomed/creatis/vip/data/users/nouha_boujelben/kk_ssh", TransferType.Synchronization, "nouha", "localhost", "/home/nouha/r", 22, true, 0, 0, 0, 0, 0, 0, 0, 0);
    }
    @Rule
    public final ExpectedException thrown2 = ExpectedException.none();

    /**
     * Test of getNumberSynchronizationFailed method, of class SSHDevice.
     */
    @Test
    public void testUpdateNumberSynchronizationFailed() throws SyncException {

        System.out.println("updateNumberSynchronizationFailed");
        thrown2.expect(SyncException.class);
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
    public void testMustWaitBeforeNextSynchronization() throws SyncException {
        thrown2.expect(SyncException.class);
        SSHDevice instance = new SSHDevice(ConfigFile.getInstance().getPrivKeyFile(), ConfigFile.getInstance().getPrivKeyPass(), ConfigFile.getInstance().getLOCAL_TEMP(), ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        boolean expResult = false;
        boolean result = instance.isMustWaitBeforeNextSynchronization(ua);
        assertEquals(expResult, result);

    }

    /**
     * Test of updateNumberSynchronizationFailed method, of class SSHDevice.
     */
    @Test
    public void testGetNumberOfMinuteFromConfigFile() {
        SSHDevice instance = new SSHDevice(ConfigFile.getInstance().getPrivKeyFile(), ConfigFile.getInstance().getPrivKeyPass(), ConfigFile.getInstance().getLOCAL_TEMP(), ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        Double expResult = 5.0;
        Double result = instance.getNbSecondFromConfigFile();
        assertEquals(expResult, result);

    }

    @Test
    public void testGetSynchronizations() throws SyncException {
        thrown2.expect(SyncException.class);
        SSHMySQLDAO sSHMySQLDAO = SSHMySQLDAO.getInstance(ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        assertEquals(2, sSHMySQLDAO.getActiveSynchronizations().size());
        throw new SyncException("get synchronizations failed");
    }

    @Test
    public void deleteFile() throws SyncException {
        thrown2.expect(SyncException.class);
        SSHDevice instance = new SSHDevice(ConfigFile.getInstance().getPrivKeyFile(), ConfigFile.getInstance().getPrivKeyPass(), ConfigFile.getInstance().getLOCAL_TEMP(), ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        instance.setSynchronization(ua);
        instance.deleteFile("rr.txt");
        throw new SyncException("failed to delete file");

    }

}
