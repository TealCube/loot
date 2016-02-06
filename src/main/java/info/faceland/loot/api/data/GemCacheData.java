package info.faceland.loot.api.data;

import info.faceland.loot.api.sockets.effects.SocketEffect;

import java.util.Set;
import java.util.UUID;

public interface GemCacheData {

    UUID getOwner();

    Set<SocketEffect> getArmorCache();

    Set<SocketEffect> getWeaponCache();

    void setArmorCache(Set<SocketEffect> set);

    void setWeaponCache(Set<SocketEffect> set);

}
