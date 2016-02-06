package info.faceland.loot.api.managers;

import info.faceland.loot.api.data.GemCacheData;

import java.util.Set;
import java.util.UUID;

public interface GemCacheManager {

    GemCacheData getGemCacheData(UUID uuid);

    GemCacheData createGemCacheData(UUID uuid);

    void addGemCacheData(GemCacheData data);

    void removeGemCacheData(UUID uuid);

    void removeGemCacheData(GemCacheData data);

    Set<GemCacheData> getGemCacheData();

    boolean hasGemCacheData(UUID uuid);

}
