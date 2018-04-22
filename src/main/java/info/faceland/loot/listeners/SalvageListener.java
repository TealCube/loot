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
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.utils.inventory.InventoryUtil;
import info.faceland.strife.events.StrifeCraftEvent;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class SalvageListener implements Listener {

    private final LootPlugin plugin;
    private LootRandom random;

    public SalvageListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.RIGHT) {
            return;
        }
        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR ||
            !(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        HiltItemStack currentItem = new HiltItemStack(event.getCurrentItem());
        HiltItemStack cursor = new HiltItemStack(event.getCursor());
        if (cursor.getName() == null) {
            return;
        }
        System.out.println();
        if (!cursor.getName().endsWith("Craftsman's Tools")) {
            return;
        }

        int itemLevel = getItemLevel(currentItem);
        if (itemLevel == -1) {
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.craft.no-level", ""));
            event.setCancelled(true);
            return;
        }
        int craftingLevel = plugin.getStrifePlugin().getPlayerDataUtil().getCraftLevel(player);
        if (!isHighEnoughCraftingLevel(craftingLevel, itemLevel)) {
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.craft.low-level", ""));
            event.setCancelled(true);
            return;
        }

        List<String> lore = currentItem.getLore();
        List<String> possibleStats = new ArrayList<>();
        for (String str : lore) {
            if (str.startsWith("" + ChatColor.GREEN) || str.startsWith("" + ChatColor.YELLOW)) {
                possibleStats.add(str);
            }
        }
        if (possibleStats.size() == 0) {
            MessageUtils.sendMessage(player, plugin.getSettings().getString("language.craft.no-stats", ""));
            event.setCancelled(true);
            return;
        }

        String type = InventoryUtil.getItemType(currentItem);
        // Item Level Req = item level minus 0-5 minus tp to 10% of item level
        int itemLevelReq = (int)((double)itemLevel-random.nextDouble()*((double)itemLevel / 10)-random.nextDouble()*5);
        itemLevelReq = Math.max(itemLevelReq, 1);

        String statString = ChatColor.stripColor(possibleStats.get(random.nextInt(possibleStats.size())));
        double bonus = Math.pow(random.nextDouble(), 2) * (0.3 + craftingLevel * 0.01);
        int statVal = getDigit(statString);
        double newVal = Math.max(1, getDigit(statString) * (0.5 + bonus));
        String newStatString = statString.replace(String.valueOf(statVal), String.valueOf((int)newVal));

        HiltItemStack shard = new HiltItemStack(Material.PRISMARINE_SHARD);
        shard.setName(ChatColor.YELLOW + "Item Essence");
        List<String> esslore = shard.getLore();
        esslore.add(TextUtils.color("&fItem Level Requirement: " + itemLevelReq));
        esslore.add(TextUtils.color("&fItem Type: " + type));
        esslore.add(TextUtils.color("&e" + newStatString));
        esslore.add(TextUtils.color("&7&oCraft this together with an"));
        esslore.add(TextUtils.color("&7&ounfinished item to have a"));
        esslore.add(TextUtils.color("&7&ochance of applying this stat!"));
        esslore.add(TextUtils.color("&e[ Crafting Component ]"));
        shard.setLore(esslore);

        event.setCurrentItem(null);
        player.getInventory().addItem(shard);

        player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
        double exp = 2 + itemLevelReq/10 + statVal/100;
        exp += exp * bonus;
        Bukkit.getServer().getPluginManager().callEvent(new StrifeCraftEvent(player, (float)exp));
        event.setCancelled(true);
    }

    private int getItemLevel(HiltItemStack stack) {
        if (stack.getItemMeta() == null) {
            return -1;
        }
        if (stack.getLore().get(0) == null) {
            return -1;
        }
        String lvlReqString = ChatColor.stripColor(stack.getLore().get(0));
        if (!lvlReqString.startsWith("Level Requirement:")) {
            return -1;
        }
        return getDigit(stack.getLore().get(0));
    }

    private int getDigit(String string) {
        String lev = CharMatcher.DIGIT.or(CharMatcher.is('-')).negate().collapseFrom(ChatColor.stripColor(string), ' ').trim();
        return NumberUtils.toInt(lev.split(" ")[0], 0);
    }

    private boolean isHighEnoughCraftingLevel(int craftLevel, int itemLevel) {
        int lvlBonus = (int)Math.floor((double)craftLevel/5) * 8;
        return 10 + lvlBonus >= itemLevel;
    }
}
