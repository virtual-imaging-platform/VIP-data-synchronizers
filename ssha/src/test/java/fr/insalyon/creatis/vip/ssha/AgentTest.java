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

import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDevice;
import fr.insalyon.creatis.vip.synchronizedcommons.business.Synchronizer;
import java.io.File;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 *
 * @author Nouha Boujelben
 */
public class AgentTest {

    public AgentTest() {
    }

    /**
     * Test of main method, of class Agent.
     */
    /**
     * To test the synchronization in your machine you have to:
     * 1)Active the sshd server (sudo service sshd start)
     * 2)Create a database for test:
     *       **local database :you can run the class DataBaseTest and DataBaseOperations to create a local database
     *       **remote database(remote MYSQL server) for test you have to create an ssh tunnel for the mysql server:
     *         ssh -L [LOCAL PORT]:localhost:[REMOTE PORT] [USERNAME]@[SSH_SERVER] -f -N
     *         (option -f -N  to force ssh to go to background without running a particular command on the remote server)
     * 3)SSH TUNNEL FOR GRIDA(grida port and grida host are configured in the ssha config file)
     *        ssh -L [LOCALPORT]:localhost:[GRIDA PORT] [USER NAME]@[GRIDA SERVER] -f -N
     *        use SSHFS to mount a  temporary local directory (specified in the ssha config file) with a directory in grida server 
     *        these two directories have to take the some path and name
     *        sshfs [USERNAME]@[SSH REMOTE SERVER]:[REMOTE DIR] [LOCAL DIR] -o nonempty
     */
    @Test
    public void testMain() {
        //the logger file must be created in the home directory of the project
        System.setProperty("logfile.name", "./ssha.log");
        final Logger logger = Logger.getLogger(AgentTest.class);
        SSHDevice sd = new SSHDevice(ConfigFile.getInstance().getPrivKeyFile(), ConfigFile.getInstance().getPrivKeyPass(), ConfigFile.getInstance().getLOCAL_TEMP(), ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        Synchronizer s = new Synchronizer((SyncedDevice) sd, ConfigFile.getInstance().getGridaHost(), ConfigFile.getInstance().getGridaPort(), ConfigFile.getInstance().getGridaProxy(), ConfigFile.getInstance().getMaxFilesIteration(), ConfigFile.getInstance().getSleepTimeMillis());
        s.run();

    }

}
