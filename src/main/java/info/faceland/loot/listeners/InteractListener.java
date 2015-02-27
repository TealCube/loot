/*
 * This file is part of Loot, licensed under the ISC License.
 *
 * Copyright (c) 2014 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package info.faceland.loot.listeners;

import com.tealcube.minecraft.bukkit.facecore.shade.hilt.HiltItemStack;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.kern.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.kern.shade.google.common.base.CharMatcher;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.items.prefabs.UpgradeScroll;
import info.faceland.loot.math.LootRandom;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class InteractListener implements Listener {

    private final LootPlugin plugin;
    private LootRandom random;

    public InteractListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSpawnEvent(ItemSpawnEvent event) {
        HiltItemStack itemStack = new HiltItemStack(event.getEntity().getItemStack());
        if (!itemStack.getName().equals(itemStack.getDefaultName()) && !itemStack.getName().equals(ChatColor.GOLD +
                "REWARD!")) {
            event.getEntity().setCustomName(itemStack.getName());
            event.getEntity().setCustomNameVisible(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnchantingInventory) {
            event.setCancelled(true);
            MessageUtils.sendMessage((Player) event.getPlayer(),
                    plugin.getSettings().getString("language.enchant.no-open", ""));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCursor() == null
            || event.getCurrentItem().getType() == Material.AIR || event.getCursor().getType() == Material.AIR ||
            !(event.getWhoClicked() instanceof Player) || event.getClick() != ClickType.RIGHT) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        HiltItemStack currentItem = new HiltItemStack(event.getCurrentItem());
        HiltItemStack cursor = new HiltItemStack(event.getCursor());

        if (cursor.getName() == null) {
            return;
        }

        if (cursor.getName().startsWith(ChatColor.GOLD + "Socket Gem - ")) {
            String gemName = ChatColor.stripColor(cursor.getName().replace(ChatColor.GOLD + "Socket Gem - ", ""));
            SocketGem gem = plugin.getSocketGemManager().getSocketGem(gemName);

            if (gem == null) {
                return;
            }

            if (!plugin.getItemGroupManager().getMatchingItemGroups(currentItem.getType()).containsAll(
                    gem.getItemGroups())) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.socket.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }

            List<String> lore = currentItem.getLore();
            List<String> strippedLore = stripColor(lore);
            if (!strippedLore.contains("(Socket)")) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.socket.needs-sockets", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }
            int index = strippedLore.indexOf("(Socket)");

            lore.remove(index);
            lore.addAll(index, TextUtils.color(gem.getLore()));

            currentItem.setLore(lore);

            // strip color, check against that
            // k
            String name = currentItem.getName();
            int level = getLevel(ChatColor.stripColor(name));
            name = name.replace("+" + level + " ", "");
            ChatColor start = getFirstColor(name);
            String format = "%s%s%s";
            name = String.format(format, start + (level > 0 ? "+" + level + " " : "") +
                                         (!gem.getPrefix().isEmpty() ? gem.getPrefix() + " " : ""),
                                 name + (!gem.getSuffix().isEmpty() ? " " : ""),
                                 start + gem.getSuffix() + ChatColor.getLastColors(name));
            currentItem.setName(TextUtils.color(name));

            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.socket.success", ""));
            player.playSound(player.getEyeLocation(), Sound.ORB_PICKUP, 1L, 2.0F);
        } else if (cursor.getName().startsWith(ChatColor.BLUE + "Enchantment Tome - ")) {
            String stoneName = ChatColor.stripColor(
                    cursor.getName().replace(ChatColor.BLUE + "Enchantment Tome - ", ""));
            EnchantmentTome stone = plugin.getEnchantmentStoneManager().getEnchantmentStone(stoneName);

            if (!isBlockWithinRadius(Material.ENCHANTMENT_TABLE, event.getWhoClicked().getLocation(), 5)) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.no-enchantment-table", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }

            if (stone == null) {
                return;
            }

            if (!plugin.getItemGroupManager().getMatchingItemGroups(currentItem.getType()).containsAll(
                    stone.getItemGroups())) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }

            List<String> lore = currentItem.getLore();
            List<String> strippedLore = stripColor(lore);
            if (!strippedLore.contains("(Enchantable)")) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.needs-enchantable", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }
            int index = strippedLore.indexOf("(Enchantable)");

            List<String> added = new ArrayList<>();
            for (int i = 0; i < random.nextIntRange(stone.getMinStats(), stone.getMaxStats()); i++) {
                added.add(stone.getLore().get(random.nextInt(stone.getLore().size())));
            }

            lore.remove(index);
            lore.addAll(index, TextUtils.color(added));

            currentItem.setLore(lore);

            currentItem.addUnsafeEnchantments(stone.getEnchantments());

            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.success", ""));
            player.playSound(player.getEyeLocation(), Sound.PORTAL_TRAVEL, 1L, 2.0F);
        } else if (cursor.getName().equals(ChatColor.DARK_AQUA + "Socket Extender")) {
            List<String> lore = currentItem.getLore();
            List<String> stripColor = stripColor(lore);
            if (!stripColor.contains("(+)")) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.extend.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }
            int index = stripColor.indexOf("(+)");
            lore.set(index, ChatColor.GOLD + "(Socket)");
            currentItem.setLore(lore);

            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.extend.success", ""));
            player.playSound(player.getEyeLocation(), Sound.PORTAL_TRAVEL, 1L, 2.0F);
        } else if (cursor.getName().equals(ChatColor.DARK_PURPLE + "Identity Tome")) {
            if (!currentItem.getName().equals(ChatColor.LIGHT_PURPLE + "Unidentified Item")) {
                return;
            }
            Material m = currentItem.getType();
            currentItem = plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.IDENTIFYING)
                                .withMaterial(m).build();

            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.identify.success", ""));
            player.playSound(player.getEyeLocation(), Sound.PORTAL_TRAVEL, 1L, 2.0F);
        } else if (cursor.getName().endsWith("Upgrade Scroll")) {
            if (currentItem.getName().equals(ChatColor.DARK_AQUA + "Socket Extender") ||
                currentItem.getName().startsWith(ChatColor.BLUE + "Enchantment Tome - ") ||
                currentItem.getName().startsWith(ChatColor.GOLD + "Socket Gem -") ||
                currentItem.getName().equals(ChatColor.AQUA + "Charm of Protection")) {
                return;
            }
            String name = ChatColor.stripColor(cursor.getName()).replace("Upgrade Scroll", "").trim();
            UpgradeScroll.ScrollType type = UpgradeScroll.ScrollType.getByName(name);
            if (type == null) {
                return;
            }
            name = currentItem.getName();
            if (plugin.getSettings().getStringList("config.cannot-be-upgraded", new ArrayList<String>()).contains(ChatColor.stripColor(name))) {
                return;
            }
            int level = ChatColor.stripColor(name).startsWith("+") ? getLevel(ChatColor.stripColor(name)) : 0, lev = level;
            if (level < type.getMinimumLevel() || level > type.getMaximumLevel()) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.upgrade.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
                return;
            }
            boolean succeed = false;
            List<String> strip = stripColor(currentItem.getLore());
            for (String s : strip) {
                if (s.startsWith("+")) {
                    succeed = true;
                    break;
                }
            }
            if (!succeed) {
                return;
            }
            if (random.nextDouble() < type.getChanceToDestroy()) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.upgrade.destroyed", ""));
                player.playSound(player.getEyeLocation(), Sound.ITEM_BREAK, 1F, 1F);
                currentItem = null;
            }
            if (currentItem != null) {
                if (level == 0) {
                    level++;
                    name = getFirstColor(name) + ("+" + level) + " " + name;
                    currentItem.setName(name);
                } else {
                    level++;
                    name = name.replace("+" + lev, "+" + String.valueOf(level));
                    currentItem.setName(name);
                    if (level >= 7 && currentItem.getEnchantments().isEmpty()) {
                        currentItem.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 32);
                    }
                }
                List<String> lore = currentItem.getLore();
                for (int i = 0; i < lore.size(); i++) {
                    String s = lore.get(i);
                    String ss = ChatColor.stripColor(s);
                    if (!ss.startsWith("+")) {
                        continue;
                    }
                    String loreLev = CharMatcher.DIGIT.or(CharMatcher.is('-')).retainFrom(ss);
                    int loreLevel = NumberUtils.toInt(loreLev);
                    lore.set(i, s.replace("+" + loreLevel, "+" + (loreLevel + 1)));
                    break;
                }
                currentItem.setLore(lore);
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.upgrade.success", ""));
                player.playSound(player.getEyeLocation(), Sound.LEVEL_UP, 1F, 2F);
            }
        } else {
            return;
        }

        event.setCurrentItem(currentItem);
        cursor.setAmount(cursor.getAmount() - 1);
        event.setCursor(cursor.getAmount() == 0 ? null : cursor);
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
        player.updateInventory();
    }

    private boolean isBlockWithinRadius(Material material, Location location, int radius) {
        int minX = location.getBlockX() - radius;
        int maxX = location.getBlockX() + radius;
        int minY = location.getBlockY() - radius;
        int maxY = location.getBlockY() + radius;
        int minZ = location.getBlockZ() - radius;
        int maxZ = location.getBlockZ() + radius;
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private ChatColor getFirstColor(String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            if (!s.substring(i, i + 1).equals(ChatColor.COLOR_CHAR + "")) {
                continue;
            }
            ChatColor c = ChatColor.getByChar(s.substring(i + 1, i + 2));
            if (c != null) {
                return c;
            }
        }
        return ChatColor.RESET;
    }

    private int getLevel(String name) {
        String lev = CharMatcher.DIGIT.or(CharMatcher.is('-')).negate().collapseFrom(name, ' ').trim();
        return NumberUtils.toInt(lev.split(" ")[0], 0);
    }

    private List<String> stripColor(List<String> strings) {
        List<String> ret = new ArrayList<>();
        for (String s : strings) {
            ret.add(ChatColor.stripColor(s));
        }
        return ret;
    }

}
