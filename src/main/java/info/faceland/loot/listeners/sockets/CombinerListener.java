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
package info.faceland.loot.listeners.sockets;

import com.kill3rtaco.tacoserialization.SingleItemSerialization;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Predicates;
import com.tealcube.minecraft.bukkit.shade.google.common.collect.Iterables;
import com.tealcube.minecraft.bukkit.shade.google.common.collect.Lists;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.math.Vec3;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class CombinerListener implements Listener {

    private LootPlugin plugin;
    private final Map<UUID, List<String>> gems;

    public CombinerListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.gems = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!ChatColor.stripColor(event.getInventory().getName()).equals("Socket Gem Combiner")) {
            return;
        }
        if (event.isShiftClick()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getHotbarButton() != -1) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getCurrentItem().getAmount() > 1) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
        HiltItemStack his = new HiltItemStack(event.getCurrentItem());
        if (!his.getName().startsWith(ChatColor.GOLD + "Socket Gem - ")) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
            return;
        }
        his = new HiltItemStack(event.getCursor());
        if (!his.getName().startsWith(ChatColor.GOLD + "Socket Gem - ")) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof Chest)) {
            return;
        }
        Vec3 loc = new Vec3(((Chest) holder).getWorld().getName(), ((Chest) holder).getX(), ((Chest) holder).getY(),
                ((Chest) holder).getZ());
        if (!plugin.getChestManager().getChestLocations().contains(loc)) {
            return;
        }
        event.setCancelled(true);
        Inventory toShow = Bukkit.createInventory(null, 9, "Socket Gem Combiner");
        toShow.setMaxStackSize(1);
        List<String> toAdd = new ArrayList<>();
        toAdd.addAll(gems.containsKey(event.getPlayer().getUniqueId()) ? gems.get(event.getPlayer().getUniqueId()) : new ArrayList<String>());
        gems.remove(event.getPlayer().getUniqueId());
        for (String s : toAdd) {
            toShow.addItem(SingleItemSerialization.getItem(s));
        }
        event.getPlayer().openInventory(toShow);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!ChatColor.stripColor(event.getInventory().getName()).equals("Socket Gem Combiner")) {
            return;
        }
        if (event.getInventory().getSize() > 9) {
            return;
        }
        List<ItemStack> newResults = new ArrayList<>();
        List<ItemStack> contents = Lists.newArrayList(Iterables.filter(Arrays.asList(event.getInventory().getContents()),
                Predicates.notNull()));
        for (ItemStack content : contents) {
            HiltItemStack his = new HiltItemStack(content);
            if (!his.getName().startsWith(ChatColor.GOLD + "Socket Gem - ")) {
                MessageUtils.sendMessage(event.getPlayer(), "<green>All items must be Socket Gems in order to transmute.");
                return;
            }
        }
        while (contents.size() >= 4) {
            contents = contents.subList(4, contents.size());
            newResults.add(plugin.getSocketGemManager().getRandomSocketGemByBonus().toItemStack(1));
        }
        newResults.addAll(contents);
        List<String> toAdd = new ArrayList<>();
        for (ItemStack is : newResults) {
            toAdd.add(SingleItemSerialization.serializeItemAsString(is));
        }
        if (toAdd.size() > 0) {
            HumanEntity c = event.getPlayer();
            c.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, c.getLocation().clone().add(0, 1, 0),30, 5, 5, 5);
            c.getWorld().playSound(c.getLocation().clone(), Sound.ENTITY_ENDERMEN_SCREAM, 1.0f, 1.0f);
            MessageUtils.sendMessage(event.getPlayer(), "<green>Open the chest again to get your new Socket Gems!");
        }
        gems.put(event.getPlayer().getUniqueId(), toAdd);
    }
}
