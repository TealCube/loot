package info.faceland.loot.api.sockets.effects;

import org.bukkit.entity.LivingEntity;

public interface SocketEffect {

    int getDuration();

    int getIntensity();

    void apply(LivingEntity entity);

}
