package eu.pb4.banhammer.config;


import java.util.Arrays;
import java.util.List;

public class MessageConfigData {
    public List<String> banChatMessage = Arrays.asList("Player <red><banned></red> has been banned by <gold><operator></gold>!",
            "Reason: <yellow><reason></yellow>. Expires in: <yellow><expiration_time></yellow>");

    public List<String> muteChatMessage = Arrays.asList("Player <red><banned></red> has been muted by <gold><operator></gold>!",
            "Reason: <yellow><reason></yellow>. Expires in: <yellow><expiration_time></yellow>");

    public List<String> kickChatMessage = Arrays.asList("Player <red><banned></red> has been kicked by <gold><operator></gold>!",
            "Reason: <yellow><reason></yellow>");

    public List<String> unbanChatMessage = Arrays.asList("Player <red><banned></red> has been unbanned by <gold><operator></gold>!");

    public List<String> unmuteChatMessage = Arrays.asList("Player <red><banned></red> has been unmuted by <gold><operator></gold>!");

    public List<String> pardonChatMessage = Arrays.asList("Punishments of player <red><banned></red> has been redeemed by <gold><operator></gold>!",
            "Reason: <yellow><reason></yellow>");

    public List<String> bannedScreen = Arrays.asList("<red><bold>You are banned</bold></red>",
            "<gray>Reason: </gray><yellow><reason></yellow>",
            "<gray>Expires in: </gray><yellow><expiration_time></yellow>",
            "<gray>By: </gray><yellow><operator></yellow>");

    public List<String> kickScreen = Arrays.asList("<red><bold>You has been kicked!</bold></red>",
            "<gray>Reason: </gray><yellow><reason></yellow>",
            "<gray>By: </gray><yellow><operator></yellow>");

    public List<String> mutedText = Arrays.asList("<red>You are muted for <expiration_time> by <operator>. Reason: <reason></red>");

    public String defaultReason = "Unknown reason";

    public String dateFormat = "dd.MM.YYYY HH:mm";
    public String neverExpiresText = "Never";
    public String foreverText = "Forever";

    public String yearsText = " year(s) ";
    public String daysText = " day(s) ";
    public String hoursText = " hour(s) ";
    public String minutesText = " minute(s) ";
    public String secondsText = " second(s)";
}