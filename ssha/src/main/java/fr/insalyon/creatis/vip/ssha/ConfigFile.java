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

import java.io.File;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author Nouha Boujelben nouha.boujelben@creatis.insa-lyon.fr
 */
public class ConfigFile {

    PropertiesConfiguration config;
    private static final String configFile = "./ssha.conf";
    private static ConfigFile instance;
    private String gridaHost;
    private int gridaPort;
    private String gridaProxy;
    private String LOCAL_TEMP;
    private long sleepTimeMillis;
    private int maxFilesIteration = 10;
    private String privKeyFile;
    private String privKeyPass;
    private String url;
    private String userName;
    private String password;
    private double nbSecond;

    /**
     *
     * @return instance of the configure file
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
            nbSecond = config.getDouble("ssha.exponentielBackOff.nbSecond", 5);

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
            config.setProperty("ssha.exponentielBackOff.nbSecond", nbSecond);
            config.save();

        } catch (ConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public String getGridaHost() {
        return gridaHost;
    }

    /**
     *
     * @param gridaHost grida Host
     */
    public void setGridaHost(String gridaHost) {
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
     * @param gridaPort grida port
     */
    public void setGridaPort(int gridaPort) {
        this.gridaPort = gridaPort;
    }

    /**
     *
     * @return
     */
    public String getGridaProxy() {
        return gridaProxy;
    }

    /**
     *
     * @param gridaProxy grida proxy
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
    public int getMaxFilesIteration() {
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
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     *
     * @return the slotTime in millisecond unit
     */

    public double getNbSecond() {
        return nbSecond;
    }
     
    /**
     *
     * @param slotTime millisecond unit
     */
    public void setNbSecond(double nbSecond) {
        this.nbSecond = nbSecond;
    }

}
