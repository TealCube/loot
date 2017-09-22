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

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.fanciful.FancyMessage;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.math.Vec3;
import info.faceland.loot.api.sockets.SocketGem;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
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
    private HiltItemStack clickToCombineButton;

    public CombinerListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.clickToCombineButton = SetupClickToCombineButton();
    }

    @EventHandler
    public void onGemAddToCombiner(InventoryClickEvent event) {
        if (!ChatColor.stripColor(event.getInventory().getName()).equals("Socket Gem Combiner")) {
            return;
        }
        event.setResult(Event.Result.DENY);
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (!(event.getClickedInventory().getHolder() instanceof HumanEntity)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory invy = event.getInventory();
        Inventory playerInvy = event.getWhoClicked().getInventory();

        HiltItemStack clickedItem = new HiltItemStack(event.getCurrentItem());
        HiltItemStack resultSlotItem = new HiltItemStack(Material.BARRIER);
        if (invy.getItem(31) != null) {
            resultSlotItem = new HiltItemStack(invy.getItem(31));
        }

        if (!isSocketGem(clickedItem)) {
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.socket.must-be-gem", ""));
            return;
        }

        if (isSocketGem(resultSlotItem)) {
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.socket.pls-claim-first", ""));
            return;
        }

        int firstEmptyCombinerSlot = getEmptySocketCombinerSlot(invy);

        if (firstEmptyCombinerSlot != -1) {
            HiltItemStack newGem = clickedItem.clone();
            HiltItemStack oldGem = clickedItem.clone();

            newGem.setAmount(1);
            oldGem.setAmount(oldGem.getAmount() - 1);

            invy.setItem(firstEmptyCombinerSlot, newGem);
            playerInvy.setItem(event.getSlot(), oldGem);

            player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1L, 1.8F);

            if (getEmptySocketCombinerSlot(invy) == -1) {
                player.playSound(player.getEyeLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT, 1L, 2.0F);
                player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1L, 1.0F);
                invy.setItem(31, clickToCombineButton);
            }
        }
    }

    @EventHandler
    public void onGemRemoveFromCombiner(InventoryClickEvent event) {
        if (!ChatColor.stripColor(event.getInventory().getName()).equals("Socket Gem Combiner")) {
            return;
        }
        event.setResult(Event.Result.DENY);
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (event.getClickedInventory().getHolder() instanceof HumanEntity) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory invy = event.getInventory();
        Inventory playerInvy = player.getInventory();

        HiltItemStack his = new HiltItemStack(event.getCurrentItem());

        if (!isSocketGem(his) && !isCombineButton(his)) {
            return;
        }

        if (event.getSlot() == 31) {
            if (isCombineButton(his) && getEmptySocketCombinerSlot(invy) == -1) {
                HiltItemStack gem = RandomTransmutedGem((Player)event.getWhoClicked());
                playTransmuteEffects((Player)event.getWhoClicked());
                invy.setItem(10, null);
                invy.setItem(12, null);
                invy.setItem(14, null);
                invy.setItem(16, null);
                invy.setItem(event.getSlot(), gem);
                return;
            }
            if (isSocketGem(his)) {
                player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1L, 1.8F);
                invy.setItem(event.getSlot(), null);
                if (playerInvy.firstEmpty() == -1) {
                    event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), his);
                    return;
                } else {
                    playerInvy.addItem(his);
                    return;
                }
            }
            invy.setItem(event.getSlot(), null);
        }

        if (event.getSlot() == 10 || event.getSlot() == 12 || event.getSlot() == 14 || event.getSlot() == 16) {
            if (playerInvy.firstEmpty() != -1) {
                playerInvy.addItem(his);
            } else {
                event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), his);
            }
            invy.setItem(event.getSlot(), null);
            if (invy.getItem(31) != null) {
                player.playSound(player.getEyeLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1L, 1.2F);
                invy.setItem(31, null);
            }
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
        Inventory toShow = Bukkit.createInventory(null, 45, TextUtils.color("&5&lSocket Gem Combiner"));
        HiltItemStack buffer = new HiltItemStack(Material.IRON_FENCE);
        buffer.setName(TextUtils.color("&aClick a &6Socket Gem &ato begin!"));
        for (int slot = 0; slot < toShow.getSize(); slot++) {
            if (slot == 10 || slot == 12 || slot == 14 || slot == 16 || slot == 31) {
                continue;
            }
            toShow.setItem(slot, buffer);
        }
        toShow.setMaxStackSize(1);
        event.getPlayer().openInventory(toShow);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!ChatColor.stripColor(event.getInventory().getName()).equals("Socket Gem Combiner")) {
            return;
        }
        List<ItemStack> contents = new ArrayList<>();

        contents.add(event.getInventory().getItem(10));
        contents.add(event.getInventory().getItem(12));
        contents.add(event.getInventory().getItem(14));
        contents.add(event.getInventory().getItem(16));
        contents.add(event.getInventory().getItem(31));

        HumanEntity c = event.getPlayer();

        for (ItemStack content : contents) {
            if (content == null || content.getType() != Material.EMERALD) {
                continue;
            }
            if (c.getInventory().firstEmpty() != -1) {
                c.getInventory().addItem(content);
            } else {
                c.getWorld().dropItem(c.getLocation(), content);
            }
        }
    }

    private int getEmptySocketCombinerSlot(Inventory invy) {
        if (invy.getItem(10) == null || invy.getItem(10).getType() == Material.AIR ) {
            return 10;
        }
        if (invy.getItem(12) == null || invy.getItem(12).getType() == Material.AIR ) {
            return 12;
        }
        if (invy.getItem(14) == null || invy.getItem(14).getType() == Material.AIR ) {
            return 14;
        }
        if (invy.getItem(16) == null || invy.getItem(16).getType() == Material.AIR ) {
            return 16;
        }
        return -1;
    }

    private HiltItemStack SetupClickToCombineButton() {
        HiltItemStack clickToCombineButton = new HiltItemStack(Material.NETHER_STAR);
        clickToCombineButton.setAmount(1);
        clickToCombineButton.setName(TextUtils.color("&e&lClick to combine!"));
        return clickToCombineButton;
    }

    private HiltItemStack RandomTransmutedGem(Player player) {
        SocketGem gem = plugin.getSocketGemManager().getRandomSocketGemByBonus();
        HiltItemStack gemItem = gem.toItemStack(1);
        if (gem.isBroadcast()) {
            broadcast(player, gemItem);
        }
        return gemItem;
    }

    private void broadcast(Player player, HiltItemStack his) {
        FancyMessage message = new FancyMessage("");
        String mess = plugin.getSettings().getString("language.broadcast.transmute-gem", "");
        String[] split = mess.split(" ");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            String str = TextUtils.color(s);
            if (str.contains("%player%")) {
                message.then(str.replace("%player%", player.getDisplayName()));
            } else if (str.contains("%item%")) {
                message.then(str.replace("%item%", his.getName())).itemTooltip(his);
            } else {
                message.then(str);
            }
            if (i != split.length - 1) {
                message.then(" ");
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            message.send(p);
        }
    }

    private void playTransmuteEffects(Player player) {
        MessageUtils.sendMessage(player, plugin.getSettings().getString("language.socket.transmute-success", ""));
        player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation().clone().add(0, 1, 0),30, 5, 5, 5);
        player.getWorld().playSound(player.getLocation().clone(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
        player.getWorld().playSound(player.getLocation().clone(), Sound.BLOCK_END_GATEWAY_SPAWN, 1.0f, 2f);
    }

    private boolean isSocketGem(HiltItemStack item) {
        if (item == null || item.getType() != Material.EMERALD) {
            return false;
        }
        if (!item.getName().startsWith(ChatColor.GOLD + "Socket Gem - ")) {
            return false;
        }
        return true;
    }

    private boolean isCombineButton(HiltItemStack item) {
        return item.isSimilar(clickToCombineButton);
    }
}
