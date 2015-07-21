/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
