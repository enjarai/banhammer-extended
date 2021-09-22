package nl.enjarai.banhammer.types;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.TextParser;
import net.minecraft.text.LiteralText;
import nl.enjarai.banhammer.BanHammerMod;
import nl.enjarai.banhammer.Helpers;
import nl.enjarai.banhammer.config.ConfigManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.text.SimpleDateFormat;
import java.util.*;

public class BHPlayerData {
    public final UUID uuid;
    public final String name;
    public final String ip;
    public final Text displayName;
    public final ServerPlayerEntity player;

    public BHPlayerData(UUID uuid, String name, String ip, Text displayName, ServerPlayerEntity player) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.displayName = displayName;
        this.player = player;
    }

    public Map<String, Text> getPlaceholders() {
        HashMap<String, Text> list = new HashMap<>();

        list.put("player", this.displayName);
        list.put("uuid", new LiteralText(this.uuid.toString()));
        list.put("ip", new LiteralText(this.ip));

        return list;
    }

    public List<Text> getFormattedPunishmentHistory() {
        List<SyncedPunishment> history = BanHammerMod.DATABASE.getPunishmentHistory(this.uuid);

        List<Text> messageList = new ArrayList<>();
        messageList.add(PlaceholderAPI.parsePredefinedText(ConfigManager.getConfig().punishmentHistoryHeader,
                PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN, this.getPlaceholders()));

        if (history.isEmpty()) {
            messageList.add(PlaceholderAPI.parsePredefinedText(ConfigManager.getConfig().punishmentHistoryNoneEntry,
                    PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN, this.getPlaceholders()));

            return messageList;
        }
        Map<String, Text> placeholders;

        for (SyncedPunishment punishment : history) {
            placeholders = getPlaceholders();

            placeholders.put("time", new LiteralText(new SimpleDateFormat("dd/MM/yyyy HH:mm")
                    .format(new Date(punishment.time * 1000))));
            placeholders.put("type", punishment.type.displayName);
            placeholders.put("reason", new LiteralText(punishment.reason));
            placeholders.put("operator", punishment.adminDisplayName);
            placeholders.put("duration", new LiteralText(punishment.isTemporary() ?
                    " " + Helpers.getFormattedDuration(punishment.duration, false) : ""));

            messageList.add(PlaceholderAPI.parsePredefinedText(ConfigManager.getConfig().punishmentHistoryEntry,
                    PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN, placeholders));
        }

        return messageList;
    }
}
