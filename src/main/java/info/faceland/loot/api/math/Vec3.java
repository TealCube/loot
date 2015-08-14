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
package info.faceland.loot.api.math;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Preconditions;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Splitter;

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
