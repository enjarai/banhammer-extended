package nl.enjarai.banhammer.database;

import nl.enjarai.banhammer.types.BasicPunishment;
import nl.enjarai.banhammer.types.PunishmentTypes;
import nl.enjarai.banhammer.types.SeenEntry;
import nl.enjarai.banhammer.types.SyncedPunishment;

import java.util.List;
import java.util.UUID;

public interface DatabaseHandlerInterface {
    boolean insertPunishment(BasicPunishment punishment);
    boolean insertSeenEntry(SeenEntry entry);
    List<SyncedPunishment> getPunishments(String id, PunishmentTypes type);

    SeenEntry getLatestSeen(UUID uuid);

    List<SyncedPunishment> getAllPunishments(PunishmentTypes type);

    List<SyncedPunishment> getPunishmentHistory(UUID uuid);

    int removePunishment(long id, PunishmentTypes type);
    int removePunishment(String id, PunishmentTypes type);

    void closeConnection();

    boolean insertPunishmentIntoHistory(BasicPunishment punishment);
}
