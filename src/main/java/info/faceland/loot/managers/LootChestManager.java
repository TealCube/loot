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

import com.tealcube.minecraft.bukkit.shade.google.common.base.Preconditions;
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
