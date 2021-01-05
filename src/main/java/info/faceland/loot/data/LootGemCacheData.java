/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.data;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.data.GemCacheData;
import info.faceland.loot.api.sockets.effects.SocketEffect;
import info.faceland.loot.sockets.SocketGem;
import info.faceland.loot.sockets.SocketGem.GemType;
import info.faceland.loot.utils.GemUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LootGemCacheData implements GemCacheData {

    private final LootPlugin plugin;
    private final UUID owner;
    private Map<GemType, Set<SocketEffect>> armorGems;
    private Map<SocketGem.GemType, Set<SocketEffect>> weaponGems;

    public LootGemCacheData(LootPlugin plugin, UUID owner) {
        this.plugin = plugin;
        this.owner = owner;
        this.armorGems = new HashMap<>();
        this.weaponGems = new HashMap<>();
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public Set<SocketEffect> getArmorCache(SocketGem.GemType gemType) {
        if (armorGems.containsKey(gemType)) {
            return armorGems.get(gemType);
        }
        return new HashSet<>();
    }

    @Override
    public Set<SocketEffect> getWeaponCache(SocketGem.GemType gemType) {
        if (weaponGems.containsKey(gemType)) {
            return weaponGems.get(gemType);
        }
        return new HashSet<>();
    }

    @Override
    public void setArmorCache(SocketGem.GemType gemType, Set<SocketEffect> set) {
        this.armorGems.put(gemType, set);
    }

    @Override
    public void setWeaponCache(SocketGem.GemType gemType, Set<SocketEffect> set) {
        this.weaponGems.put(gemType, set);
    }

    @Override
    public void updateArmorCache() {
        armorGems.clear();
        Player player = Bukkit.getPlayer(getOwner());
        if (player == null) {
            return;
        }
        Set<SocketGem> equipmentGems = new HashSet<>();
        for (ItemStack itemStack : player.getEquipment().getArmorContents()) {
            equipmentGems.addAll(GemUtil.getGems(plugin.getSocketGemManager(), itemStack));
        }
        Map<SocketGem.GemType, Set<SocketEffect>> equipmentEffects = new HashMap<>();
        extractEffects(equipmentGems, equipmentEffects);
        armorGems.putAll(equipmentEffects);
    }

    @Override
    public void updateWeaponCache() {
        weaponGems.clear();
        Player player = Bukkit.getPlayer(getOwner());
        if (player == null) {
            return;
        }
        Set<SocketGem> primaryHandGems = GemUtil.getGems(
                plugin.getSocketGemManager(), player.getEquipment().getItemInMainHand());
        //Set<SocketGem> secondaryHandGems = GemUtil.getGems(
        //        plugin.getSocketGemManager(), player.getEquipment().getItemInOffHand());
        Map<SocketGem.GemType, Set<SocketEffect>> weaponEffects = new HashMap<>();
        extractEffects(primaryHandGems, weaponEffects);
        //extractEffects(secondaryHandGems, weaponEffects);
        weaponGems.putAll(weaponEffects);
    }

    private void extractEffects(Set<SocketGem> primaryHandGems, Map<SocketGem.GemType, Set<SocketEffect>> weaponEffects) {
        for (SocketGem gem : primaryHandGems) {
            Set<SocketEffect> existingEffects = new HashSet<>();
            if (weaponEffects.containsKey(gem.getGemType())) {
                existingEffects.addAll(weaponEffects.get(gem.getGemType()));
            }
            existingEffects.addAll(gem.getSocketEffects());
            weaponEffects.put(gem.getGemType(), existingEffects);
        }
    }

}
