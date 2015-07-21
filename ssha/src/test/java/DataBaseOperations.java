
import fr.insalyon.creatis.vip.ssha.SSHMySQLDAO;
import fr.insalyon.creatis.vip.ssha.SSHSynchronization;
import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nouha Boujelben
 */
public class DataBaseOperations {

    private static Connection connection;
    //the ssh user name
    String userName = "";
    //the ssh server host
    String hostName = "";
    //the ssh directory
    String directoryName = "";
    int sshPort = 22;
    String email = "nouha.boujelben@creatis.insa-lyon.fr";
    boolean validated = true;
    boolean authFailed = false;
    String syncedLFCDir = "/grid/biomed/creatis/vip/data/users/nouha_boujelben/kk_ssh";
    //String transfertType = "Synchronization";
    private static String dbName = "test";
    private static String user = "root";
    private static String password = "nouhanouha";
    private static String jdbUrl = "jdbc:mysql://localhost/" + dbName;

    @BeforeClass
    public static void onceExecutedBeforeAll() throws SyncException {
        System.setProperty("logfile.name", "./ssha.log");
        connection = SSHMySQLDAO.getInstance(jdbUrl, user, password).getConnection();

    }

    @Test
    public void addRecord() {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO VIPSSHAccounts(email, validated, auth_failed, LFCDir,transfertType, sshUser, sshHost, sshDir, sshPort) "
                    + "VALUES (?, ?, ?, ?,?, ?, ?, ?)");

            ps.setString(1, email);
            ps.setBoolean(2, validated);
            ps.setBoolean(3, authFailed);
            ps.setString(4, syncedLFCDir);
            //ps.setString(5, transfertType);
            ps.setString(6, userName);
            ps.setString(7, hostName);
            ps.setString(8, directoryName);
            ps.setInt(9, sshPort);
            ps.execute();
            ps.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }
}
