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
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SaleIcon extends MenuItem {

  private PawnMenu pawnMenu;
  private int price;
  private ItemStack stack;
  private boolean checkRare = false;

  SaleIcon(PawnMenu pawnMenu) {
    super("", new ItemStack(Material.AIR));
    this.pawnMenu = pawnMenu;
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    if (stack == null) {
      return getIcon();
    }
    ItemStack finalIcon = stack.clone();
    List<String> newLore = new ArrayList<>(ItemStackExtensionsKt.getLore(finalIcon));
    newLore.add("");
    newLore.add(TextUtils.color("&6Sale Price: &f" + price + " &fBits"));
    ItemStackExtensionsKt.setLore(finalIcon, newLore);
    return finalIcon;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    price = 0;
    checkRare = false;
    stack = null;
    event.setWillUpdate(true);
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public ItemStack getStack() {
    return stack;
  }

  public void setStack(ItemStack stack) {
    this.stack = stack;
  }

  public boolean isCheckRare() {
    return checkRare;
  }

  public void setCheckRare(boolean checkRare) {
    this.checkRare = checkRare;
  }
}
