/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.managers;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.data.GemCacheData;
import info.faceland.loot.api.managers.GemCacheManager;
import info.faceland.loot.data.LootGemCacheData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class LootGemCacheManager implements GemCacheManager {

    private final LootPlugin plugin;
    private final Map<UUID, GemCacheData> gemCacheDataMap;

    public LootGemCacheManager(LootPlugin plugin) {
        this.plugin = plugin;
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
        GemCacheData data = new LootGemCacheData(plugin, uuid);
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
