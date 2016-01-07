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

import fr.insalyon.creatis.vip.synchronizedcommons.FileProperties;
import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDevice;
import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
import fr.insalyon.creatis.vip.synchronizedcommons.TransferType;
import fr.insalyon.creatis.vip.synchronizedcommons.business.PathUtils;
import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import fr.insalyon.creatis.vip.synchronizedcommons.business.Synchronizer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Nouha Boujelben
 */
/**
 * To test the synchronization in your machine you have to: 1)Active the sshd
 * server (sudo service sshd start) 2)Create a database for test: **local
 * database :you can run the class DataBaseTest and DataBaseOperations to create
 * a local database **remote database(remote MYSQL server) for test you have to
 * create an ssh tunnel for the mysql server: ssh -L [LOCAL
 * PORT]:localhost:[REMOTE PORT] [USERNAME]@[SSH_SERVER] -f -N (option -f -N to
 * force ssh to go to background without running a particular command on the
 * remote server) 3)SSH TUNNEL FOR GRIDA(grida port and grida host are
 * configured in the ssha config file) ssh -L [LOCALPORT]:localhost:[GRIDA PORT]
 * [USER NAME]@[GRIDA SERVER] -f -N use SSHFS to mount a temporary local
 * directory (specified in the ssha config file) with a directory in grida
 * server these two directories have to take the some path and name sshfs
 * [USERNAME]@[SSH REMOTE SERVER]:[REMOTE DIR] [LOCAL DIR] -o nonempty
 */
public class TransferTest {

    static SSHDevice sshd;
    static Synchronizer s;
    static SSHSynchronization ua;

    public TransferTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("logfile.name", "./ssha.log");
        sshd = new SSHDevice(ConfigFile.getInstance().getPrivKeyFile(), ConfigFile.getInstance().getPrivKeyPass(), ConfigFile.getInstance().getLOCAL_TEMP(), ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword());
        s = new Synchronizer((SyncedDevice) sshd, ConfigFile.getInstance().getGridaHost(), ConfigFile.getInstance().getGridaPort(), ConfigFile.getInstance().getGridaProxy(), ConfigFile.getInstance().getMaxFilesIteration(), ConfigFile.getInstance().getSleepTimeMillis());
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void transferFilesFromLFCToSynchDevice() throws SyncException {
        System.out.println("transferFilesFromLFCToSynchDevice");
        int fileWithSameName = 0;
        ua = (SSHSynchronization) getSynchronization("nouha.boujelben@creatis.insa-lyon.fr", "/grid/biomed/creatis/vip/data/users/nouha_boujelben/NOUHA4_ssh");
        sshd.setSynchronization(ua);
        HashMap<String, FileProperties> lfcFiles = s.getLfcu().listLFCDir("/", ua);
        HashMap<String, FileProperties> sshFiles = sshd.listFiles("/", ua);
        for (Map.Entry<String, FileProperties> p : lfcFiles.entrySet()) {
            String lfcPath = PathUtils.cleanse(p.getKey());
            if (sshFiles.get(lfcPath) != null) {
                fileWithSameName++;
            }
        }
        String syncedLFCDir = ua.getSyncedLFCDir();
        resetLFCAndDeviceMonitorParams(sshd, ua);
        methodReflection(s, "transferFilesFromLFCToSynchDevice", ua, sshFiles, lfcFiles, 0, ua.getNumberOfFilesTransferredToDevice(), ua.getSizeOfFilesTransferredToDevice(), ua.getNumberOfFilesDeletedInLFC(), ua.getSizeOfFilesDeletedInLFC(), ua.getNumberOfFilesTransferredToLFC(), ua.getSizeOfFilesTransferredToLFC(), ua.getNumberOfFilesDeletedInDevice(), ua.getSizeOfFilesDeletedInDevice(), syncedLFCDir, true, true, true);
        HashMap<String, FileProperties> expSshFiles = sshd.listFiles("/", ua);
        //assert equal 
        assertEquals(expSshFiles.size(), lfcFiles.size() + sshFiles.size() - fileWithSameName);
    }

    //  @Test
    public void transferFilesFromSynchDeviceToLFC() throws SyncException {
        System.out.println("transferFilesFromSynchDeviceToLFC");
        int fileWithSameName = 0;
        ua = (SSHSynchronization) getSynchronization("nouha.boujelben@creatis.insa-lyon.fr", "/grid/biomed/creatis/vip/data/users/nouha_boujelben/NOUHA4_ssh");
        sshd.setSynchronization(ua);
        HashMap<String, FileProperties> lfcFiles = s.getLfcu().listLFCDir("/", ua);
        HashMap<String, FileProperties> sshFiles = sshd.listFiles("/", ua);
        for (Map.Entry<String, FileProperties> p : sshFiles.entrySet()) {
            String sshPath = PathUtils.cleanse(p.getKey());
            if (lfcFiles.get(sshPath) != null) {
                fileWithSameName++;
            }
        }

        String syncedLFCDir = ua.getSyncedLFCDir();
        resetLFCAndDeviceMonitorParams(sshd, ua);
        int countFiles = 0;
        methodReflection(s, "transferFilesFromSynchDeviceToLFC", ua, sshd, sshFiles, lfcFiles, ua.getSizeOfFilesTransferredToLFC(), ua.getNumberOfFilesDeletedInLFC(), ua.getSizeOfFilesDeletedInLFC(), syncedLFCDir, true);
        HashMap<String, FileProperties> expLfcFiles = s.getLfcu().listLFCDir("/", ua);
        assertEquals(expLfcFiles.size(), lfcFiles.size() + sshFiles.size() - fileWithSameName);

    }

    //@Test
    public void synchronization() throws SyncException {
        System.out.println("transferFilesFromSynchDeviceToLFC");
        int filesWithSameName = 0;
        ua = (SSHSynchronization) getSynchronization("nouha.boujelben@creatis.insa-lyon.fr", "/grid/biomed/creatis/vip/data/users/nouha_boujelben/NOUHA4_ssh");
        sshd.setSynchronization(ua);
        HashMap<String, FileProperties> lfcFiles = s.getLfcu().listLFCDir("/", ua);
        HashMap<String, FileProperties> sshFiles = sshd.listFiles("/", ua);
        for (Map.Entry<String, FileProperties> p : sshFiles.entrySet()) {
            String sshPath = PathUtils.cleanse(p.getKey());
            if (lfcFiles.get(sshPath) != null) {
                filesWithSameName++;
            }
        }
        String syncedLFCDir = ua.getSyncedLFCDir();
        resetLFCAndDeviceMonitorParams(sshd, ua);

        int countFiles = 0;
        //SyncedDevice -> LFC
        methodReflection(s, "transferFilesFromSynchDeviceToLFC", ua, sshd, sshFiles, lfcFiles, ua.getNumberOfFilesTransferredToLFC(), ua.getSizeOfFilesTransferredToLFC(), ua.getNumberOfFilesDeletedInLFC(), ua.getSizeOfFilesDeletedInLFC(), syncedLFCDir, false);
        //LFC -> SyncedDevice
        LFCMonitorParams lFCMonitorParams = getLFCMonitorParams(ua.getEmail(), "/grid/biomed/creatis/vip/data/users/nouha_boujelben/NOUHA4_ssh");
        methodReflection(s, "transferFilesFromLFCToSynchDevice", ua, sshFiles, lfcFiles, 0, ua.getNumberOfFilesTransferredToDevice(), ua.getSizeOfFilesTransferredToDevice(), lFCMonitorParams.getNumberOfFilesDeletedInLFC(), lFCMonitorParams.getSizeOfFilesDeletedInLFC(), lFCMonitorParams.getNumberOfFilesTransferredToLFC(), lFCMonitorParams.getSizeOfFilesTransferredToLFC(), ua.getNumberOfFilesDeletedInDevice(), ua.getSizeOfFilesDeletedInDevice(), syncedLFCDir, false, true, false);
        HashMap<String, FileProperties> expLfcFiles = s.getLfcu().listLFCDir("/", ua);
        HashMap<String, FileProperties> expSshFiles = sshd.listFiles("/", ua);

        assertEquals(expLfcFiles.size(), expSshFiles.size());

    }

    private Synchronization getSynchronization(String email, String lfcDir) throws SyncException {

        try {
            PreparedStatement ps = SSHMySQLDAO.getInstance(ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword()).getConnection().prepareStatement("SELECT "
                    + " * "
                    + "FROM VIPSSHAccounts where email=? and LFCDir=? ");
            ps.setString(1, email);
            ps.setString(2, lfcDir);
            ResultSet rs = ps.executeQuery();
            Synchronization sSHSynchronization = null;
            while (rs.next()) {

                sSHSynchronization = new SSHSynchronization(rs.getString("email"), rs.getBoolean("validated"), rs.getBoolean("auth_failed"), rs.getString("LFCDir"), TransferType.valueOf(rs.getString("transferType")),
                        rs.getString("sshUser"), rs.getString("sshHost"), rs.getString("sshDir"), rs.getInt("sshPort"), rs.getBoolean("deleteFilesFromSource"), rs.getBoolean("checkFilesContent"),
                        rs.getInt("numberOfFilesTransferredToLFC"), rs.getLong("sizeOfFilesTransferredToLFC"), rs.getInt("numberOfFilesTransferredToDevice"), rs.getLong("sizeOfFilesTransferredToDevice"),
                        rs.getInt("numberOfFilesDeletedInLFC"), rs.getLong("sizeOfFilesDeletedInLFC"), rs.getInt("numberOfFilesDeletedInDevice"), rs.getLong("sizeOfFilesDeletedInDevice"));

            }
            ps.close();
            return sSHSynchronization;
        } catch (SQLException ex) {
            throw new SyncException(ex);
        }

    }

    private void resetLFCAndDeviceMonitorParams(SSHDevice sshd, SSHSynchronization ua) throws SyncException {
        sshd.updateDeviceMonitoringParams(ua, 0, 0, 0, 0);
        ua.setNumberOfFilesTransferredToDevice(0);
        ua.setSizeOfFilesTransferredToDevice(0);
        ua.setNumberOfFilesDeletedInDevice(0);
        ua.setSizeOfFilesDeletedInDevice(0);
        sshd.updateLFCMonitoringParams(ua, 0, 0, 0, 0);
        ua.setNumberOfFilesTransferredToLFC(0);
        ua.setSizeOfFilesTransferredToLFC(0);
        ua.setNumberOfFilesDeletedInLFC(0);
        ua.setSizeOfFilesDeletedInLFC(0);

    }

    private LFCMonitorParams getLFCMonitorParams(String email, String lfcDir) throws SyncException {
        try {
            PreparedStatement ps = SSHMySQLDAO.getInstance(ConfigFile.getInstance().getUrl(), ConfigFile.getInstance().getUserName(), ConfigFile.getInstance().getPassword()).getConnection().prepareStatement("SELECT "
                    + " * "
                    + "FROM VIPSSHAccounts where email=? and LFCDir=? ");
            ps.setString(1, email);
            ps.setString(2, lfcDir);
            ResultSet rs = ps.executeQuery();
            LFCMonitorParams lFCMonitorParams = null;
            while (rs.next()) {

                lFCMonitorParams = new LFCMonitorParams(
                        rs.getInt("numberOfFilesTransferredToLFC"), rs.getLong("sizeOfFilesTransferredToLFC"),
                        rs.getInt("numberOfFilesDeletedInLFC"), rs.getLong("sizeOfFilesDeletedInLFC"));

            }
            ps.close();
            return lFCMonitorParams;
        } catch (SQLException ex) {
            throw new SyncException(ex);
        }

    }

    private void methodReflection(Synchronizer s, String methodName,
            Object... args) throws SyncException {
        final Class table[] = new Class[args.length];
        int i = 0;
        for (Object obj : args) {
            if (obj instanceof java.lang.Integer) {
                table[i] = Integer.TYPE;
            } else if (obj instanceof java.lang.Long) {
                table[i] = Long.TYPE;
            } else if (obj instanceof java.lang.Boolean) {
                table[i] = Boolean.TYPE;
            } else if (obj instanceof Synchronization) {
                table[i] = Synchronization.class;
            } else {
                table[i] = obj.getClass();
            }
            i++;
        }
        try {
            Method method = s.getClass().getDeclaredMethod(methodName, table);
            method.setAccessible(true);
            method.invoke(s, args);
        } catch (IllegalAccessException ex) {
            ex.getCause().printStackTrace();
            throw new SyncException(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            ex.getCause().printStackTrace();
            throw new SyncException(ex.getMessage());
        } catch (InvocationTargetException ex) {
            ex.getCause().printStackTrace();
            throw new SyncException(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            ex.getCause().printStackTrace();
            throw new SyncException(ex.getMessage());
        } catch (SecurityException ex) {
            ex.getCause().printStackTrace();
            throw new SyncException(ex.getMessage());
        }
    }

}
