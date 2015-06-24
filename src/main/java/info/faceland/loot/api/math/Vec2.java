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
package info.faceland.loot.api.math;

import com.tealcube.minecraft.bukkit.kern.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Preconditions;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.Splitter;

import java.util.List;

public class Vec2 {

    private final String world;
    private final int x;
    private final int z;

    public Vec2(String world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Vec2 add(Vec2 o) {
        Preconditions.checkNotNull(o);
        Preconditions.checkArgument(world.equals(o.getWorld()));
        return new Vec2(world, x + o.getX(), z + o.getZ());
    }

    public static Vec2 fromString(String s) {
        Preconditions.checkNotNull(s);
        List<String> split = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(s);
        return new Vec2(split.get(0), split.size() > 1 ? NumberUtils.toInt(split.get(1)) : 0,
                split.size() > 2 ? NumberUtils.toInt(split.get(2)) : 0);
    }

    @Override
    public String toString() {
        return world + ":" + x + ":" + z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vec2)) return false;

        Vec2 vec2 = (Vec2) o;

        return x == vec2.x && z == vec2.z && !(world != null ? !world.equals(vec2.world) : vec2.world != null);
    }

    @Override
    public int hashCode() {
        int result = world != null ? world.hashCode() : 0;
        result = 31 * result + x;
        result = 31 * result + z;
        return result;
    }

}
