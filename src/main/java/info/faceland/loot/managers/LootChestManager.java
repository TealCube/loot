package info.faceland.loot.managers;

import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Preconditions;
import info.faceland.loot.api.managers.ChestManager;
import info.faceland.loot.api.math.Vec3;

import java.util.HashSet;
import java.util.Set;

public class LootChestManager implements ChestManager {

    private final Set<Vec3> chestLocations;

    public LootChestManager() {
        chestLocations = new HashSet<>();
    }

    @Override
    public Set<Vec3> getChestLocations() {
        return new HashSet<>(chestLocations);
    }

    @Override
    public void addChestLocation(Vec3 vec) {
        Preconditions.checkNotNull(vec);
        chestLocations.add(vec);
    }

    @Override
    public void removeChestLocation(Vec3 vec) {
        Preconditions.checkNotNull(vec);
        chestLocations.remove(vec);
    }

}
