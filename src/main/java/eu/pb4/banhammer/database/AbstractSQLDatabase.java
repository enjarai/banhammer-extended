package eu.pb4.banhammer.database;

import com.google.common.net.InetAddresses;
import eu.pb4.banhammer.types.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public abstract class AbstractSQLDatabase implements DatabaseHandlerInterface {
    protected Connection conn;
    protected Statement stat;


    protected abstract String getTableCreation();
    protected abstract String getHistoryTableCreation();
    protected abstract String getSeenTableCreation();
    protected abstract String getReportsTableCreation();

    public void createTables() throws SQLException  {
        String create = this.getTableCreation();
        String createHistory = this.getHistoryTableCreation();
        String createSeen = this.getSeenTableCreation();
        String createReports = this.getReportsTableCreation();

        stat.execute(String.format(create, PunishmentTypes.BAN.databaseName));
        stat.execute(String.format(create, PunishmentTypes.IPBAN.databaseName));
        stat.execute(String.format(create, PunishmentTypes.MUTE.databaseName));
        stat.execute(createHistory);
        stat.execute(createSeen);
        stat.execute(createReports);
    }

    public boolean insertPunishmentIntoHistory(BasicPunishment punishment) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into history values (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            prepStmt.setString(1, punishment.bannedUUID.toString());
            prepStmt.setString(2, punishment.bannedIP);
            prepStmt.setString(3, punishment.bannedName);
            prepStmt.setString(4, Text.Serializer.toJson(punishment.bannedDisplayName));
            prepStmt.setString(5, punishment.adminUUID.toString());
            prepStmt.setString(6, Text.Serializer.toJson(punishment.adminDisplayName));
            prepStmt.setString(7, String.valueOf(punishment.time));
            prepStmt.setString(8, String.valueOf(punishment.duration));
            prepStmt.setString(9, punishment.reason);
            prepStmt.setString(10, punishment.type.name);

            prepStmt.setQueryTimeout(10);

            prepStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean insertPunishment(BasicPunishment punishment) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into " + punishment.type.databaseName + " values (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            prepStmt.setString(1, punishment.bannedUUID.toString());
            prepStmt.setString(2, punishment.bannedIP);
            prepStmt.setString(3, punishment.bannedName);
            prepStmt.setString(4, Text.Serializer.toJson(punishment.bannedDisplayName));
            prepStmt.setString(5, punishment.adminUUID.toString());
            prepStmt.setString(6, Text.Serializer.toJson(punishment.adminDisplayName));
            prepStmt.setString(7, String.valueOf(punishment.time));
            prepStmt.setString(8, String.valueOf(punishment.duration));
            prepStmt.setString(9, punishment.reason);

            prepStmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertSeenEntry(SeenEntry entry) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into seen values (NULL, ?, ?, ?, ?, ?, ?, ?);");
            prepStmt.setString(1, entry.uuid.toString());
            prepStmt.setString(2, entry.ip);
            prepStmt.setString(3, entry.name.toString());
            prepStmt.setString(4, String.valueOf(entry.time));
            prepStmt.setString(5, String.valueOf((long) entry.coords.x));
            prepStmt.setString(6, String.valueOf((long) entry.coords.y));
            prepStmt.setString(7, String.valueOf((long) entry.coords.z));

            prepStmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean insertReport(Report report) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "insert into reports values (NULL, ?, ?, ?, ?, ?, ?, ?);");
            prepStmt.setString(1, report.uuid.toString());
            prepStmt.setString(2, String.valueOf(report.time));
            prepStmt.setString(3, report.description);
            prepStmt.setString(4, String.valueOf(report.x));
            prepStmt.setString(5, String.valueOf(report.y));
            prepStmt.setString(6, String.valueOf(report.z));
            prepStmt.setString(7, report.open ? "1" : "0");

            prepStmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean closeReport(Report report) {
        try {
            PreparedStatement prepStmt = conn.prepareStatement(
                    "UPDATE reports SET open = 0 WHERE id = ?;");
            prepStmt.setString(1, String.valueOf(report.id));

            prepStmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<SyncedPunishment> getPunishments(String id, PunishmentTypes type) {
        List<SyncedPunishment> list = new LinkedList<>();
        try {
            String query = "SELECT * FROM " + type.databaseName + " WHERE " + (InetAddresses.isInetAddress(id) ? "bannedIP" : "bannedUUID") + "='" + id + "';";
            ResultSet result = stat.executeQuery(query);
            UUID bannedUUID;
            String bannedIP;
            String bannedNameRaw;
            Text bannedName;
            UUID adminUUID;
            Text adminName;
            long time;
            long duration;
            String reason;


            while(result.next()) {
                long idd = result.getLong("id");
                bannedUUID = UUID.fromString(result.getString("bannedUUID"));
                bannedIP = result.getString("bannedIP");
                bannedNameRaw = result.getString("bannedName");
                bannedName = Text.Serializer.fromJson(result.getString("bannedDisplay"));
                adminUUID = UUID.fromString(result.getString("adminUUID"));
                adminName = Text.Serializer.fromJson(result.getString("adminDisplay"));
                time = result.getLong("time");
                duration = result.getLong("duration");
                reason = result.getString("reason");

                list.add(new SyncedPunishment(idd, bannedUUID, bannedIP, bannedName, bannedNameRaw, adminUUID, adminName, time, duration, reason, type));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    @Override
    public SeenEntry getLatestSeen(UUID uuid) {
        try {
            String query = "SELECT * FROM seen WHERE UUID='" + uuid + "' ORDER BY id DESC LIMIT 1;";
            ResultSet result = stat.executeQuery(query);

            if (!result.next()) { return null; }

            UUID uuid1 = UUID.fromString(result.getString("UUID"));
            String ip = result.getString("IP");
            String name = result.getString("Name");
            long time = result.getLong("time");
            long x = result.getLong("x");
            long y = result.getLong("y");
            long z = result.getLong("z");
            Vec3d coords = new Vec3d((double) x, (double) y, (double) z);

            SeenEntry entry = new SeenEntry(uuid1, ip, name, time, coords);

            return entry;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Report> getOpenReports() {
        List<Report> list = new LinkedList<>();
        try {
            String query = "SELECT * FROM reports WHERE open = 1;";
            ResultSet result = stat.executeQuery(query);
            int id;
            UUID uuid;
            long time;
            String description;
            long x;
            long y;
            long z;
            boolean open;


            while(result.next()) {
                id = result.getInt("id");
                uuid = UUID.fromString(result.getString("uuid"));
                time = result.getLong("time");
                description = result.getString("description");
                x = result.getLong("x");
                y = result.getLong("y");
                z = result.getLong("z");
                open = result.getBoolean("open");

                list.add(new Report(id, uuid, time, description, x, y, z, open));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    @Override
    public List<SyncedPunishment> getAllPunishments(PunishmentTypes type) {
        List<SyncedPunishment> list = new LinkedList<>();
        try {
            String query = "SELECT * FROM " + type.databaseName + ";";
            ResultSet result = stat.executeQuery(query);
            UUID bannedUUID;
            String bannedIP;
            String bannedNameRaw;
            Text bannedName;
            UUID adminUUID;
            Text adminName;
            long time;
            long duration;
            String reason;


            while(result.next()) {
                long idd = result.getLong("id");
                bannedUUID = UUID.fromString(result.getString("bannedUUID"));
                bannedIP = result.getString("bannedIP");
                bannedNameRaw = result.getString("bannedName");
                bannedName = Text.Serializer.fromJson(result.getString("bannedDisplay"));
                adminUUID = UUID.fromString(result.getString("adminUUID"));
                adminName = Text.Serializer.fromJson(result.getString("adminDisplay"));
                time = result.getLong("time");
                duration = result.getLong("duration");
                reason = result.getString("reason");

                list.add(new SyncedPunishment(idd, bannedUUID, bannedIP, bannedName, bannedNameRaw, adminUUID, adminName, time, duration, reason, type));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    @Override
    public int removePunishment(long id, PunishmentTypes type) {
        try {
            return stat.executeUpdate("DELETE FROM " + type.databaseName + " WHERE id=" + id + ";");
        } catch (Exception x) {
            x.printStackTrace();
            return 0;
        }
    }

    @Override
    public int removePunishment(String id, PunishmentTypes type) {
        try {
            return stat.executeUpdate("DELETE FROM " + type.databaseName + " WHERE " + (InetAddresses.isInetAddress(id) ? "bannedIP" : "bannedUUID") + "='" + id + "';");
        } catch (Exception x) {
            x.printStackTrace();
            return 0;
        }
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
