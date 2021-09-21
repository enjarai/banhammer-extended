package eu.pb4.banhammer.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.banhammer.BanHammerMod;
import eu.pb4.banhammer.Helpers;
import eu.pb4.banhammer.config.ConfigManager;
import eu.pb4.banhammer.types.BHPlayerData;
import eu.pb4.banhammer.types.PunishmentTypes;
import eu.pb4.banhammer.types.SeenEntry;
import eu.pb4.placeholders.PlaceholderAPI;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GeneralCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("banhammer")
                            .requires(Permissions.require("banhammer.commands.main", true))
                            .executes(GeneralCommands::about)
                            .then(literal("reload")
                                    .requires(Permissions.require("banhammer.commands.reload", 4))
                                    .executes(GeneralCommands::reloadConfig)
                            )
                            .then(literal("import")
                                    .requires(Permissions.require("banhammer.commands.import", 4))
                                    .then(importArgument("source")
                                            .executes(GeneralCommands::importer)
                                            .then(CommandManager.argument("remove", BoolArgumentType.bool())
                                                    .executes(GeneralCommands::importer)
                                            )
                                    )
                            )
                );
            dispatcher.register(literal("seen")
                    .requires(Permissions.require("banhammer.seen", 3))
                    .then(playerArgument("player")
                            .executes(GeneralCommands::seenCommand)
                    )
            );
            });
        }

    private static int seenCommand(CommandContext<ServerCommandSource> context) {
        String playerNameOrIp = context.getArgument("player", String.class);
        BHPlayerData player = Helpers.lookupPlayerDataNoPunishment(playerNameOrIp);

        if (player == null) {
            context.getSource().sendFeedback(new LiteralText("Invalid Player"), false);
            return 1;
        }

        Text message;
        if (BanHammerMod.SERVER.getPlayerManager().getPlayer(player.uuid) == null) {
            SeenEntry entry = BanHammerMod.getSeenEntry(player.uuid);

            if (entry == null) {
                message = new LiteralText("That player has never been online");
            } else {
                HashMap<String, Text> placeholders = new HashMap<>();

                placeholders.put("player", new LiteralText(entry.name));
                placeholders.put("uuid", new LiteralText(entry.uuid.toString()));
                placeholders.put("ip", new LiteralText(entry.ip));
                placeholders.put("x", new LiteralText(String.valueOf((int) entry.coords.x)));
                placeholders.put("y", new LiteralText(String.valueOf((int) entry.coords.y)));
                placeholders.put("z", new LiteralText(String.valueOf((int) entry.coords.z)));
                placeholders.put("time", new LiteralText(Helpers.getFormattedDuration(entry.time)));

                message = PlaceholderAPI.parsePredefinedText(ConfigManager.getConfig().seenChatMessage,
                        PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN, placeholders);
            }
        } else {
            HashMap<String, Text> placeholders = new HashMap<>();

            placeholders.put("player", new LiteralText(player.name));
            placeholders.put("uuid", new LiteralText(player.uuid.toString()));
            placeholders.put("ip", new LiteralText(player.ip));

            message = PlaceholderAPI.parsePredefinedText(ConfigManager.getConfig().seenChatMessageOnline,
                    PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN, placeholders);
        }
        context.getSource().sendFeedback(message, false);

        return 1;
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        if (ConfigManager.loadConfig()) {
            context.getSource().sendFeedback(new LiteralText("Reloaded config!"), false);
        } else {
            context.getSource().sendError(new LiteralText("Error accrued while reloading config!").formatted(Formatting.RED));
        }
        return 1;
    }

    private static int about(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(new LiteralText("BanHammer").formatted(Formatting.RED)
                        .append(new LiteralText(" - " + BanHammerMod.VERSION).formatted(Formatting.WHITE)), false);
        return 1;
    }

    private static int importer(CommandContext<ServerCommandSource> context) {
        String type = context.getArgument("source", String.class);
        boolean remove;
        try {
            remove = context.getArgument("remove", Boolean.class);
        } catch (Exception e) {
            remove = false;
        }

        BanHammerMod.PunishmentImporter importer = BanHammerMod.IMPORTERS.get(type);

        if (importer != null) {
            boolean result = importer.importPunishments(remove);

            if (result) {
                context.getSource().sendFeedback(new LiteralText("Successfully imported punishments!").formatted(Formatting.GREEN), false);
                return 1;
            } else {
                context.getSource().sendError(new LiteralText("Couldn't import punishments!"));
                return 0;
            }
        } else {
            context.getSource().sendError(new LiteralText("Invalid importer type!"));
            return 0;
        }

    }



    public static RequiredArgumentBuilder<ServerCommandSource, String> importArgument(String name) {
        return CommandManager.argument(name, StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

                    for (String type : BanHammerMod.IMPORTERS.keySet()) {
                        if (type.contains(remaining)) {
                            builder.suggest(type);
                        }
                    }

                    return builder.buildFuture();
                });
    }

    public static RequiredArgumentBuilder<ServerCommandSource, String> playerArgument(String name) {
        return CommandManager.argument(name, StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

                    for (String player : ctx.getSource().getServer().getPlayerNames()) {
                        if (player.toLowerCase(Locale.ROOT).contains(remaining)) {
                            builder.suggest(player);
                        }
                    }

                    return builder.buildFuture();
                });
    }
}
