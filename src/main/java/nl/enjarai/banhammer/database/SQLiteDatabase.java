package nl.enjarai.banhammer.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase extends AbstractSQLDatabase {
    public SQLiteDatabase(String database) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + database);

        stat = conn.createStatement();
        this.createTables();
    }

    @Override
    protected String getTableCreation() {
        return "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bannedUUID varchar(36), bannedIP varchar(15), bannedName varchar(64), bannedDisplay varchar(512), " +
                "adminUUID varchar(36), adminDisplay TEXT, time BIGINT, duration BIGINT, reason varchar(128))";
    }

    @Override
    protected String getHistoryTableCreation() {
        return "CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "bannedUUID varchar(36), bannedIP varchar(15), bannedName varchar(64), bannedDisplay varchar(512), " +
                "adminUUID varchar(36), adminDisplay TEXT, time BIGINT, duration BIGINT, reason varchar(128), type varchar(16))";
    }

    @Override
    protected String getSeenTableCreation() {
        return "CREATE TABLE IF NOT EXISTS seen (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "UUID varchar(36), IP varchar(15), Name varchar(64), time BIGINT, x BIGINT, y BIGINT, z BIGINT)";
    }
}
