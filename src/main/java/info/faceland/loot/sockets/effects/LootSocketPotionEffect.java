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

package info.faceland.loot.sockets.effects;

import info.faceland.loot.api.sockets.effects.SocketEffectTarget;
import info.faceland.loot.api.sockets.effects.SocketPotionEffect;
import info.faceland.loot.utils.converters.StringConverter;
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
        int duration = StringConverter.toInt(split[2]);
        int intensity = StringConverter.toInt(split[3]);
        int radius = StringConverter.toInt(split[4]);
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
