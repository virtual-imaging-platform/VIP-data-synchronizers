package fr.insalyon.creatis.vip.ssha;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDevice;
import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import fr.insalyon.creatis.vip.synchronizedcommons.business.Synchronizer;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Main class for SSH synchronization agent.
 *
 * @author Tristan Glatard tristan.glatard@creatis.insa-lyon.fr, Nouha Boujelben
 * nouha.boujelben@creatis.insa-lyon.fr
 */
public class Agent {

    /**
     *
     * @param args
     * @throws IOException
     * @throws JSchException
     * @throws SftpException
     * @throws SyncException
     */
    public static void main(String[] args) throws IOException, JSchException, SftpException, SyncException {
        
      
        System.setProperty("logfile.name", "./ssha.log");
        ConfigFile.getInstance();
        final Logger logger = Logger.getLogger(Agent.class);
        SSHDevice sshd = new SSHDevice(ConfigFile.getInstance().getPrivKeyFile(), ConfigFile.getInstance().getPrivKeyPass(), ConfigFile.getInstance().getLOCAL_TEMP(), ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        Synchronizer s = new Synchronizer((SyncedDevice) sshd, ConfigFile.getInstance().getGridaHost(), ConfigFile.getInstance().getGridaPort(), ConfigFile.getInstance().getGridaProxy(), ConfigFile.getInstance().getMaxFilesIteration(), ConfigFile.getInstance().getSleepTimeMillis());
        s.start();
    }
}
