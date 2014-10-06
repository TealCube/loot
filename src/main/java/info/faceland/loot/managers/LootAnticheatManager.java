package info.faceland.loot.managers;

import info.faceland.loot.anticheat.LootAnticheatTag;
import info.faceland.loot.api.anticheat.AnticheatTag;
import info.faceland.loot.api.managers.AnticheatManager;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class LootAnticheatManager implements AnticheatManager {

    private final Map<UUID, AnticheatTag> anticheatTagMap;

    public LootAnticheatManager() {
        this.anticheatTagMap = new HashMap<>();
    }

    @Override
    public boolean isTagged(LivingEntity entity) {
        return entity != null && anticheatTagMap.containsKey(entity.getUniqueId());
    }

    @Override
    public boolean push(LivingEntity entity) {
        return entity != null && anticheatTagMap.put(entity.getUniqueId(), new LootAnticheatTag(entity)) != null;
    }

    @Override
    public boolean pull(LivingEntity entity) {
        return entity != null && anticheatTagMap.remove(entity.getUniqueId()) != null;
    }

    @Override
    public AnticheatTag tag(LivingEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity cannot be null");
        }
        if (!isTagged(entity)) {
            throw new IllegalArgumentException("entity does not have tag");
        }
        return anticheatTagMap.get(entity.getUniqueId());
    }

}
