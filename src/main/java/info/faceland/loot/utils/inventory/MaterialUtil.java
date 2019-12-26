/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package info.faceland.loot.utils.inventory;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import info.faceland.loot.api.tier.Tier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class MaterialUtil {

  private static final double BONUS_ESS_MULT = 0.21;
  private static final double TOOL_QUALITY_ESS_MULT = 0.06;
  private static final double MIN_BONUS_ESS_MULT = 0.08;
  private static final double BASE_ESSENCE_MULT = 0.65;
  private static final Random random = new Random();

  public static ItemStack buildMaterial(Material m, String name, int level, int quality) {
    ItemStack his = new ItemStack(m);

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
      case 4:
        prefix = "Grand";
        color = ChatColor.RED;
        break;
      case 5:
        prefix = "Perfect";
        color = ChatColor.GOLD;
        break;
      default:
        prefix = "Old";
        color = ChatColor.WHITE;
    }
    ItemStackExtensionsKt.setDisplayName(his, color + prefix + " " + name);
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.WHITE + "Item Level: " + Math.min(100, Math.max(1, 3 * (level / 3))));
    lore.add(ChatColor.WHITE + "Quality: " + color + IntStream.range(0, quality).mapToObj(i -> "✪")
        .collect(Collectors.joining("")));
    lore.add(ChatColor.YELLOW + "[ Crafting Component ]");
    ItemStackExtensionsKt.setLore(his, lore);

    return his;
  }

  public static int getQuality(ItemStack stack) {
    for (String line : ItemStackExtensionsKt.getLore(stack)) {
      if (ChatColor.stripColor(line).startsWith("Quality:")) {
        return (int) line.chars().filter(ch -> ch == '✪').count();
      }
    }
    return 1;
  }

  public static ItemStack buildEssence(String type, double itemLevel, double craftLevel,
      int toolQuality, List<String> possibleStats, boolean lucky) {
    int essLevel = 1 + (int) (itemLevel * 0.85 + random.nextDouble() * itemLevel * 0.125);

    String statString = ChatColor.stripColor(
        possibleStats.get(random.nextInt(possibleStats.size())));
    int statVal = getDigit(statString);

    double toolQualityBonus = rollMult(lucky) * (TOOL_QUALITY_ESS_MULT * toolQuality);
    double baseCraftBonus = MIN_BONUS_ESS_MULT * (craftLevel / 100);
    double bonusCraftBonus = rollMult(lucky, 1.3) * (BONUS_ESS_MULT * (craftLevel / 100));

    double essMult = BASE_ESSENCE_MULT + baseCraftBonus + bonusCraftBonus + toolQualityBonus;

    String newStatString = statString.replace(String.valueOf(statVal),
        String.valueOf((int) Math.max(1, statVal * essMult)));

    ItemStack shard = new ItemStack(Material.PRISMARINE_SHARD);
    ItemStackExtensionsKt.setDisplayName(shard, ChatColor.YELLOW + "Item Essence");

    List<String> esslore = new ArrayList<>();
    esslore.add("&fItem Level Requirement: " + essLevel);
    esslore.add("&fItem Type: " + type);
    esslore.add("&e" + newStatString);
    esslore.add("&7&oCraft this together with an");
    esslore.add("&7&ounfinished item to have a");
    esslore.add("&7&ochance of applying this stat!");
    esslore.add("&e[ Crafting Component ]");
    ItemStackExtensionsKt.setLore(shard, TextUtils.color(esslore));

    return shard;
  }

  public static int getItemLevel(ItemStack stack) {
    if (stack.getItemMeta() == null) {
      return -1;
    }
    if (ItemStackExtensionsKt.getLore(stack).get(0) == null) {
      return -1;
    }
    String lvlReqString = ChatColor.stripColor(ItemStackExtensionsKt.getLore(stack).get(0));
    if (!lvlReqString.startsWith("Level Requirement:")) {
      return -1;
    }
    return getDigit(ItemStackExtensionsKt.getLore(stack).get(0));
  }

  public static int getToolLevel(ItemStack stack) {
    if (stack.getItemMeta() == null) {
      return -1;
    }
    if (ItemStackExtensionsKt.getLore(stack).get(0) == null) {
      return -1;
    }
    String lvlReqString = ChatColor.stripColor(ItemStackExtensionsKt.getLore(stack).get(0));
    if (!lvlReqString.startsWith("Craft Skill Requirement:")) {
      return -1;
    }
    return getDigit(ItemStackExtensionsKt.getLore(stack).get(0));
  }

  public static int getDigit(String string) {
    String lev = CharMatcher.digit().or(CharMatcher.is('-')).negate()
        .collapseFrom(ChatColor.stripColor(string), ' ').trim();
    return NumberUtils.toInt(lev.split(" ")[0], 0);
  }

  private static double rollMult(boolean lucky) {
    return rollMult(lucky, 2);
  }

  private static double rollMult(boolean lucky, double exponent) {
    if (!lucky) {
      return Math.pow(random.nextDouble(), exponent);
    }
    return random.nextDouble();
  }

  public static void applyTierLevelData(ItemStack stack, Tier tier, int level) {
    if (tier.getCustomDataStart() != -1) {
      double modelLevel = Math.max(0, level - 1);
      int customModel = tier.getCustomDataStart() + (int) Math.floor(modelLevel / tier.getCustomDataInterval());
      ItemStackExtensionsKt.setCustomModelData(stack, customModel);
    }
  }

  public static int getCustomData(ItemStack stack) {
    if (stack.getItemMeta() == null) {
      return -1;
    }
    if (!stack.getItemMeta().hasCustomModelData()) {
      return -1;
    }
    return stack.getItemMeta().getCustomModelData();
  }
}
