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
import com.tealcube.minecraft.bukkit.shade.fanciful.FancyMessage;
import java.util.ArrayList;
import java.util.List;

import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class InventoryUtil {

  private InventoryUtil() {
    // meh
  }

  public static void broadcast(Player player, ItemStack his, String format) {
    FancyMessage message = new FancyMessage("");
    String[] split = format.split(" ");
    for (int i = 0; i < split.length; i++) {
      String s = split[i];
      String str = TextUtils.color(s);
      if (str.contains("%player%")) {
        message.then(str.replace("%player%", player.getDisplayName()));
      } else if (str.contains("%item%")) {
        message.then(str.replace("%item%", ItemStackExtensionsKt.getDisplayName(his))).itemTooltip(his);
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

  public static ChatColor getFirstColor(String s) {
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

  public static ChatColor getLastColor(String s) {
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
    if (itemStack.getItemMeta().getLore().size() > 1 && itemStack.getItemMeta().getLore().get(1)
        .endsWith("Wand")) {
      return true;
    }
    return false;
  }
}
