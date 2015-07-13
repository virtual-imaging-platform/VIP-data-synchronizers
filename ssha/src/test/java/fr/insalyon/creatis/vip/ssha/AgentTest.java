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
     * To test this class you have to run these following ssh commands: 
     * SSH TUNNEL FOR THE MYSQL SERVER ssh -L [LOCAL PORT]:localhost:3306 [USERNAME]@vip.creatis.insa-lyon.fr -f -N 
     * SSH TUNNEL FOR GRIDA(grida port and grida host are configured in the config file)
     * ssh -L [LOCALPORT]:localhost:[GRIDA PORT] [USER NAME]@vip.creatis.insa-lyon.fr -f -N
     * USE SSHFS TO MOUNT YOUR LOCAL DIR (SPECIFIED IN THE CONFIGURATION FILE)
     * WITH A KINGKONG REMOTE DIR TO BE ACCESSIBLE BY GRIDA 
     * sshfs [USERNAME]@vip.creatis.insa-lyon.fr:[REMOTE DIR] [LOCAL DIR] -o nonempty
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
