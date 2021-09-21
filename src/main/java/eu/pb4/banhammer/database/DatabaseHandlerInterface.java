package eu.pb4.banhammer.database;

import eu.pb4.banhammer.types.BasicPunishment;
import eu.pb4.banhammer.types.PunishmentTypes;
import eu.pb4.banhammer.types.SeenEntry;
import eu.pb4.banhammer.types.SyncedPunishment;

import java.util.List;
import java.util.UUID;

public interface DatabaseHandlerInterface {
    boolean insertPunishment(BasicPunishment punishment);
    boolean insertSeenEntry(SeenEntry entry);
    List<SyncedPunishment> getPunishments(String id, PunishmentTypes type);

    SeenEntry getLatestSeen(UUID uuid);

    List<SyncedPunishment> getAllPunishments(PunishmentTypes type);
    int removePunishment(long id, PunishmentTypes type);
    int removePunishment(String id, PunishmentTypes type);

    void closeConnection();

    boolean insertPunishmentIntoHistory(BasicPunishment punishment);
}
