/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.ssha;

import fr.insalyon.creatis.vip.synchronizedcommons.SyncedDeviceDAO;
import fr.insalyon.creatis.vip.synchronizedcommons.Synchronization;
import fr.insalyon.creatis.vip.synchronizedcommons.business.SyncException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * DAO for SSH synchronizations.
 * @author Tristan Glatard
 */
public class SSHMySQLDAO implements SyncedDeviceDAO {

    private static SSHMySQLDAO instance = null;
    private Connection connection;

    private final static Logger logger = Logger.getLogger(SSHMySQLDAO.class);
    
    private SSHMySQLDAO(String jdbcUrl, String userName, String password) throws SyncException {      
            connect(jdbcUrl, userName, password);
            createTable();
    }

    public static SSHMySQLDAO getInstance(String jdbcUrl, String userName, String password) throws SyncException {
        try {
            if (instance == null) {
                instance = new SSHMySQLDAO(jdbcUrl, userName, password);
            }
            if (!instance.getConnection().isValid(3)) {
                instance.getConnection().close();
                instance = new SSHMySQLDAO(jdbcUrl, userName, password);
            }
        } catch (SQLException ex) {
            throw new SyncException(ex);
        }
        return instance;

    }

    @Override
    public void validateSynchronization(Synchronization ua) throws SyncException {
        try {
            logger.info("Validating SSH user account: "+ua.toString());
         PreparedStatement ps = connection.prepareStatement("UPDATE "
                 + "VIPSSHAccounts SET "
                 + "validated = '1' "
                 + "WHERE email = ? AND LFCDir = ?");

         ps.setString(1, ua.getEmail());
         ps.setString(2, ua.getSyncedLFCDir());

         ps.executeUpdate();
         ps.close();
        } catch (SQLException ex) {
            throw new SyncException(ex);
        }
    }

    @Override
    public void setAuthFailed(Synchronization ua) throws SyncException {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE "
                    + "VIPSSHAccounts SET "
                    + "auth_failed = '1' "
                    + "WHERE email = ? and LFCDir=?");

            ps.setString(1, ua.getEmail());
            ps.setString(2, ua.getSyncedLFCDir());
            ps.executeUpdate();
            ps.close();

        } catch (SQLException ex) {
            throw new SyncException(ex);
        }
    }

    @Override
    public List<Synchronization> getSynchronizations() throws SyncException {
        ArrayList<Synchronization> userAccounts = new ArrayList<Synchronization>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT "
                    + " * "
                    + "FROM VIPSSHAccounts");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String val = rs.getString("validated");
                Synchronization ua = new SSHSynchronization(rs.getString("email"),rs.getBoolean("validated"),rs.getBoolean("auth_failed"),rs.getString("LFCDir"),
                        rs.getString("sshUser"),rs.getString("sshHost"),rs.getString("sshDir"),rs.getInt("sshPort"));
                userAccounts.add(ua);
            }

            ps.close();
            return userAccounts;

        } catch (SQLException ex) {
           throw new SyncException(ex);
        }
    }

    private void createTable() throws SyncException{
        
         try {
            Statement stat = connection.createStatement();
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS VIPSSHAccounts (email VARCHAR(255), LFCDir VARCHAR(255), "
                    + "sshUser VARCHAR(255), sshHost VARCHAR(255), sshDir VARCHAR(255), sshPort INT, validated BOOLEAN,"
                    + " auth_failed BOOLEAN, PRIMARY KEY(email,LFCDir)) ENGINE=InnoDB");

            logger.info("Table VIPSSHAccounts successfully created.");

        } catch (SQLException ex) {
            throw new SyncException(ex);
        }
        
    }
    
    public Connection getConnection() {
        return connection;
    }

    private void connect(String jdbcUrl, String userName, String password) throws SyncException  {
        try {
            //TODO put this into config file
            String driver = "com.mysql.jdbc.Driver";
            try {
                Class.forName(driver).newInstance();
            } catch (InstantiationException ex) {
                throw new SyncException(ex);
            } catch (IllegalAccessException ex) {
                throw new SyncException(ex);
            }
            try { 
                connection = DriverManager.getConnection(jdbcUrl, userName, password);
            } catch (SQLException ex) {
                throw new SyncException(ex);
            }
        } catch (ClassNotFoundException ex) {
                throw new SyncException(ex);
        }
    }
}
