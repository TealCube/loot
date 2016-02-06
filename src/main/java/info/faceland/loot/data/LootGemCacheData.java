package info.faceland.loot.data;

import info.faceland.loot.api.data.GemCacheData;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LootGemCacheData implements GemCacheData {

    private final UUID owner;
    private Set<SocketEffect> armorGems;
    private Set<SocketEffect> weaponGems;

    public LootGemCacheData(UUID owner) {
        this.owner = owner;
        this.armorGems = new HashSet<>();
        this.weaponGems = new HashSet<>();
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public Set<SocketEffect> getArmorCache() {
        return armorGems;
    }

    @Override
    public Set<SocketEffect> getWeaponCache() {
        return weaponGems;
    }

    @Override
    public void setArmorCache(Set<SocketEffect> set) {
        this.armorGems = set;
    }

    @Override
    public void setWeaponCache(Set<SocketEffect> set) {
        this.weaponGems = set;
    }

}
