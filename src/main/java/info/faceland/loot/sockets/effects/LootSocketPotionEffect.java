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
package info.faceland.loot.sockets.effects;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import info.faceland.loot.api.sockets.effects.SocketEffectTarget;
import info.faceland.loot.api.sockets.effects.SocketPotionEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class LootSocketPotionEffect implements SocketPotionEffect {

    private final PotionEffectType type;
    private final int duration;
    private final int intensity;
    private final int radius;
    private final SocketEffectTarget target;

    public LootSocketPotionEffect(PotionEffectType type, int duration, int intensity, int radius,
                                  SocketEffectTarget target) {
        this.type = type;
        this.duration = duration;
        this.intensity = intensity;
        this.radius = radius;
        this.target = target;
    }

    public static LootSocketPotionEffect parseString(String s) {
        String[] split = s.split(":");
        if (split.length < 5) {
            throw new IllegalArgumentException("not proper amount of arguments");
        }
        PotionEffectType type = PotionEffectType.getByName(split[0]);
        SocketEffectTarget targ = SocketEffectTarget.valueOf(split[1]);
        int duration = NumberUtils.toInt(split[2]);
        int intensity = NumberUtils.toInt(split[3]);
        int radius = NumberUtils.toInt(split[4]);
        return new LootSocketPotionEffect(type, duration, intensity, radius, targ);
    }

    @Override
    public PotionEffectType getPotionEffectType() {
        return type;
    }

    @Override
    public SocketEffectTarget getTarget() {
        return target;
    }

    @Override
    public int getRadius() {
        return radius;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getIntensity() {
        return intensity;
    }

    @Override
    public void apply(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        entity.removePotionEffect(type);
        entity.addPotionEffect(new PotionEffect(type, duration / 50, intensity));
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + duration;
        result = 31 * result + intensity;
        result = 31 * result + radius;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LootSocketPotionEffect that = (LootSocketPotionEffect) o;

        return duration == that.duration && intensity == that.intensity && radius == that.radius &&
               target == that.target && !(type != null ? !type.equals(that.type) : that.type != null);
    }

}
