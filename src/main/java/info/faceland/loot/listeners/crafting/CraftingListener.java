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
package info.faceland.loot.listeners.crafting;

import static info.faceland.loot.utils.inventory.InventoryUtil.stripColor;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.recipe.EquipmentRecipeBuilder;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public final class CraftingListener implements Listener {

    private final LootPlugin plugin;
    private LootRandom random;

    public CraftingListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCraftItemEvent(CraftItemEvent event) {
        for (ItemStack is : event.getInventory().getMatrix()) {
            if (is == null || is.getType() == Material.AIR) {
                continue;
            }
            HiltItemStack his = new HiltItemStack(is);
            if (his.getName().equals(ChatColor.DARK_AQUA + "Socket Extender") ||
                his.getName().startsWith(ChatColor.BLUE + "Enchantment Tome - ") ||
                his.getName().startsWith(ChatColor.GOLD + "Socket Gem -") ||
                his.getName().startsWith(ChatColor.DARK_AQUA + "Scroll Augment -")) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCraftEvent(CraftItemEvent event) {
        ItemStack resultStack = event.getCurrentItem();
        if (!resultStack.hasItemMeta() || !resultStack.getItemMeta().hasDisplayName()) {
            return;
        }
        if (!resultStack.getItemMeta().getDisplayName().equals(EquipmentRecipeBuilder.INFUSE_NAME)) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player)event.getWhoClicked();
        List<String> essenceStats = new ArrayList<>();
        HiltItemStack baseItem = null;
        for (ItemStack is : event.getInventory().getMatrix()) {
            if (is == null || is.getType() == Material.AIR) {
                continue;
            }
            HiltItemStack loopItem = new HiltItemStack(is);
            if (isEssence(loopItem)) {
                if (getEssenceMaterial(loopItem) != resultStack.getType()) {
                    MessageUtils
                        .sendMessage(player, plugin.getSettings().getString("language.craft.wrong-ess-type", ""));
                    return;
                }
                essenceStats.add(getEssenceStat(loopItem));
                continue;
            }
            if (is.getType() == resultStack.getType()) {
                baseItem = loopItem;
                continue;
            }
            System.out.println("ERROR! SOMETHING IS UP WITH ESSENCE CRAFTING!");
            return;
        }
        if (baseItem == null) {
            return;
        }
        List<String> lore = baseItem.getLore();
        List<String> strippedLore = stripColor(lore);
        if (!strippedLore.contains("[ Crafted Stat Slot ]")) {
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.craft.no-slots", ""));
            return;
        }
        List<String> craftedStatTypes = new ArrayList<>();
        for (String str : lore) {
            if (str.startsWith(ChatColor.AQUA + "")) {
                str = CharMatcher.JAVA_LETTER.or(CharMatcher.is(' ')).retainFrom(ChatColor.stripColor(str).trim());
                craftedStatTypes.add(str);
            }
        }
        for (String str : essenceStats) {
            str = CharMatcher.JAVA_LETTER.or(CharMatcher.is(' ')).retainFrom(ChatColor.stripColor(str).trim());
            if (craftedStatTypes.contains(str)) {
                MessageUtils.sendMessage(player, plugin.getSettings().getString("language.craft.stat-exists", ""));
                return;
            }
        }
        int selectedSlot = random.nextInt(8);
        if (selectedSlot > essenceStats.size() - 1) {
            event.setCurrentItem(baseItem);
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.craft.ess-failed", ""));
            event.setCancelled(false);
            return;
        }
        int slotIndex = strippedLore.indexOf("[ Crafted Stat Slot ]");
        lore.remove(slotIndex);
        lore.add(slotIndex, ChatColor.AQUA + ChatColor.stripColor(essenceStats.get(selectedSlot)));
        baseItem.setLore(lore);

        event.setCurrentItem(baseItem);
        MessageUtils.sendMessage(player, plugin.getSettings().getString("language.craft.ess-success", ""));
        event.setCancelled(false);
    }

    private boolean isEssence(HiltItemStack itemStack) {
        if (itemStack.getType() != Material.PRISMARINE_SHARD || StringUtils.isBlank(itemStack.getName())) {
            return false;
        }
        if (!ChatColor.stripColor(itemStack.getName()).equals("Item Essence")) {
            return false;
        }
        List<String> lore = itemStack.getLore();
        List<String> strippedLore = stripColor(lore);
        if (strippedLore.get(0) == null || !strippedLore.get(0).startsWith("Item Level Requirement")) {
            return false;
        }
        if (strippedLore.get(1) == null || !strippedLore.get(1).startsWith("Item Type")) {
            return false;
        }
        if (strippedLore.get(2) == null) {
            return false;
        }
        return true;
    }

    private Material getEssenceMaterial(HiltItemStack itemStack) {
        String str = ChatColor.stripColor(itemStack.getLore().get(1)).replace("Item Type: ", "");
        str = str.replace(" ", "_").toUpperCase();
        Material material;
        try {
            material = Material.getMaterial(str);
        } catch (Exception e) {
            System.out.println("INVALID MATERIAL ON ESSENCE! What tf is " + str + "?");
            return null;
        }
        return material;
    }

    private String getEssenceStat(HiltItemStack itemStack) {
        return itemStack.getLore().get(2);
    }
}
