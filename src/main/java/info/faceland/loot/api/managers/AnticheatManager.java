package info.faceland.loot.api.managers;

import info.faceland.loot.api.anticheat.AnticheatTag;
import org.bukkit.entity.LivingEntity;

public interface AnticheatManager {

    boolean isTagged(LivingEntity entity);

    boolean push(LivingEntity entity);

    boolean pull(LivingEntity entity);

    AnticheatTag tag(LivingEntity entity);

}
