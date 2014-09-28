package info.faceland.loot.managers;

import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.managers.CreatureModManager;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootCreatureModManager implements CreatureModManager {

    private final Map<EntityType, CreatureMod> creatureModMap;

    public LootCreatureModManager() {
        this.creatureModMap = new HashMap<>();
    }

    @Override
    public Set<CreatureMod> getCreatureMods() {
        return new HashSet<>(creatureModMap.values());
    }

    @Override
    public CreatureMod getCreatureMod(EntityType entityType) {
        return null;
    }

    @Override
    public void addCreatureMod(CreatureMod mod) {
        if (mod != null) {
            creatureModMap.put(mod.getEntityType(), mod);
        }
    }

    @Override
    public void removeCreatureMod(EntityType entityType) {
        if (entityType != null) {
            creatureModMap.remove(entityType);
        }
    }

}
