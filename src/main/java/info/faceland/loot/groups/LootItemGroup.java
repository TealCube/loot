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
package info.faceland.loot.groups;

import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public final class LootItemGroup implements ItemGroup {

  private final String name;
  private final boolean inverse;
  private final Set<Material> legalMaterials;
  private int minimumCustomData;
  private int maximumCustomData;

  public LootItemGroup(String name, boolean inv) {
    this(name, new HashSet<>(), inv);
  }

  public LootItemGroup(String name, Set<Material> materials, boolean inv) {
    this.name = name;
    this.legalMaterials = materials;
    this.inverse = inv;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (inverse ? 1 : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LootItemGroup that = (LootItemGroup) o;

    return inverse == that.inverse && !(name != null ? !name.equals(that.name) : that.name != null);
  }

  @Override
  public String getName() {
    return name;
  }


  @Override
  public Set<Material> getMaterials() {
    return new HashSet<>(legalMaterials);
  }

  @Override
  public void setMinimumCustomData(int val) {
    minimumCustomData = val;
  }

  @Override
  public void setMaximumCustomData(int val) {
    maximumCustomData = val;
  }

  @Override
  public void addMaterial(Material material) {
    legalMaterials.add(material);
  }

  @Override
  public void removeMaterial(Material material) {
    legalMaterials.remove(material);
  }

  @Override
  public boolean hasMaterial(Material material) {
    return legalMaterials.contains(material);
  }

  @Override
  public int getMinimumCustomData() {
    return minimumCustomData;
  }

  @Override
  public int getMaximumCustomData() {
    return maximumCustomData;
  }

  @Override
  public boolean isInverse() {
    return inverse;
  }

  @Override
  public ItemGroup getInverse() {
    return new LootItemGroup(name, legalMaterials, !inverse);
  }

}
