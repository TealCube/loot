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
package info.faceland.loot.utils;

import info.faceland.loot.data.ItemStat;
import io.pixeloutlaw.minecraft.spigot.garbage.BroadcastMessageUtil;
import io.pixeloutlaw.minecraft.spigot.garbage.BroadcastMessageUtil.BroadcastItemVisibility;
import io.pixeloutlaw.minecraft.spigot.garbage.BroadcastMessageUtil.BroadcastTarget;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtil {

  private InventoryUtil() {
    // meh
  }

  public static void broadcast(Player player, ItemStack his, String format) {
    broadcast(player, his, format, true);
  }

  public static void broadcast(Player player, ItemStack his, String format, boolean sendToAll) {
    BroadcastTarget target = sendToAll ? BroadcastTarget.SERVER : BroadcastTarget.PLAYER;
    BroadcastMessageUtil.INSTANCE.broadcastItem(format, player, his, target, BroadcastItemVisibility.SHOW);
  }

  public static net.md_5.bungee.api.ChatColor getRollColor(ItemStat stat, double roll) {
    return getRollColor(roll, stat.getMinHue(), stat.getMaxHue(), stat.getMinSaturation(), stat.getMaxSaturation(),
        stat.getMinBrightness(), stat.getMaxBrightness());
  }

  public static net.md_5.bungee.api.ChatColor getRollColor(double roll, float minHue, float maxHue, float minSat,
      float maxSat, float minBright, float maxBright) {
    if (roll < 0.92) {
      roll = Math.max(0, (roll - 0.5) * 2);
    } else {
      roll = 1;
    }
    float hue = minHue + (maxHue - minHue) * (float) roll;
    float saturation = minSat + (maxSat - minSat) * (float) roll;
    float brightness = minBright + (maxBright - minBright) * (float) roll;
    return net.md_5.bungee.api.ChatColor.of(Color.getHSBColor(hue, saturation, brightness));
  }

  public static ChatColor getFirstColor(String s) {
    for (int i = 0; i < s.length() - 1; i++) {
      if (!s.startsWith(ChatColor.COLOR_CHAR + "", i)) {
        continue;
      }
      ChatColor c = ChatColor.getByChar(s.substring(i + 1, i + 2));
      if (c != null) {
        return c;
      }
    }
    return ChatColor.RESET;
  }

  public static ChatColor getLastColor(String s) {
    for (int i = s.length() - 1; i >= 0; i--) {
      if (!s.startsWith(ChatColor.COLOR_CHAR + "", i)) {
        continue;
      }
      ChatColor c = ChatColor.getByChar(s.substring(i + 1, i + 2));
      if (c != null) {
        return c;
      }
    }
    return ChatColor.RESET;
  }

  public static String getItemType(ItemStack itemStack) {
    Material material = itemStack.getType();
    if (isWand(itemStack)) {
      return "Wand";
    }
    return WordUtils.capitalizeFully(material.toString().replace("_", " "));
  }

  public static List<String> stripColor(List<String> strings) {
    List<String> ret = new ArrayList<>();
    for (String s : strings) {
      ret.add(ChatColor.stripColor(s));
    }
    return ret;
  }

  // Dirty hardcoded check for Faceland, as wood swords are still a thing, but wands use the item type
  private static boolean isWand(ItemStack itemStack) {
    if (!itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
      return false;
    }
    return itemStack.getItemMeta().getLore().size() > 1 && itemStack.getItemMeta().getLore().get(1)
        .endsWith("Wand");
  }
}
