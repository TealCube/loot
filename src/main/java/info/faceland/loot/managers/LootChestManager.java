/*
 * This file is part of Loot, licensed under the ISC License.
 *
 * Copyright (c) 2014 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
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
