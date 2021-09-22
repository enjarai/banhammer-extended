package nl.enjarai.banhammer.types;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum PunishmentTypes {
    BAN("ban", true, false, "bans", new LiteralText("Ban").formatted(Formatting.RED)),
    IPBAN("ipban",true, true, "ipbans", new LiteralText("IP-Ban").formatted(Formatting.DARK_RED)),
    MUTE("mute",false, false, "mutes", new LiteralText("Mute").formatted(Formatting.YELLOW)),
    KICK("kick", true, false, null, new LiteralText("Kick").formatted(Formatting.GREEN));


    public final boolean ipBased;
    public final boolean kick;
    public final String databaseName;
    public final String name;
    public final Text displayName;

    PunishmentTypes(String name, boolean shouldKick, boolean ipBased, String databaseName, Text displayName) {
        this.name = name;
        this.ipBased = ipBased;
        this.kick = shouldKick;
        this.databaseName = databaseName;
        this.displayName = displayName;
    }
}
