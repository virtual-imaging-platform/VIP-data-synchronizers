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
        ua = new SSHSynchronization("nouha.boujelben@creatis.insa-lyon.fr", true, false, "/grid/biomed/creatis/vip/data/users/nouha_boujelben/kk_ssh", TransferType.Synchronization, "nouha", "localhost", "/home/nouha/r", 22, true,false, 0, 0, 0, 0, 0, 0, 0, 0);
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
