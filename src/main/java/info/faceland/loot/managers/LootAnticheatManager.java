/******************************************************************************
 * Copyright (c) 2014, Richard Harrah                                         *
 *                                                                            *
 * Permission to use, copy, modify, and/or distribute this software for any   *
 * purpose with or without fee is hereby granted, provided that the above     *
 * copyright notice and this permission notice appear in all copies.          *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES   *
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF           *
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR    *
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES     *
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN      *
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF    *
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.             *
 ******************************************************************************/

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
