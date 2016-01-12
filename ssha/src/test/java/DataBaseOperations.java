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
import fr.insalyon.creatis.vip.ssha.SSHMySQLDAO;
import fr.insalyon.creatis.vip.synchronizedcommons.TransferType;
import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 *This class will insert a record into you table VIPSSHAccounts
 */
/**
 *
 * @author Nouha Boujelben
 */
public class DataBaseOperations {

    private static Connection connection;
    //the ssh user name
    String userName = "nouha";
    //the ssh server host
    String hostName = "localhost";
    //the ssh directory
    String directoryName = "/home/nouha/r";
    int sshPort = 22;
    String email = "nouha.boujelben@creatis.insa-lyon.fr";
    boolean validated = true;
    boolean authFailed = false;
    String syncedLFCDir = "/grid/biomed/creatis/vip/data/users/nouha_boujelben/kk_ssh";
    boolean deleteFilesFromSource = false;
    TransferType transferType = TransferType.Synchronization;
    private static String dbName = "vip";
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
                    "INSERT INTO VIPSSHAccounts(email, validated, auth_failed, LFCDir,transferType, sshUser, sshHost, sshDir, sshPort, deleteFilesFromSource) "
                    + "VALUES (?, ?, ?, ?,?, ?, ?, ?, ?, ?)");

            ps.setString(1, email);
            ps.setBoolean(2, validated);
            ps.setBoolean(3, authFailed);
            ps.setString(4, syncedLFCDir);
            ps.setString(5, transferType.toString());
            ps.setString(6, userName);
            ps.setString(7, hostName);
            ps.setString(8, directoryName);
            ps.setInt(9, sshPort);
            ps.setBoolean(10, deleteFilesFromSource);
            ps.execute();
            ps.close();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }
}
