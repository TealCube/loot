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
package info.faceland.loot.managers;

import info.faceland.loot.data.DeconstructData;
import info.faceland.loot.utils.MaterialUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public final class LootCraftMatManager {

  private final Map<Material, String> craftMaterials;
  private final Set<DeconstructData> deconstructDataSet;
  private Random random;

  public LootCraftMatManager() {
    deconstructDataSet = new HashSet<>();
    craftMaterials = new HashMap<>();
    random = new Random();
  }

  public void addCraftMaterial(Material material, String name) {
    craftMaterials.put(material, name);
  }

  public Map<Material, String> getCraftMaterials() {
    return craftMaterials;
  }

  public void addDeconstructData(DeconstructData deconstructData) {
    deconstructDataSet.add(deconstructData);
  }

  public Set<DeconstructData> getDeconstructDataSet() {
    return deconstructDataSet;
  }

  public Material getMaterial(ItemStack stack) {
    for (DeconstructData data : deconstructDataSet) {
      if (data.getMaterial() != stack.getType()) {
        continue;
      }
      int customData = MaterialUtil.getCustomData(stack);
      if (data.getMinCustomData() == -1 && data.getMaxCustomData() == -1) {
        return DeconstructData.getResultMaterial(data, craftMaterials.keySet());
      }
      if (customData >= data.getMinCustomData() && customData <= data.getMaxCustomData()) {
        return DeconstructData.getResultMaterial(data, craftMaterials.keySet());
      }
    }

    List<Material> possibleMaterials = new ArrayList<>();
    Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
    while (it.hasNext()) {
      Recipe recipe;
      try {
        recipe = it.next();
      } catch (NoSuchElementException e) {
        // THIS IS HORRIBLE, BUT SPIGOT HAS MADE AN ITERATOR INCOMPETENT BEYOND
        // MY EXPERIENCE LEVEL, THAT CAN RETURN NoSuchElement EVEN WITHIN A .hasNext()
        // WHILE LOOP... MAY GOD HAVE MERCY ON OUR SOULS
        break;
      }
      if (recipe.getResult().getType() == stack.getType() && recipe instanceof ShapedRecipe) {
        ShapedRecipe shaped = (ShapedRecipe) recipe;
        for (ItemStack i : shaped.getIngredientMap().values()) {
          if (i == null || i.getType() == Material.AIR) {
            continue;
          }
          if (getCraftMaterials().keySet().contains(i.getType())) {
            possibleMaterials.add(i.getType());
          }
        }
      }
    }
    if (possibleMaterials.isEmpty()) {
      return null;
    }
    List<Material> mats = new ArrayList<>(possibleMaterials);
    return mats.get(random.nextInt(mats.size()));
  }
}
