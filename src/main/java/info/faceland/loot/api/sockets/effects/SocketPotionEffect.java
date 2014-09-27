package info.faceland.loot.api.sockets.effects;

import org.bukkit.potion.PotionEffectType;

public interface SocketPotionEffect extends SocketEffect {

    PotionEffectType getPotionEffectType();

}
