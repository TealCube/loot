package info.faceland.loot.anticheat;

import info.faceland.loot.api.anticheat.AnticheatTag;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public final class LootAnticheatTag implements AnticheatTag {

    private final UUID uuid;
    private final Location location;

    public LootAnticheatTag(LivingEntity entity) {
        this(entity.getUniqueId(), entity.getLocation());
    }

    public LootAnticheatTag(UUID uuid, Location location) {
        this.uuid = uuid;
        this.location = location;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Location getLocation() {
        return location;
    }

}
