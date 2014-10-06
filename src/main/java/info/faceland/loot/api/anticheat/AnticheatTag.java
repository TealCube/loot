package info.faceland.loot.api.anticheat;

import org.bukkit.Location;

import java.util.UUID;

public interface AnticheatTag {

    UUID getUniqueId();

    Location getLocation();

}
