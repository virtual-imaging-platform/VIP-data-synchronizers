
import fr.insalyon.creatis.vip.ssha.SSHMySQLDAO;
import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Nouha Boujelben this class will create a local data base for test you
 * have to change some parameters
 */
public class DataBaseTest {

    private static String jdbcDriver = "com.mysql.jdbc.Driver";
    //the name of the data base
    private static String dbName = "changeit";
    //user
    private static String user = "changeit";
    //password of the MYSQL server
    private static String password = "nouhanouha";
    private static String jdbUrl = "jdbc:mysql://localhost/" + dbName;

    /**
     *
     */
    @BeforeClass
    public static void onceExecutedBeforeAll() {
        System.setProperty("logfile.name", "./ssha.log");
    }

    /**
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void createDataBase() throws SQLException, ClassNotFoundException {
        Class.forName(jdbcDriver);
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/?user=" + user + "&password=" + password);
        Statement s = conn.createStatement();
        int Result = s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
    }

    /**
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws SyncException
     */
    @Test
    public void createTables() throws SQLException, ClassNotFoundException, SyncException {
        SSHMySQLDAO.getInstance(jdbUrl, user, password);
    }
}
