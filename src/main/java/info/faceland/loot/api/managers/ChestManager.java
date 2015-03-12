package info.faceland.loot.api.managers;

import info.faceland.loot.api.math.Vec3;

import java.util.Set;

public interface ChestManager {

    Set<Vec3> getChestLocations();

    void addChestLocation(Vec3 vec);

    void removeChestLocation(Vec3 vec);

}
