package nl.enjarai.banhammer.types;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeenEntry {
    public final UUID uuid;
    public final String ip;
    public final String name;
    public final long time;
    public final Vec3d coords;

    public SeenEntry(UUID uuid, String ip, String name, long time, Vec3d coords) {
        this.uuid = uuid;
        this.ip = ip;
        this.name = name;
        this.time = time;
        this.coords = coords;
    }
}
