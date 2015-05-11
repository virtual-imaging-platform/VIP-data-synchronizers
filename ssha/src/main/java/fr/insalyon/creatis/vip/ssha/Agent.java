package fr.insalyon.creatis.vip.ssha;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDevice;
import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import fr.insalyon.creatis.vip.synchronizedcommons.business.Synchronizer;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.RollingFileAppender;

/**
 * Main class for SSH synchronization agent.
 *
 * @author Tristan Glatard tristan.glatard@creatis.insa-lyon.fr
 */
public class Agent {

    private static final String configFile = "./ssha.conf";
    private static Logger logger;
    //get this from config
    private static String gridaHost;
    private static int gridaPort;
    private static String gridaProxy;
    private static String LOCAL_TEMP;
    private static long sleepTimeMillis;
    private static int maxFilesIteration = 10;
    private static String privKeyFile;
    private static String privKeyPass;
    private static String url;
    private static String userName;
    private static String password;
    //private static String fileLoggerPath;

    public static void main(String[] args) throws IOException, JSchException, SftpException, SyncException {

        
        System.setProperty("logfile.name", "/home/boujelben/.ssha/ssha.log");
        logger = Logger.getLogger(Agent.class);
        loadConfigurationFile();

        SSHDevice sshd = new SSHDevice(privKeyFile, privKeyPass, LOCAL_TEMP, url, userName, password);
        Synchronizer s = new Synchronizer((SyncedDevice) sshd, gridaHost, gridaPort, gridaProxy, maxFilesIteration, sleepTimeMillis);
        s.start();
    }

    private static void loadConfigurationFile() {

        
        try {
            PropertiesConfiguration config = new PropertiesConfiguration(new File(configFile));
            //fileLoggerPath = config.getString("ssha.file.log", "/home/boujelben/.ssha/ssha.log");
            gridaHost = config.getString("ssha.grida.host", "kingkong.grid.creatis.insa-lyon.fr");
            gridaPort = config.getInt("ssha.grida.port", 9006);
            gridaProxy = config.getString("ssha.grida.proxy", "/root/.vip/proxies/x509up_server");
            LOCAL_TEMP = config.getString("ssha.tempdir", "/tmp/ssh");
            sleepTimeMillis = config.getInt("ssha.iteration.sleeptime", 5000);
            maxFilesIteration = config.getInt("ssha.iteration.maxfiles", 10);
            privKeyFile = config.getString("ssha.auth.privatekeyfile", "./id_rsa");
            privKeyPass = config.getString("ssha.auth.privatekeypass", "changeit");
            url = config.getString("ssha.db.jdbcurl", "jdbc:mysql://localhost:3306/vip");
            userName = config.getString("ssha.db.user", "vip");
            password = config.getString("ssha.db.password", "changeit");

            //config.setProperty("ssha.file.log", fileLoggerPath);
            config.setProperty("ssha.grida.host", gridaHost);
            config.setProperty("ssha.grida.port", gridaPort);
            config.setProperty("ssha.grida.proxy", gridaProxy);
            config.setProperty("ssha.tempdir", LOCAL_TEMP);
            config.setProperty("ssha.iteration.sleeptime", sleepTimeMillis);
            config.setProperty("ssha.iteration.maxfiles", maxFilesIteration);
            config.setProperty("ssha.auth.privatekeyfile", privKeyFile);
            config.setProperty("ssha.auth.privatekeypass", privKeyPass);
            config.setProperty("ssha.db.jdbcurl", url);
            config.setProperty("ssha.db.user", userName);
            config.setProperty("ssha.db.password", password);
            config.save();
        } catch (ConfigurationException ex) {
            java.util.logging.Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }

        

        
    }
}
