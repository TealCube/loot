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
package info.faceland.loot.data;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.data.GemCacheData;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LootGemCacheData implements GemCacheData {

    private final LootPlugin plugin;
    private final UUID owner;
    private Set<SocketEffect> armorGems;
    private Set<SocketEffect> weaponGems;

    public LootGemCacheData(LootPlugin plugin, UUID owner) {
        this.plugin = plugin;
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

    @Override
    public void updateArmorCache() {
        armorGems.clear();
        Player player = Bukkit.getPlayer(getOwner());
        if (player == null) {
            return;
        }
        Set<SocketGem> equipmentGems = new HashSet<>();
        for (ItemStack itemStack : player.getEquipment().getArmorContents()) {
            equipmentGems.addAll(getGems(itemStack));
        }
        Set<SocketEffect> equipmentEffects = new HashSet<>();
        for (SocketGem gem : equipmentGems) {
            equipmentEffects.addAll(gem.getSocketEffects());
        }
        armorGems.addAll(equipmentEffects);
    }

    @Override
    public void updateWeaponCache() {
        weaponGems.clear();
        Player player = Bukkit.getPlayer(getOwner());
        if (player == null) {
            return;
        }
        Set<SocketGem> weaponGemsS = getGems(player.getItemInHand());
        Set<SocketEffect> weaponEffects = new HashSet<>();
        for (SocketGem gem : weaponGemsS) {
            weaponEffects.addAll(gem.getSocketEffects());
        }
        weaponGems.addAll(weaponEffects);
    }

    private Set<SocketGem> getGems(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return new HashSet<>();
        }
        Set<SocketGem> gems = new HashSet<>();
        HiltItemStack item = new HiltItemStack(itemStack);
        List<String> lore = item.getLore();
        List<String> strippedLore = stripColor(lore);
        for (String key : strippedLore) {
            SocketGem gem = plugin.getSocketGemManager().getSocketGem(key);
            if (gem == null) {
                for (SocketGem g : plugin.getSocketGemManager().getSocketGems()) {
                    if (key.equals(ChatColor.stripColor(TextUtils.color(
                            g.getTriggerText() != null ? g.getTriggerText() : "")))) {
                        gem = g;
                        break;
                    }
                }
                if (gem == null) {
                    continue;
                }
            }
            gems.add(gem);
        }
        return gems;
    }

    private List<String> stripColor(List<String> strings) {
        List<String> ret = new ArrayList<>();
        for (String s : strings) {
            ret.add(ChatColor.stripColor(s));
        }
        return ret;
    }

}
