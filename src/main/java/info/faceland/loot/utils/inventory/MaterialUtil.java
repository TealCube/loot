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
package info.faceland.loot.utils.inventory;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import info.faceland.loot.math.LootRandom;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public final class MaterialUtil {

    private static final double MAX_ESSENCE_BONUS = 0.35;
    private static final double BASE_ESSENCE_MULT = 0.8;

    private MaterialUtil() {
        // meh
    }

    public static HiltItemStack buildMaterial(Material m, String name, int level, int quality) {
        HiltItemStack his = new HiltItemStack(m);

        ChatColor color;
        String prefix;
        switch (quality) {
            case 2:
                prefix = "Quality";
                color = ChatColor.BLUE;
                break;
            case 3:
                prefix = "Rare";
                color = ChatColor.DARK_PURPLE;
                break;
            default:
                prefix = "Old";
                color = ChatColor.WHITE;
        }
        his.setName(color + prefix + " " + name);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Item Level: " + Math.min(100, Math.max(1, 3 * (Math.round(level / 3)))));
        lore.add(ChatColor.WHITE + "Quality: " + color + IntStream.range(0, quality).mapToObj(i -> "✪")
            .collect(Collectors.joining("")));
        lore.add(ChatColor.YELLOW + "[ Crafting Component ]");
        his.setLore(lore);

        return his;
    }

    public static HiltItemStack buildEssence(Player player, String type, int itemLevel, int craftingLevel, List<String> possibleStats) {
        // Item Level Req = item level minus 0-5 minus tp to 10% of item level
        LootRandom random = new LootRandom();
        int itemLevelReq = (int)((double)itemLevel-random.nextDouble()*((double)itemLevel / 10)-random.nextDouble()*5);
        itemLevelReq = Math.max(itemLevelReq, 1);

        String statString = ChatColor.stripColor(possibleStats.get(random.nextInt(possibleStats.size())));
        double craftBonus = MAX_ESSENCE_BONUS * (craftingLevel / 100) * random.nextDouble();
        double luckMult = Math.pow(random.nextDouble(), 2);
        if (player.hasPotionEffect(PotionEffectType.LUCK)) {
            luckMult = Math.max(luckMult, Math.pow(random.nextDouble(), 2));
        }
        double luckyBonus = (MAX_ESSENCE_BONUS - craftBonus) * luckMult;
        int statVal = getDigit(statString);
        double newVal = BASE_ESSENCE_MULT + craftBonus + luckyBonus;
        String newStatString = statString.replace(String.valueOf(statVal),
            String.valueOf((int)Math.max(newVal*statVal, 1.0)));

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
        return shard;
    }

    public static int getItemLevel(HiltItemStack stack) {
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

    public static int getToolLevel(HiltItemStack stack) {
        if (stack.getItemMeta() == null) {
            return -1;
        }
        if (stack.getLore().get(0) == null) {
            return -1;
        }
        String lvlReqString = ChatColor.stripColor(stack.getLore().get(0));
        if (!lvlReqString.startsWith("Craft Skill Requirement:")) {
            return -1;
        }
        return getDigit(stack.getLore().get(0));
    }

    public static int getDigit(String string) {
        String lev = CharMatcher.DIGIT.or(CharMatcher.is('-')).negate().collapseFrom(ChatColor.stripColor(string), ' ').trim();
        return NumberUtils.toInt(lev.split(" ")[0], 0);
    }
}
