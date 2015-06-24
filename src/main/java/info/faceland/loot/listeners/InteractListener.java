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
import com.tealcube.minecraft.bukkit.kern.shade.google.common.collect.Sets;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.items.prefabs.RevealPowder;
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
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemFlag;

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
        boolean updateItem = false;

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
            updateItem = true;
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
            updateItem = true;
        } else if (cursor.getName().equals(ChatColor.YELLOW + "Stat Reveal Powder")) {
            List<String> lore = currentItem.getLore();
            List<String> stripColor = stripColor(lore);

            Material matType = currentItem.getType();
            String[] stat = new String[5];

            if (!stripColor.contains("( ??? )")) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.reveal.failure", ""));
                return;
            }
            int index = stripColor.indexOf("( ??? )");

            // hhaha..hhHHAHEHHAHHEHAHAHAHAHAHAH!!!
            switch (matType) {
                case LEATHER_HELMET:
                    stat[0] = "<yellow>+4 Evasion";
                    stat[1] = "<yellow>+8% Attack Speed";
                    stat[2] = "<yellow>+3 Ranged Damage";
                    stat[3] = "<yellow>+3 Melee Damage";
                    stat[4] = "<yellow>+8 Movement Speed";
                    break;
                case LEATHER_CHESTPLATE:
                    stat[0] = "<yellow>+6 Evasion";
                    stat[1] = "<yellow>+10% Attack Speed";
                    stat[2] = "<yellow>+3 Ranged Damage";
                    stat[3] = "<yellow>+3 Melee Damage";
                    stat[4] = "<yellow>+8 Movement Speed";
                    break;
                case LEATHER_LEGGINGS:
                    stat[0] = "<yellow>+5 Evasion";
                    stat[1] = "<yellow>+8% Attack Speed";
                    stat[2] = "<yellow>+3 Ranged Damage";
                    stat[3] = "<yellow>+3 Melee Damage";
                    stat[4] = "<yellow>+8 Movement Speed";
                    break;
                case LEATHER_BOOTS:
                    stat[0] = "<yellow>+4 Evasion";
                    stat[1] = "<yellow>+8% Attack Speed";
                    stat[2] = "<yellow>+3 Ranged Damage";
                    stat[3] = "<yellow>+3 Melee Damage";
                    stat[4] = "<yellow>+15 Movement Speed";
                    break;
                case CHAINMAIL_HELMET:
                    stat[0] = "<yellow>+4 Evasion";
                    stat[1] = "<yellow>+8% Attack Speed";
                    stat[2] = "<yellow>+3 Ranged Damage";
                    stat[3] = "<yellow>+3 Melee Damage";
                    stat[4] = "<yellow>+8 Movement Speed";
                    break;
                case CHAINMAIL_CHESTPLATE:
                    stat[0] = "<yellow>+6 Evasion";
                    stat[1] = "<yellow>+10% Attack Speed";
                    stat[2] = "<yellow>+3 Ranged Damage";
                    stat[3] = "<yellow>+3 Melee Damage";
                    stat[4] = "<yellow>+8 Movement Speed";
                    break;
                case CHAINMAIL_LEGGINGS:
                    stat[0] = "<yellow>+5 Evasion";
                    stat[1] = "<yellow>+8% Attack Speed";
                    stat[2] = "<yellow>+3 Ranged Damage";
                    stat[3] = "<yellow>+3 Melee Damage";
                    stat[4] = "<yellow>+8 Movement Speed";
                    break;
                case CHAINMAIL_BOOTS:
                    stat[0] = "<yellow>+4 Evasion";
                    stat[1] = "<yellow>+8% Attack Speed";
                    stat[2] = "<yellow>+3 Ranged Damage";
                    stat[3] = "<yellow>+3 Melee Damage";
                    stat[4] = "<yellow>+15 Movement Speed";
                    break;
                case IRON_HELMET:
                    stat[0] = "<yellow>+7 Armor";
                    stat[1] = "<yellow>+7 Health";
                    stat[2] = "<yellow>+8 Armor";
                    stat[3] = "<yellow>+8 Health";
                    stat[4] = "<yellow>+15% Block";
                    break;
                case IRON_CHESTPLATE:
                    stat[0] = "<yellow>+7 Armor";
                    stat[1] = "<yellow>+7 Health";
                    stat[2] = "<yellow>+8 Armor";
                    stat[3] = "<yellow>+8 Health";
                    stat[4] = "<yellow>+15% Block";
                    break;
                case IRON_LEGGINGS:
                    stat[0] = "<yellow>+7 Armor";
                    stat[1] = "<yellow>+7 Health";
                    stat[2] = "<yellow>+8 Armor";
                    stat[3] = "<yellow>+8 Health";
                    stat[4] = "<yellow>+15% Block";
                    break;
                case IRON_BOOTS:
                    stat[0] = "<yellow>+7 Armor";
                    stat[1] = "<yellow>+7 Health";
                    stat[2] = "<yellow>+8 Armor";
                    stat[3] = "<yellow>+8 Health";
                    stat[4] = "<yellow>+15% Block";
                    break;
                case GOLD_HELMET:
                    stat[0] = "<yellow>+5% Life Steal";
                    stat[1] = "<yellow>+6 Health";
                    stat[2] = "<yellow>+5% Damage Reflect";
                    stat[3] = "<yellow>+5 Movement Speed";
                    stat[4] = "<yellow>+4 Fire Damage";
                    break;
                case GOLD_CHESTPLATE:
                    stat[0] = "<yellow>+5% Life Steal";
                    stat[1] = "<yellow>+6 Health";
                    stat[2] = "<yellow>+5% Damage Reflect";
                    stat[3] = "<yellow>+5 Movement Speed";
                    stat[4] = "<yellow>+4 Fire Damage";
                    break;
                case GOLD_LEGGINGS:
                    stat[0] = "<yellow>+5% Life Steal";
                    stat[1] = "<yellow>+6 Health";
                    stat[2] = "<yellow>+5% Damage Reflect";
                    stat[3] = "<yellow>+5 Movement Speed";
                    stat[4] = "<yellow>+4 Fire Damage";
                    break;
                case GOLD_BOOTS:
                    stat[0] = "<yellow>+5% Life Steal";
                    stat[1] = "<yellow>+6 Health";
                    stat[2] = "<yellow>+5% Damage Reflect";
                    stat[3] = "<yellow>+5 Movement Speed";
                    stat[4] = "<yellow>+4 Fire Damage";
                    break;
                case DIAMOND_HELMET:
                    stat[0] = "<yellow>+7 Armor";
                    stat[1] = "<yellow>+7 Health";
                    stat[2] = "<yellow>+8 Armor";
                    stat[3] = "<yellow>+8 Health";
                    stat[4] = "<yellow>+8% Parry Chance";
                    break;
                case DIAMOND_CHESTPLATE:
                    stat[0] = "<yellow>+7 Armor";
                    stat[1] = "<yellow>+7 Health";
                    stat[2] = "<yellow>+8 Armor";
                    stat[3] = "<yellow>+8 Health";
                    stat[4] = "<yellow>+8% Parry Chance";
                    break;
                case DIAMOND_LEGGINGS:
                    stat[0] = "<yellow>+7 Armor";
                    stat[1] = "<yellow>+7 Health";
                    stat[2] = "<yellow>+8 Armor";
                    stat[3] = "<yellow>+8 Health";
                    stat[4] = "<yellow>+8% Parry Chance";
                    break;
                case DIAMOND_BOOTS:
                    stat[0] = "<yellow>+7 Armor";
                    stat[1] = "<yellow>+7 Health";
                    stat[2] = "<yellow>+8 Armor";
                    stat[3] = "<yellow>+8 Health";
                    stat[4] = "<yellow>+8% Parry Chance";
                    break;
                case WOOD_SWORD:
                    stat[0] = "<yellow>+6 Fire Damage";
                    stat[1] = "<yellow>+20% Attack Speed";
                    stat[2] = "<yellow>+5 Melee Damage";
                    stat[3] = "<yellow>+25% Accuracy";
                    stat[4] = "<yellow>+12% Life Steal";
                    break;
                case WOOD_AXE:
                    stat[0] = "<yellow>+6 Fire Damage";
                    stat[1] = "<yellow>+20% Overcharge";
                    stat[2] = "<yellow>+5 Melee Damage";
                    stat[3] = "<yellow>+25% Accuracy";
                    stat[4] = "<yellow>+15% Life Steal";
                    break;
                case STONE_SWORD:
                    stat[0] = "<yellow>+25% Block";
                    stat[1] = "<yellow>+20% Attack Speed";
                    stat[2] = "<yellow>+5 Melee Damage";
                    stat[3] = "<yellow>+25% Accuracy";
                    stat[4] = "<yellow>+15% Overcharge";
                    break;
                case STONE_AXE:
                    stat[0] = "<yellow>+6 Melee Damage";
                    stat[1] = "<yellow>+30% Overcharge";
                    stat[2] = "<yellow>+5 Melee Damage";
                    stat[3] = "<yellow>+25% Accuracy";
                    stat[4] = "<yellow>+25% Overcharge";
                    break;
                case IRON_SWORD:
                    stat[0] = "<yellow>+5 Melee Damage";
                    stat[1] = "<yellow>+20% Overcharge";
                    stat[2] = "<yellow>+20% Attack Speed";
                    stat[3] = "<yellow>+25% Accuracy";
                    stat[4] = "<yellow>+12% Life Steal";
                    break;
                case IRON_AXE:
                    stat[0] = "<yellow>+5 Melee Damage";
                    stat[1] = "<yellow>+25% Overcharge";
                    stat[2] = "<yellow>+15% Attack Speed";
                    stat[3] = "<yellow>+25% Accuracy";
                    stat[4] = "<yellow>+12% Life Steal";
                    break;
                case GOLD_SWORD:
                    stat[0] = "<yellow>+5 Melee Damage";
                    stat[1] = "<yellow>+5 Fire Damage";
                    stat[2] = "<yellow>+5 Lightning Damage";
                    stat[3] = "<yellow>+4 Ice Damage";
                    stat[4] = "<yellow>+15% Life Steal";
                    break;
                case GOLD_AXE:
                    stat[0] = "<yellow>+5 Melee Damage";
                    stat[1] = "<yellow>+5 Fire Damage";
                    stat[2] = "<yellow>+5 Lightning Damage";
                    stat[3] = "<yellow>+4 Ice Damage";
                    stat[4] = "<yellow>+15% Life Steal";
                    break;
                case DIAMOND_SWORD:
                    stat[0] = "<yellow>+5 Melee Damage";
                    stat[1] = "<yellow>+20% Overcharge";
                    stat[2] = "<yellow>+20% Attack Speed";
                    stat[3] = "<yellow>+25% Accuracy";
                    stat[4] = "<yellow>+12% Life Steal";
                    break;
                case DIAMOND_AXE:
                    stat[0] = "<yellow>+5 Melee Damage";
                    stat[1] = "<yellow>+25% Overcharge";
                    stat[2] = "<yellow>+15% Attack Speed";
                    stat[3] = "<yellow>+25% Accuracy";
                    stat[4] = "<yellow>+12% Life Steal";
                    break;
                case BOW:
                    stat[0] = "<yellow>+5 Ranged Damage";
                    stat[1] = "<yellow>+20% Snare Chance";
                    stat[2] = "<yellow>+25% Armor Penetration";
                    stat[3] = "<yellow>+25% Accuracy";
                    stat[4] = "<yellow>+6 Ranged Damage";
                    break;
            }

            int i = random.nextInt(5);
            lore.remove(index);
            lore.add(index, TextUtils.color(stat[i]));

            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.reveal.success", ""));
            player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 0.5F);
            updateItem = true;
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
            updateItem = true;
        } else if (cursor.getName().equals(ChatColor.DARK_PURPLE + "Identity Tome")) {
            if (!currentItem.getName().equals(ChatColor.LIGHT_PURPLE + "Unidentified Item")) {
                return;
            }
            currentItem = plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.IDENTIFYING)
                    .build();

            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.identify.success", ""));
            player.playSound(player.getEyeLocation(), Sound.PORTAL_TRAVEL, 1L, 2.0F);
            updateItem = true;
        } else if (cursor.getName().equals(ChatColor.DARK_AQUA + "Faceguy's Tears")) {
            if (currentItem.getName().equals(ChatColor.DARK_AQUA + "Socket Extender") ||
                    currentItem.getName().startsWith(ChatColor.BLUE + "Enchantment Tome - ") ||
                    currentItem.getName().startsWith(ChatColor.GOLD + "Socket Gem -") ||
                    currentItem.getName().equals(ChatColor.AQUA + "Charm of Protection")) {
                return;
            }
            String name = currentItem.getName();
            if (plugin.getSettings().getStringList("config.cannot-be-upgraded", new ArrayList<String>()).contains(ChatColor.stripColor(name))) {
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
            updateItem = true;
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
            boolean damaged = false;
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
                if (random.nextDouble() > 0.1) {
                    level = level - 2;
                    name = name.replace("+" + lev, "+" + String.valueOf(level));
                    currentItem.setName(name);
                    if (currentItem.containsEnchantment(Enchantment.DURABILITY)) {
                        if (level < 7) {
                            currentItem.removeEnchantment(Enchantment.DURABILITY);
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
                        lore.set(i, s.replace("+" + loreLevel, "+" + (loreLevel - 2)));
                        break;
                    }
                    currentItem.setLore(lore);
                    damaged = true;
                    MessageUtils.sendMessage(player, plugin.getSettings().getString("language.upgrade.damaged", ""));
                    player.playSound(player.getEyeLocation(), Sound.LAVA_POP, 1F, 1F);
                    updateItem = true;
                } else {
                    MessageUtils.sendMessage(player, plugin.getSettings().getString("language.upgrade.destroyed", ""));
                    player.playSound(player.getEyeLocation(), Sound.ITEM_BREAK, 1F, 1F);
                    currentItem = null;
                    updateItem = true;
                }
            }
            if (!damaged) {
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
                            currentItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                            currentItem.setItemFlags(Sets.newHashSet(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES));
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
                    updateItem = true;
                }
            }
        }
        if (updateItem) {
            event.setCurrentItem(currentItem);
            cursor.setAmount(cursor.getAmount() - 1);
            event.setCursor(cursor.getAmount() == 0 ? null : cursor);
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            player.updateInventory();
        }
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
