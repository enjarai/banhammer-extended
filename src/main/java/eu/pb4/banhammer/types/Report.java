package eu.pb4.banhammer.types;

import java.util.UUID;

public class Report {
    public final int id;
    public final UUID uuid;
    public final long time;
    public final String description;
    public final long x;
    public final long y;
    public final long z;
    public final boolean open;

    public Report(int id, UUID uuid, long time, String description, long x, long y, long z, boolean open) {
        this.id = id;
        this.uuid = uuid;
        this.time = time;
        this.description = description;
        this.x = x;
        this.y = y;
        this.z = z;
        this.open = open;
    }
}
