/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author Nouha Boujelben
 */
public class  ConfigFile {

    PropertiesConfiguration config;
    private static final String configFile = "./ssha.conf";
    private static ConfigFile instance;
    private  String gridaHost;
    private  int gridaPort;
    private  String gridaProxy;
    private  String LOCAL_TEMP;
    private  long sleepTimeMillis;
    private  int maxFilesIteration = 10;
    private  String privKeyFile;
    private  String privKeyPass;
    private  String url;
    private  String userName;
    private  String password;
    private  String fileLoggerPath;
   
    /**
     *
     * @return
     */
    public static ConfigFile getInstance() {
        if (instance == null) {
            instance = new ConfigFile();
        }
        return instance;
    }
   

    private ConfigFile() {
        try {
            config = new PropertiesConfiguration(new File(configFile));
            fileLoggerPath = config.getString("ssha.file.log", "/home/boujelben/.ssha/ssha.log");
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

            config.setProperty("ssha.file.log", fileLoggerPath);
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
         System.out.println(ex.getMessage());
        }
    }
    
    /**
     *
     * @return
     */
    public  String getGridaHost() {
        return gridaHost;
    }

    /**
     *
     * @param gridaHost
     */
    public  void setGridaHost(String gridaHost) {
        this.gridaHost = gridaHost;
    }

    /**
     *
     * @return
     */
    public int getGridaPort() {
        return gridaPort;
    }

    /**
     *
     * @param gridaPort
     */
    public void setGridaPort(int gridaPort) {
        this.gridaPort = gridaPort;
    }

    /**
     *
     * @return
     */
    public  String getGridaProxy() {
        return gridaProxy;
    }

    /**
     *
     * @param gridaProxy
     */
    public void setGridaProxy(String gridaProxy) {
        this.gridaProxy = gridaProxy;
    }

    /**
     *
     * @return
     */
    public String getLOCAL_TEMP() {
        return LOCAL_TEMP;
    }

    /**
     *
     * @param LOCAL_TEMP
     */
    public void setLOCAL_TEMP(String LOCAL_TEMP) {
        this.LOCAL_TEMP = LOCAL_TEMP;
    }

    /**
     *
     * @return
     */
    public long getSleepTimeMillis() {
        return sleepTimeMillis;
    }

    /**
     *
     * @param sleepTimeMillis
     */
    public void setSleepTimeMillis(long sleepTimeMillis) {
        this.sleepTimeMillis = sleepTimeMillis;
    }

    /**
     *
     * @return
     */
    public  int getMaxFilesIteration() {
        return maxFilesIteration;
    }

    /**
     *
     * @param maxFilesIteration
     */
    public void setMaxFilesIteration(int maxFilesIteration) {
        this.maxFilesIteration = maxFilesIteration;
    }

    /**
     *
     * @return
     */
    public String getPrivKeyFile() {
        return privKeyFile;
    }

    /**
     *
     * @param privKeyFile
     */
    public void setPrivKeyFile(String privKeyFile) {
        this.privKeyFile = privKeyFile;
    }

    /**
     *
     * @return
     */
    public String getPrivKeyPass() {
        return privKeyPass;
    }

    /**
     *
     * @param privKeyPass
     */
    public void setPrivKeyPass(String privKeyPass) {
        this.privKeyPass = privKeyPass;
    }

    /**
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     *
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     *
     * @return
     */
    public  String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public  void setPassword(String password) {
        this.password = password;
    }

    /**
     *
     * @return 
     */
    public String getFileLoggerPath() {
        return fileLoggerPath;
    }

    /**
     *
     * @param fileLoggerPath the path of the logger file
     */
    public void setFileLoggerPath(String fileLoggerPath) {
        this.fileLoggerPath = fileLoggerPath;
    }
}
