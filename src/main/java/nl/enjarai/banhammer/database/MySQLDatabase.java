package nl.enjarai.banhammer.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabase extends AbstractSQLDatabase {
    public MySQLDatabase(String address, String database, String username, String password) throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://" + address + "/" + database, username, password);
        stat = conn.createStatement();

        this.createTables();
    }

    @Override
    protected String getTableCreation() {
        return "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "bannedUUID varchar(36), bannedIP varchar(15), bannedName varchar(64), bannedDisplay varchar(512), " +
                "adminUUID varchar(36), adminDisplay TEXT, time BIGINT, duration BIGINT, reason varchar(128))";
    }

    @Override
    protected String getHistoryTableCreation() {
        return "CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "bannedUUID varchar(36), bannedIP varchar(15), bannedName varchar(64), bannedDisplay varchar(512), " +
                "adminUUID varchar(36), adminDisplay TEXT, time BIGINT, duration BIGINT, reason varchar(128), type varchar(16))";
    }

    @Override
    protected String getSeenTableCreation() {
        return null;
    }
}
