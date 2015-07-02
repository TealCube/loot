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
    public boolean addTag(LivingEntity entity) {
        return entity != null && anticheatTagMap.put(entity.getUniqueId(), new LootAnticheatTag(entity)) != null;
    }

    @Override
    public boolean removeTag(LivingEntity entity) {
        return entity != null && anticheatTagMap.remove(entity.getUniqueId()) != null;
    }

    @Override
    public boolean pushTag(LivingEntity entity, AnticheatTag tag) {
        return entity != null && tag != null && anticheatTagMap.put(entity.getUniqueId(), tag) != null;
    }

    @Override
    public AnticheatTag getTag(LivingEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity cannot be null");
        }
        if (!isTagged(entity)) {
            throw new IllegalArgumentException("entity does not have tag");
        }
        return anticheatTagMap.get(entity.getUniqueId());
    }

}
