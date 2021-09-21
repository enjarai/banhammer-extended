package eu.pb4.banhammer.database;

import eu.pb4.banhammer.types.*;

import java.util.List;
import java.util.UUID;

public interface DatabaseHandlerInterface {
    boolean insertPunishment(BasicPunishment punishment);
    boolean insertSeenEntry(SeenEntry entry);
    boolean insertReport(Report report);
    boolean closeReport(Report report);
    List<SyncedPunishment> getPunishments(String id, PunishmentTypes type);

    SeenEntry getLatestSeen(UUID uuid);

    List<Report> getOpenReports();

    List<SyncedPunishment> getAllPunishments(PunishmentTypes type);
    int removePunishment(long id, PunishmentTypes type);
    int removePunishment(String id, PunishmentTypes type);

    void closeConnection();

    boolean insertPunishmentIntoHistory(BasicPunishment punishment);
}
