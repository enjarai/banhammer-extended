package eu.pb4.banhammer.mixin;

import com.mojang.authlib.GameProfile;
import eu.pb4.banhammer.BanHammerMod;
import eu.pb4.banhammer.Helpers;
import eu.pb4.banhammer.config.ConfigManager;
import eu.pb4.banhammer.database.AbstractSQLDatabase;
import eu.pb4.banhammer.types.BasicPunishment;
import eu.pb4.banhammer.types.PunishmentTypes;
import eu.pb4.banhammer.types.SeenEntry;
import eu.pb4.banhammer.types.SyncedPunishment;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;
import java.util.List;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    private void cachePlayersIP(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (connection != null && connection.getAddress() != null) {
            BanHammerMod.IP_CACHE.put(player.getUuid().toString(), Helpers.stringifyAddress(connection.getAddress()));
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void addToSeen(ServerPlayerEntity player, CallbackInfo ci) {
        SeenEntry entry = new SeenEntry(
                player.getUuid(),
                player.getIp(),
                player.getEntityName(),
                System.currentTimeMillis() / 1000,
                player.getPos()
        );

        BanHammerMod.addSeenEntry(entry);
    }

    @Inject(method = "checkCanJoin", at = @At("TAIL"), cancellable = true)
    private void checkIfBanned(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> cir) {
        BasicPunishment punishment = null;

        if (address == null || profile == null) {
            return;
        }

        String ip = Helpers.stringifyAddress(address);

        final List<SyncedPunishment> bans = BanHammerMod.getPlayersPunishments(profile.getId().toString(), PunishmentTypes.BAN);
        final List<SyncedPunishment> ipBans = BanHammerMod.getPlayersPunishments(ip, PunishmentTypes.IPBAN);

        if (bans.size() > 0) {
            punishment = bans.get(0);
        } else if (ipBans.size() > 0) {
            punishment = ipBans.get(0);
        }

        if (punishment != null) {
            if (punishment.type == PunishmentTypes.IPBAN && ConfigManager.getConfig().configData.standardBanPlayersWithBannedIps) {
                final boolean silent = ConfigManager.getConfig().configData.autoBansFromIpBansAreSilent;

                BasicPunishment punishment1 = new BasicPunishment(profile.getId(), Helpers.stringifyAddress(address), new LiteralText(profile.getName()), profile.getName(),
                        punishment.adminUUID,
                        punishment.adminDisplayName,
                        punishment.time,
                        punishment.duration,
                        punishment.reason,
                        PunishmentTypes.BAN);

                BanHammerMod.punishPlayer(punishment1, silent, silent);
            }
            cir.setReturnValue(punishment.getDisconnectMessage());
        }
    }
}
