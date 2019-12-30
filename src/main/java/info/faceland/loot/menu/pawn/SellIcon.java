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
package info.faceland.loot.menu.pawn;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import info.faceland.loot.LootPlugin;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellIcon extends MenuItem {

  private PawnMenu menu;
  private List<String> lore = new ArrayList<>();

  SellIcon(PawnMenu menu) {
    super(TextUtils.color("&e&lSell Items"), new ItemStack(Material.GOLD_INGOT));
    this.menu = menu;
    lore.add(TextUtils.color("&7Click to sell the items"));
    lore.add(TextUtils.color("&7selected above!"));
    lore.add("");
    lore.add(TextUtils.color("&6Total: &f{total} Bits"));
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack stack = getIcon().clone();
    ItemStackExtensionsKt.setDisplayName(stack, getDisplayName());
    List<String> newLore = new ArrayList<>();
    int total = menu.getTotal();
    for (String s : lore) {
      newLore.add(s.replace("{total}", String.valueOf(total)));
    }
    ItemStackExtensionsKt.setLore(stack, newLore);
    return stack;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    event.getPlayer()
        .playSound(event.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1, 1.2f);
    int total = menu.sellItems(event.getPlayer());
    if (total == 0) {
      return;
    }
    menu.update(event.getPlayer());
    LootPlugin.getInstance().getEconomy().depositPlayer(event.getPlayer(), total);
    event.getPlayer()
        .playSound(event.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1, 2.0f);
    MessageUtils.sendMessage(event.getPlayer(), "&a+&f" + total + " &aBits!");
    if (menu.getTotal() > 0) {
      MessageUtils.sendMessage(event.getPlayer(),
          "&e&l[!] &eHigh rarity items found! Press sell again to confirm the sale of these items.");
    }
    event.setWillUpdate(true);
  }
}
