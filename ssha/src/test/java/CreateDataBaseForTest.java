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
 * you have to start the service mysqld if it is not the case (service mysqld
 * start) have to change some parameters(user, password) this class will create
 * the a local data base for test
 */
public class CreateDataBaseForTest {

    private static String jdbcDriver = "com.mysql.jdbc.Driver";
    //the name of the database
    private static String dbName = "vip";
    //user
    private static String user = "root";
    //password of the MYSQL server
    private static String password = "nouhanouha";
    private static String jdbUrl = "jdbc:mysql://localhost/" + dbName;

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
        s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
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
