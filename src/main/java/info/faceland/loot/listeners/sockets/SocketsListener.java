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

package info.faceland.loot.listeners.sockets;

import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.effects.SocketEffect;
import info.faceland.utils.StringListUtils;
import info.faceland.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SocketsListener implements Listener {

    private LootPlugin plugin;

    public SocketsListener(LootPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        List<SocketGem> attackerGems = new ArrayList<>();
        List<SocketGem> defenderGems = new ArrayList<>();
        Entity attacker = event.getDamager();
        Entity defender = event.getEntity();
        if (attacker instanceof Player) {
            Player attackerP = (Player) attacker;
            attackerGems.addAll(getGems(attackerP.getEquipment().getItemInHand()));
        }
        if (defender instanceof Player) {
            Player defenderP = (Player) defender;
            for (ItemStack equipment : defenderP.getEquipment().getArmorContents()) {
                defenderGems.addAll(getGems(equipment));
            }
        }

        for (SocketGem gem : attackerGems) {
            for (SocketEffect effect : gem.getSocketEffects()) {
                switch (effect.getTarget()) {
                    case SELF:
                        if (attacker instanceof LivingEntity) {
                            effect.apply((LivingEntity) attacker);
                        }
                        break;
                    case OTHER:
                        if (defender instanceof LivingEntity) {
                            effect.apply((LivingEntity) defender);
                        }
                        break;
                    case AREA:
                        for (Entity e : defender
                                .getNearbyEntities(effect.getRadius(), effect.getRadius(), effect.getRadius())) {
                            if (e instanceof LivingEntity) {
                                effect.apply((LivingEntity) e);
                            }
                        }
                        if (defender instanceof LivingEntity) {
                            effect.apply((LivingEntity) defender);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        for (SocketGem gem : defenderGems) {
            for (SocketEffect effect : gem.getSocketEffects()) {
                switch (effect.getTarget()) {
                    case SELF:
                        if (defender instanceof LivingEntity) {
                            effect.apply((LivingEntity) defender);
                        }
                        break;
                    case OTHER:
                        if (attacker instanceof LivingEntity) {
                            effect.apply((LivingEntity) attacker);
                        }
                        break;
                    case AREA:
                        for (Entity e : attacker
                                .getNearbyEntities(effect.getRadius(), effect.getRadius(), effect.getRadius())) {
                            if (e instanceof LivingEntity) {
                                effect.apply((LivingEntity) e);
                            }
                        }
                        if (attacker instanceof LivingEntity) {
                            effect.apply((LivingEntity) attacker);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private Set<SocketGem> getGems(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return new HashSet<>();
        }
        Set<SocketGem> gems = new HashSet<>();
        HiltItemStack item = new HiltItemStack(itemStack);
        List<String> lore = item.getLore();
        List<String> strippedLore = StringListUtils.stripColor(lore);
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

}
