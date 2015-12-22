/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import fr.insalyon.creatis.vip.synchronizedcommons.FileProperties;
import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDevice;
import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
import fr.insalyon.creatis.vip.synchronizedcommons.TransferType;
import fr.insalyon.creatis.vip.synchronizedcommons.business.PathUtils;
import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import fr.insalyon.creatis.vip.synchronizedcommons.business.Synchronizer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    //@Test
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
        s.transferFilesFromLFCToSynchDevice(ua, sshFiles, lfcFiles, 0, ua.getNumberOfFilesTransferredToDevice(), ua.getSizeOfFilesTransferredToDevice(), ua.getNumberOfFilesDeletedInLFC(), ua.getSizeOfFilesDeletedInLFC(), ua.getNumberOfFilesTransferredToLFC(), ua.getSizeOfFilesTransferredToLFC(), ua.getNumberOfFilesDeletedInDevice(), ua.getSizeOfFilesDeletedInDevice(), syncedLFCDir, true, true, true);
        HashMap<String, FileProperties> expSshFiles = sshd.listFiles("/", ua);
        //assert equal 
        assertEquals(expSshFiles.size(), lfcFiles.size() + sshFiles.size() - fileWithSameName);
    }

    //@Test
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
        s.transferFilesFromSynchDeviceToLFC(ua, sshd, sshFiles, lfcFiles, ua.getNumberOfFilesTransferredToLFC(), ua.getSizeOfFilesTransferredToLFC(), ua.getNumberOfFilesDeletedInLFC(), ua.getSizeOfFilesDeletedInLFC(), syncedLFCDir, true);
        HashMap<String, FileProperties> expLfcFiles = s.getLfcu().listLFCDir("/", ua);
        assertEquals(expLfcFiles.size(), lfcFiles.size() + sshFiles.size() - fileWithSameName);

    }

    @Test
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
        s.transferFilesFromSynchDeviceToLFC(ua, sshd, sshFiles, lfcFiles, ua.getNumberOfFilesTransferredToLFC(), ua.getSizeOfFilesTransferredToLFC(), ua.getNumberOfFilesDeletedInLFC(), ua.getSizeOfFilesDeletedInLFC(), syncedLFCDir, false);
        //LFC -> SyncedDevice
        LFCMonitorParams lFCMonitorParams = getLFCMonitorParams(ua.getEmail(), "/grid/biomed/creatis/vip/data/users/nouha_boujelben/NOUHA4_ssh");

        s.transferFilesFromLFCToSynchDevice(ua, sshFiles, lfcFiles, 0, ua.getNumberOfFilesTransferredToDevice(), ua.getSizeOfFilesTransferredToDevice(), lFCMonitorParams.getNumberOfFilesDeletedInLFC(), lFCMonitorParams.getSizeOfFilesDeletedInLFC(), lFCMonitorParams.getNumberOfFilesTransferredToLFC(), lFCMonitorParams.getSizeOfFilesTransferredToLFC(), ua.getNumberOfFilesDeletedInDevice(), ua.getSizeOfFilesDeletedInDevice(), syncedLFCDir, false, true, false);
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

}
