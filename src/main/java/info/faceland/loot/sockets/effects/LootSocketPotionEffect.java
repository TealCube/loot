package info.faceland.loot.sockets.effects;

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
        entity.addPotionEffect(new PotionEffect(type, duration, intensity));
    }

}
