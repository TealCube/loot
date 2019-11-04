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
package info.faceland.loot.items;

import com.tealcube.minecraft.bukkit.TextUtils;
import info.faceland.loot.api.items.CustomItem;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class LootCustomItem implements CustomItem {

  private final String name;
  private String displayName;
  private List<String> lore;
  private Material material;
  private double weight;
  private double distanceWeight;
  private int levelBase;
  private int levelRange;
  private int customDataNumber;
  private boolean broadcast;
  private boolean quality;

  public LootCustomItem(String name, Material material) {
    this.name = name;
    this.material = material;
    this.lore = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }

  void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public List<String> getLore() {
    return lore;
  }

  void setLore(List<String> lore) {
    this.lore = lore;
  }

  public Material getMaterial() {
    return material;
  }

  @Override
  public ItemStack toItemStack(int amount) {
    ItemStack itemStack = new ItemStack(material);
    if (itemStack.getType() == Material.AIR) {
      return itemStack;
    }
    itemStack.setAmount(amount);
    ItemStackExtensionsKt.setDisplayName(itemStack, TextUtils.color(this.displayName));
    ItemStackExtensionsKt.setLore(itemStack, TextUtils.color(this.lore));
    ItemStackExtensionsKt.addItemFlags(itemStack, ItemFlag.HIDE_ATTRIBUTES);
    if (customDataNumber != -1) {
      ItemStackExtensionsKt.setCustomModelData(itemStack, customDataNumber);
    }
    return itemStack;
  }

  @Override
  public double getWeight() {
    return weight;
  }

  void setWeight(double weight) {
    this.weight = weight;
  }

  @Override
  public double getDistanceWeight() {
    return distanceWeight;
  }

  @Override
  public int getLevelBase() {
    return levelBase;
  }

  @Override
  public int getLevelRange() {
    return levelRange;
  }

  void setDistanceWeight(double distanceWeight) {
    this.distanceWeight = distanceWeight;
  }

  void setLevelBase(int levelBase) {
    this.levelBase = levelBase;
  }

  void setLevelRange(int levelRange) {
    this.levelRange = levelRange;
  }

  @Override
  public int getCustomDataNumber() {
    return customDataNumber;
  }

  public void setCustomDataNumber(int customDataNumber) {
    this.customDataNumber = customDataNumber;
  }

  @Override
  public boolean isBroadcast() {
    return broadcast;
  }

  @Override
  public boolean canBeQuality() {
    return quality;
  }

  void setBroadcast(boolean broadcast) {
    this.broadcast = broadcast;
  }

  void setQuality(boolean quality) {
    this.quality = quality;
  }

  void setMaterial(Material material) {
    this.material = material;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LootCustomItem that = (LootCustomItem) o;

    return !(name != null ? !name.equals(that.name) : that.name != null);
  }

}
