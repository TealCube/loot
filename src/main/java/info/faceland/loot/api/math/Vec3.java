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

public class Vec3 extends Vec2 {

    private final int y;

    public Vec3(String world, int x, int y, int z) {
        super(world, x, z);
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public Vec3 add(Vec3 o) {
        Preconditions.checkNotNull(o);
        Preconditions.checkArgument(getWorld().equals(o.getWorld()));
        return new Vec3(getWorld(), getX() + o.getX(), y + o.getY(), getZ() + o.getZ());
    }

    public static Vec3 fromString(String s) {
        Preconditions.checkNotNull(s);
        List<String> split = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(s);
        return new Vec3(split.get(0), split.size() > 1 ? NumberUtils.toInt(split.get(1)) : 0,
                split.size() > 2 ? NumberUtils.toInt(split.get(2)) : 0,
                split.size() > 3 ? NumberUtils.toInt(split.get(3)) : 0);
    }

    @Override
    public String toString() {
        return getWorld() + ":" + getX() + ":" + getY() + ":" + getZ();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vec3)) return false;
        if (!super.equals(o)) return false;

        Vec3 vec3 = (Vec3) o;

        return y == vec3.y;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + y;
        return result;
    }
}
