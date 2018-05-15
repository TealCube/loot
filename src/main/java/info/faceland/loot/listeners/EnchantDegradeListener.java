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
package info.faceland.loot.listeners;

import static com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils.sendMessage;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.math.LootRandom;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public final class EnchantDegradeListener implements Listener {

    private final LootPlugin plugin;
    private LootRandom random;

    public EnchantDegradeListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity dyingEntity = event.getEntity();
        Player p = dyingEntity.getKiller();
        if (p == null) {
            return;
        }
        if (random.nextDouble() > plugin.getSettings().getDouble("config.enchantment-degrade", 1.0)) {
            return;
        }
        switch (random.nextInt(6)) {
            case 0:
                if (p.getEquipment().getItemInMainHand() == null) return;
                p.getEquipment().setItemInMainHand(degrade(p.getEquipment().getItemInMainHand(), p));
                return;
            case 1:
                if (p.getEquipment().getItemInOffHand() == null) return;
                p.getEquipment().setItemInOffHand(degrade(p.getEquipment().getItemInOffHand(), p));
                break;
            case 2:
                if (p.getEquipment().getHelmet() == null) return;
                p.getEquipment().setHelmet(degrade(p.getEquipment().getHelmet(), p));
                break;
            case 3:
                if (p.getEquipment().getChestplate() == null) return;
                p.getEquipment().setChestplate(degrade(p.getEquipment().getChestplate(), p));
                break;
            case 4:
                if (p.getEquipment().getLeggings() == null) return;
                p.getEquipment().setLeggings(degrade(p.getEquipment().getLeggings(), p));
                break;
            case 5:
                if (p.getEquipment().getBoots() == null) return;
                p.getEquipment().setBoots(degrade(p.getEquipment().getBoots(), p));
                break;
        }
    }

    private ItemStack degrade(ItemStack itemStack, Player player) {
        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
            return itemStack;
        }
        HiltItemStack item = new HiltItemStack(itemStack);
        List<String> lore = new ArrayList<>();
        for (int i = 0; i < item.getLore().size(); i++) {
            String string = item.getLore().get(i);
            if (!string.startsWith(ChatColor.BLUE + "[") || !string.contains("" + ChatColor.BLACK)) {
                lore.add(string);
                continue;
            }
            int index = string.indexOf("" + ChatColor.BLACK);
            if (index <= 4) {
                lore.remove(lore.size() - 1);
                lore.add(ChatColor.BLUE + "(Enchantable)");
                sendMessage(player, plugin.getSettings().getString("language.enchant.degrade", ""));
                continue;
            } else if (index <= 7) {
                sendMessage(player, plugin.getSettings().getString("language.enchant.bar-low", ""));
            }
            string = string.replace("" + ChatColor.BLACK, "");
            string = new StringBuilder(string).insert(index - 1, ChatColor.BLACK + "").toString();
            lore.add(string);
        }
        item.setLore(lore);
        return item;
    }
}