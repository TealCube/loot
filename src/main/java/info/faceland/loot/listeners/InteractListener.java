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

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.fanciful.FancyMessage;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import com.tealcube.minecraft.bukkit.shade.google.common.collect.Sets;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.data.GemCacheData;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.data.ItemRarity;
import info.faceland.loot.data.ItemStat;
import info.faceland.loot.items.prefabs.UpgradeScroll;
import info.faceland.loot.items.prefabs.UpgradeScroll.ScrollType;

import info.faceland.loot.math.LootRandom;
import info.faceland.loot.utils.inventory.InventoryUtil;
import info.faceland.loot.utils.inventory.MaterialUtil;
import info.faceland.strife.util.PlayerDataUtil;
import info.faceland.strife.util.SkillExperienceUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.inventory.PlayerInventory;

public final class InteractListener implements Listener {

    private static final long MILLIS_PER_SEC = 1000;

    private final LootPlugin plugin;
    private LootRandom random;

    public InteractListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom();
    }

    // Loot protection function. Return out of it before the end to allow an item to be picked up!
    // Makes it so only the owner of a drop can pick it up.
    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemPickupEvent(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!event.getItem().hasMetadata("loot-owner")) {
            return;
        }
        if (event.getItem().getMetadata("loot-owner").get(0) == null) {
            return;
        }
        // Fetching item lore that should have been applied in EntityDeathListener
        String owner = event.getItem().getMetadata("loot-owner").get(0).asString();
        Long time = event.getItem().getMetadata("loot-time").get(0).asLong();

        // If the event player's UUID is the same as the owner UUID on the item, allow the pickup
        if (event.getEntity().getUniqueId().toString().equals(owner)) {
            return;
        }

        // If loot-protect-time seconds have passed, allow the item to be picked up!
        if ((System.currentTimeMillis() - time) >= plugin.getSettings().getInt("config.loot-protect-time", 10) *
                MILLIS_PER_SEC) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemSpawnEvent(ItemSpawnEvent event) {
        if (!plugin.getSettings().getBoolean("config.show-item-nameplates", true)) {
            return;
        }
        HiltItemStack itemStack = new HiltItemStack(event.getEntity().getItemStack());
        if (itemStack.getName().equals(itemStack.getDefaultName())) {
            return;
        }
        if (itemStack.getName().equals(ChatColor.GOLD + "REWARD!")) {
            return;
        }
        if (itemStack.getName().startsWith("***{")) {
            return;
        }
        event.getEntity().setCustomName(itemStack.getName());
        event.getEntity().setCustomNameVisible(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnchantingInventory && plugin.getSettings().getBoolean("config.custom-enchanting", true)) {
            event.setCancelled(true);
            MessageUtils.sendMessage(event.getPlayer(), plugin.getSettings().getString("language.enchant.no-open", ""));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnvilOpenEvent(InventoryOpenEvent event) {
        if (event.getInventory() instanceof AnvilInventory) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (!(inv instanceof CraftingInventory)) {
            return;
        }
        InventoryHolder holder = inv.getHolder();
        if (!(holder instanceof Player)) {
            return;
        }
        Player player = (Player) holder;
        if (player.isDead() || player.getHealth() <= 0D) {
            return;
        }
        GemCacheData gemCacheData = plugin.getGemCacheManager().getGemCacheData(player.getUniqueId());
        gemCacheData.updateArmorCache();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCursor() == null
                || event.getCurrentItem().getType() == Material.AIR || event.getCursor().getType() == Material.AIR ||
                !(event.getWhoClicked() instanceof Player) || event.getClick() != ClickType.RIGHT) {
            return;
        }
        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
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
                player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
                return;
            }

            List<String> lore = currentItem.getLore();
            List<String> strippedLore = InventoryUtil.stripColor(lore);
            if (!strippedLore.contains("(Socket)")) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.socket.needs-sockets", ""));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
                return;
            }
            int index = strippedLore.indexOf("(Socket)");

            lore.remove(index);
            lore.addAll(index, TextUtils.color(gem.getLore()));

            currentItem.setLore(lore);

            // strip color, check against that
            // k
            String name = currentItem.getName();
            ChatColor firstColor = getFirstColor(name);
            ChatColor lastColor = getLastColor(name);
            name = ChatColor.stripColor(name);
            int level = getLevel(name);
            name = name.replace("+" + level + " ", "");
            String prefix = "";
            String suffix = "";
            if (!gem.getPrefix().isEmpty()) {
                if (!name.contains(gem.getPrefix())) {
                    if (ChatColor.stripColor(name).startsWith("The ")) {
                        name = name.replace("The ", "");
                        prefix = "The " + gem.getPrefix() + " ";
                    } else {
                        prefix = gem.getPrefix() + " ";
                    }
                }
            }
            if (!gem.getSuffix().isEmpty()) {
                if (!name.contains(gem.getSuffix())) {
                    suffix = " " + gem.getSuffix();
                }
            }
            name = firstColor + (level > 0 ? "+" + level + " " : "") + prefix + name + suffix + lastColor;
            currentItem.setName(TextUtils.color(name));

            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.socket.success", ""));
            player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1L, 2.0F);
            updateItem(event, currentItem);
        } else if (cursor.getName().startsWith(ChatColor.BLUE + "Enchantment Tome - ") && plugin.getSettings()
                .getBoolean("config.custom-enchanting", true)) {
            String stoneName = ChatColor.stripColor(
                    cursor.getName().replace(ChatColor.BLUE + "Enchantment Tome - ", ""));
            EnchantmentTome stone = plugin.getEnchantmentStoneManager().getEnchantmentStone(stoneName);

            if (!isBlockWithinRadius(Material.ENCHANTMENT_TABLE, event.getWhoClicked().getLocation(), 5)) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.no-enchantment-table", ""));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
                return;
            }

            if (stone == null) {
                return;
            }

            if (!plugin.getItemGroupManager().getMatchingItemGroups(currentItem.getType()).containsAll(
                    stone.getItemGroups())) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
                return;
            }

            List<String> lore = currentItem.getLore();
            List<String> strippedLore = InventoryUtil.stripColor(lore);
            if (!strippedLore.contains("(Enchantable)")) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.needs-enchantable", ""));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
                return;
            }

            int index = strippedLore.indexOf("(Enchantable)");
            lore.remove(index);

            if (!StringUtils.isBlank(stone.getStat())) {
                double enchantLevel = PlayerDataUtil.getEnchantLevel(player);
                double bonus = getBonusMultiplier(enchantLevel);
                double rarity = getBonusMultiplier(enchantLevel);
                int size = 6 + (int) (25 * bonus);

                int itemLevel = MaterialUtil.getItemLevel(currentItem);
                double effectiveLevel = Math.max(1, Math.min(enchantLevel * 2, itemLevel));

                List<String> added = new ArrayList<>();
                ItemStat stat = plugin.getStatManager().getStat(stone.getStat());
                added.add(plugin.getStatManager().getFinalStat(stat, effectiveLevel, rarity));
                if (stone.getBar()) {
                    String bars = IntStream.range(0, size).mapToObj(i -> "|").collect(Collectors.joining(""));
                    added.add(TextUtils.color("&9[" + bars + "&0&9]"));
                }
                lore.addAll(index, TextUtils.color(added));
            }

            if (plugin.getSettings().getBoolean("config.enchantments-stack", true)) {
                for (Map.Entry<Enchantment, Integer> entry : stone.getEnchantments().entrySet()) {
                    if (currentItem.containsEnchantment(entry.getKey())) {
                        int previousLevel = currentItem.getEnchantmentLevel(entry.getKey());
                        int newLevel = previousLevel + entry.getValue();
                        currentItem.removeEnchantment(entry.getKey());
                        currentItem.addUnsafeEnchantment(entry.getKey(), newLevel);
                    } else {
                        currentItem.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                boolean fail = true;
                for (Map.Entry<Enchantment, Integer> entry : stone.getEnchantments().entrySet()) {
                    if (currentItem.containsEnchantment(entry.getKey())) {
                        if (currentItem.getEnchantmentLevel(entry.getKey()) < entry.getValue()) {
                            currentItem.removeEnchantment(entry.getKey());
                            currentItem.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                            fail = false;
                        }
                    } else {
                        currentItem.addUnsafeEnchantment(entry.getKey(), entry.getValue());
                        fail = false;
                    }
                }
                if (fail) {
                    MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.pointless", ""));
                    return;
                }
            }

            currentItem.setLore(lore);

            float weightDivisor = stone.getWeight() == 0 ? 2000 : (float)stone.getWeight();
            float exp = 3 + 2000 / weightDivisor;
            SkillExperienceUtil.addCraftExperience(player, exp);
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.success", ""));
            player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1L, 2.0F);
            updateItem(event, currentItem);
        }  else if (cursor.getName().equals(ChatColor.DARK_AQUA + "Socket Extender")) {
            if (isBannedMaterial(currentItem)) {
                return;
            }
            List<String> lore = currentItem.getLore();
            List<String> stripColor = InventoryUtil.stripColor(lore);
            if (!stripColor.contains("(+)")) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.extend.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
                return;
            }
            int index = stripColor.indexOf("(+)");
            lore.set(index, ChatColor.GOLD + "(Socket)");
            currentItem.setLore(lore);

            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.extend.success", ""));
            player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1L, 2.0F);
            updateItem(event, currentItem);
        } else if (cursor.getName().equals(ChatColor.DARK_PURPLE + "Identity Tome")) {
            if (!currentItem.getName().equals(ChatColor.LIGHT_PURPLE + "Unidentified Item")) {
                return;
            }
            int itemLevel = NumberUtils.toInt(CharMatcher.DIGIT.retainFrom(ChatColor.stripColor(currentItem
                    .getItemMeta().getLore().get(0))));
            ItemRarity r;
            Tier t;
            if (itemLevel != 0) {
                r = plugin.getRarityManager().getRandomIdRarity();
                t = plugin.getTierManager().getRandomTier();
                currentItem = plugin.getNewItemBuilder()
                    .withRarity(r)
                    .withTier(t)
                    .withLevel(itemLevel)
                    .build();
                if (r.isBroadcast()) {
                    broadcast(player, currentItem, "ided-item");
                }
            } else {
                currentItem = plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.IDENTIFYING)
                        .build();
            }
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.identify.success", ""));
            player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1L, 2.0F);
            updateItem(event, currentItem);
        } else if (cursor.getName().equals(ChatColor.DARK_AQUA + "Faceguy's Tears")) {
            if (isBannedMaterial(currentItem)) {
                return;
            }
            String name = currentItem.getName();
            if (plugin.getSettings().getStringList("config.cannot-be-upgraded", new ArrayList<String>()).contains(ChatColor.stripColor(name))) {
                return;
            }
            boolean succeed = false;
            List<String> strip = InventoryUtil.stripColor(currentItem.getLore());
            List<String> lore = currentItem.getLore();
            int line = 0;
            for (String s : strip) {
                if (s.startsWith("+")) {
                    String loreLev = CharMatcher.DIGIT.or(CharMatcher.is('-')).retainFrom(s);
                    int loreLevel = NumberUtils.toInt(loreLev);
                    lore.set(line, s.replace("+" + loreLevel, ChatColor.DARK_AQUA + "+" + (loreLevel + 1)));
                    succeed = true;
                    break;
                }
                line++;
            }
            if (!succeed) {
                return;
            }
            currentItem.setLore(lore);
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.upgrade.success", ""));
            player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F);
            updateItem(event, currentItem);
        } else if (cursor.getName().startsWith(ChatColor.DARK_AQUA + "Scroll Augment - ")) {
            String name = ChatColor.stripColor(currentItem.getName()).replace("Upgrade Scroll", "").trim();
            ScrollType type = ScrollType.getByName(name);
            if (type == null) {
                return;
            }
            if (currentItem.getAmount() > 1) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.augment.stack-size", ""));
                return;
            }
            List<String> lore = currentItem.getLore();
            for (String s : lore) {
                if (s.startsWith(ChatColor.DARK_AQUA + "Augment")) {
                    MessageUtils.sendMessage(player, plugin.getSettings().getString("language.augment.already-has", ""));
                    return;
                }
            }
            if (cursor.getName().endsWith("Chance")) {
                if (type == ScrollType.FLAWLESS) {
                    MessageUtils.sendMessage(player, plugin.getSettings().getString("language.augment.too-easy", ""));
                    return;
                }
                if (type.getChanceToFail() != 0) {
                    lore.add(ChatColor.DARK_AQUA + "Augmented: " + ChatColor.WHITE + "Chance");
                    lore.add(ChatColor.GRAY + "Success chance increased by 12%");
                } else {
                    MessageUtils.sendMessage(player, plugin.getSettings().getString("language.augment.nonsense", ""));
                    return;
                }
            } else if (cursor.getName().endsWith("Protect")) {
                if (type.getChanceToFail() != 0) {
                    lore.add(ChatColor.DARK_AQUA + "Augmented: " + ChatColor.WHITE + "Protect");
                    lore.add(ChatColor.GRAY + "Failure will not destroy item");
                } else {
                    MessageUtils.sendMessage(player, plugin.getSettings().getString("language.augment.nonsense", ""));
                    return;
                }
            } else if (cursor.getName().endsWith("Bonus")) {
                lore.add(ChatColor.DARK_AQUA + "Augmented: " + ChatColor.WHITE + "Bonus");
                lore.add(ChatColor.GRAY + "50% chance of double upgrade");
            } else {
                return;
            }
            currentItem.setLore(lore);
            event.setCurrentItem(currentItem);
            cursor.setAmount(cursor.getAmount() - 1);
            event.setCursor(cursor.getAmount() == 0 ? null : cursor);
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            player.updateInventory();
            player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1L, 1.7F);
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.augment.success", ""));

        } else if (cursor.getName().endsWith("Upgrade Scroll")) {
            if (isBannedMaterial(currentItem)) {
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
            int itemUpgradeLevel = ChatColor.stripColor(name).startsWith("+") ? getLevel(ChatColor.stripColor(name)) : 0, lev = itemUpgradeLevel;
            if (itemUpgradeLevel < type.getMinimumLevel() || itemUpgradeLevel > type.getMaximumLevel()) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.upgrade.failure", ""));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
                return;
            }
            boolean succeed = false;
            List<String> strip = InventoryUtil.stripColor(currentItem.getLore());
            for (String s : strip) {
                if (s.startsWith("+")) {
                    succeed = true;
                    break;
                }
            }
            if (!succeed) {
                return;
            }
            boolean augProtect = false;
            boolean augBonus = false;
            double augChance = 0;
            double enchBonus = PlayerDataUtil.getEnchantLevel(player) * 0.001;
            List<String> scrollLore = cursor.getLore();
            for (String s : scrollLore) {
                if (s.startsWith(ChatColor.DARK_AQUA + "Augment")) {
                    if (s.endsWith("Chance")) {
                        augChance = 0.12;
                    } else if (s.endsWith("Protect")) {
                        augProtect = true;
                    } else if (s.endsWith("Bonus")) {
                        augBonus = true;
                    }
                    break;
                }
            }
            if (random.nextDouble() + augChance + enchBonus < type.getChanceToFail()) {
                double damagePercentage = random.nextDouble() * (0.25 + itemUpgradeLevel * 0.115);
                int damageAmount = (int) Math.floor(damagePercentage * currentItem.getType().getMaxDurability()) - 1;
                damageAmount = Math.max(damageAmount, 1);
                if (augProtect) {
                    MessageUtils.sendMessage(player, plugin.getSettings().getString("language.augment.protected", ""));
                    player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                    updateItem(event, currentItem);
                    return;
                }
                if (damageAmount + currentItem.getDurability() >= currentItem.getType().getMaxDurability()) {
                    MessageUtils.sendMessage(player, plugin.getSettings().getString("language.upgrade.destroyed", ""));
                    player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
                    broadcast(player, currentItem, "destroyed-item");
                    updateItem(event, null);
                    return;
                }

                currentItem.setDurability((short)(currentItem.getDurability() + damageAmount));
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.upgrade.damaged", ""));
                updateItem(event, currentItem);
            } else {
                boolean firstTimeUpgrade = false;
                int attributeIncrease = 0;
                if (itemUpgradeLevel == 0) {
                    firstTimeUpgrade = true;
                }
                itemUpgradeLevel++;
                attributeIncrease++;
                if (augBonus && random.nextDouble() <= 0.5) {
                    itemUpgradeLevel++;
                    attributeIncrease++;
                }
                itemUpgradeLevel = Math.min(itemUpgradeLevel, 15);
                if (firstTimeUpgrade) {
                    name = getFirstColor(name) + ("+" + itemUpgradeLevel) + " " + name;
                } else {
                    name = name.replace("+" + lev, "+" + String.valueOf(itemUpgradeLevel));
                }
                currentItem.setName(name);
                if (itemUpgradeLevel >= 10 && currentItem.getEnchantments().isEmpty()) {
                    currentItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                }
                currentItem.setItemFlags(Sets.newHashSet(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE));
                List<String> lore = currentItem.getLore();
                for (int i = 0; i < lore.size(); i++) {
                    String s = lore.get(i);
                    String ss = ChatColor.stripColor(s);
                    if (!ss.startsWith("+")) {
                        continue;
                    }
                    String attributeText = CharMatcher.DIGIT.or(CharMatcher.is('-')).retainFrom(ss);
                    int attributeValue = NumberUtils.toInt(attributeText);
                    lore.set(i, s.replace("+" + attributeValue, "+" + (attributeValue + attributeIncrease)));
                    break;
                }
                currentItem.setLore(lore);
                double exp = 0.5f + (float)Math.pow(1.4, itemUpgradeLevel);
                SkillExperienceUtil.addCraftExperience(player, exp);
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.upgrade.success", ""));
                player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F);
                if (itemUpgradeLevel >= 7) {
                    broadcast(player, currentItem, "upgraded-item");
                }
                updateItem(event, currentItem);
            }
        } else if (cursor.getName().equals(ChatColor.WHITE + "Item Rename Tag")) {
            if (cursor.getLore().get(3).equals(ChatColor.WHITE + "none")) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.rename.notset", ""));
                return;
            }
            if (isBannedMaterial(currentItem)) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.rename.invalid", ""));
                return;
            }
            int level = ChatColor.stripColor(currentItem.getName()).startsWith("+") ? getLevel(ChatColor.stripColor
                    (currentItem.getName())) : 0;
            if (level > 0) {
                currentItem.setName(getFirstColor(currentItem.getName()) + "+" + level + " "
                        + ChatColor.stripColor(cursor.getLore().get(3)));
            } else {
                currentItem.setName(getFirstColor(currentItem.getName())
                        + ChatColor.stripColor(cursor.getLore().get(3)));
            }

            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.rename.success", ""));
            player.playSound(player.getEyeLocation(), Sound.ENTITY_BAT_TAKEOFF, 1F, 0.8F);
            updateItem(event, currentItem);
        } else if (cursor.getName().startsWith(ChatColor.DARK_PURPLE + "Magic Crystal")) {
            List<String> lore = currentItem.getLore();
            boolean valid = false;
            int index = 0;
            int addAmount = 0;
            for (String str : currentItem.getLore()) {
                if (str.startsWith(ChatColor.BLUE + "[") && str.contains("" + ChatColor.BLACK)) {
                    valid = true;
                    int barIndex = str.indexOf("" + ChatColor.BLACK);
                    if (barIndex == str.length() - 5) {
                        MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.full", ""));
                        return;
                    }
                    double enchantLevel = PlayerDataUtil.getEnchantLevel(player);
                    double itemLevel = MaterialUtil.getItemLevel(currentItem);
                    addAmount = 2 + (int)(random.nextDouble() * (2 + Math.max(0, (enchantLevel - itemLevel) * 0.2)));
                    str = str.replace("" + ChatColor.BLACK, "");
                    str = new StringBuilder(str)
                        .insert(Math.min(str.length()-3, barIndex + addAmount), ChatColor.BLACK + "").toString();
                    lore.set(index, str);
                    break;
                }
                index++;
            }
            if (valid) {
                currentItem.setLore(lore);
                SkillExperienceUtil.addCraftExperience(player, 8.5f + addAmount);
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.enchant.refill", ""));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_GLASS_BREAK, 1F, 1.2F);
                player.playSound(player.getEyeLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1F);
                updateItem(event, currentItem);
            }
        }
    }

    private void updateItem(InventoryClickEvent e, HiltItemStack currentItem) {
        e.setCurrentItem(currentItem);
        e.getCursor().setAmount(e.getCursor().getAmount() - 1);
        if (e.getCursor().getAmount() <= 0) {
            e.setCursor(null);
        }
        e.setCancelled(true);
        e.setResult(Event.Result.DENY);
        ((Player)e.getWhoClicked()).updateInventory();
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

    private ChatColor getLastColor(String s) {
        for (int i = s.length() - 1; i >= 0; i--) {
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

    private double getBonusMultiplier(double enchantLevel) {
        double enchant = Math.max(0, Math.min(1, enchantLevel / 60));
        return enchant * random.nextDouble() + (1 - enchant) * Math.pow(random.nextDouble(), 2);
    }

    private int getLevel(String name) {
        String lev = CharMatcher.DIGIT.or(CharMatcher.is('-')).negate().collapseFrom(name, ' ').trim();
        return NumberUtils.toInt(lev.split(" ")[0], 0);
    }

    private void broadcast(Player player, HiltItemStack his, String type) {
        FancyMessage message = new FancyMessage("");
        String mess = plugin.getSettings().getString("language.broadcast." + type, "");
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

    private boolean isBannedMaterial(HiltItemStack currentItem) {
        return currentItem.getType() == Material.BOOK || currentItem.getType() == Material.EMERALD ||
            currentItem.getType() == Material.PAPER || currentItem.getType() == Material.NETHER_STAR ||
            currentItem.getType() == Material.DIAMOND  || currentItem.getType() == Material.GHAST_TEAR ||
            currentItem.getType() == Material.ENCHANTED_BOOK || currentItem.getType() == Material.NAME_TAG ||
            currentItem.getType() == Material.ARROW;
    }

}
