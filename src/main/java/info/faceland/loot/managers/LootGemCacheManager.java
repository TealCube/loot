package info.faceland.loot.managers;

import info.faceland.loot.api.data.GemCacheData;
import info.faceland.loot.api.managers.GemCacheManager;
import info.faceland.loot.data.LootGemCacheData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class LootGemCacheManager implements GemCacheManager {

    private final Map<UUID, GemCacheData> gemCacheDataMap;

    public LootGemCacheManager() {
        gemCacheDataMap = new HashMap<>();
    }

    @Override
    public GemCacheData getGemCacheData(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        if (!hasGemCacheData(uuid)) {
            return createGemCacheData(uuid);
        }
        return gemCacheDataMap.get(uuid);
    }

    @Override
    public GemCacheData createGemCacheData(UUID uuid) {
        GemCacheData data = new LootGemCacheData(uuid);
        gemCacheDataMap.put(uuid, data);
        return data;
    }

    @Override
    public void addGemCacheData(GemCacheData data) {
        if (!hasGemCacheData(data.getOwner())) {
            gemCacheDataMap.put(data.getOwner(), data);
        }
    }

    @Override
    public void removeGemCacheData(UUID uuid) {
        if (hasGemCacheData(uuid)) {
            gemCacheDataMap.remove(uuid);
        }
    }

    @Override
    public void removeGemCacheData(GemCacheData data) {
        if (data != null) {
            removeGemCacheData(data.getOwner());
        }
    }

    @Override
    public Set<GemCacheData> getGemCacheData() {
        return new HashSet<>(gemCacheDataMap.values());
    }

    @Override
    public boolean hasGemCacheData(UUID uuid) {
        return uuid != null && gemCacheDataMap.containsKey(uuid);
    }
}
