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
package info.faceland.loot.creatures;

import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.data.JunkItemData;
import com.tealcube.minecraft.bukkit.shade.objecthunter.exp4j.Expression;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public final class LootCreatureMod implements CreatureMod {

  private final EntityType entityType;
  private Expression experienceExpression;
  private Map<CustomItem, Double> customItemDoubleMap;
  private Map<SocketGem, Double> socketGemDoubleMap;
  private Map<Tier, Double> tierDoubleMap;
  private Map<EnchantmentTome, Double> enchantmentStoneDoubleMap;
  private Map<String, Map<JunkItemData, Double>> junkItemDataDoubleMap;

  public LootCreatureMod(EntityType entityType) {
    this.entityType = entityType;
    this.customItemDoubleMap = new HashMap<>();
    this.socketGemDoubleMap = new HashMap<>();
    this.tierDoubleMap = new HashMap<>();
    this.enchantmentStoneDoubleMap = new HashMap<>();
    this.junkItemDataDoubleMap = new HashMap<>();
  }

  @Override
  public EntityType getEntityType() {
    return entityType;
  }

  @Override
  public Map<CustomItem, Double> getCustomItemMults() {
    return new HashMap<>(customItemDoubleMap);
  }

  @Override
  public Map<SocketGem, Double> getSocketGemMults() {
    return new HashMap<>(socketGemDoubleMap);
  }

  @Override
  public Map<Tier, Double> getTierMults() {
    return new HashMap<>(tierDoubleMap);
  }

  @Override
  public double getCustomItemMult(CustomItem ci) {
    if (getCustomItemMults().containsKey(ci)) {
      return getCustomItemMults().get(ci);
    }
    return 0;
  }

  @Override
  public double getSocketGemMult(SocketGem sg) {
    if (getSocketGemMults().containsKey(sg)) {
      return getSocketGemMults().get(sg);
    }
    return 0;
  }

  @Override
  public double getTierMult(Tier t) {
    if (getTierMults().containsKey(t)) {
      return getTierMults().get(t);
    }
    return 0;
  }

  @Override
  public Map<EnchantmentTome, Double> getEnchantmentStoneMults() {
    return new HashMap<>(enchantmentStoneDoubleMap);
  }

  @Override
  public Map<String, Map<JunkItemData, Double>> getJunkMaps() {
    return junkItemDataDoubleMap;
  }

  void setExperienceExpression(Expression experienceExpression) {
    this.experienceExpression = experienceExpression;
  }

  void setEnchantmentStoneDoubleMap(Map<EnchantmentTome, Double> enchantmentStoneDoubleMap) {
    this.enchantmentStoneDoubleMap = enchantmentStoneDoubleMap;
  }

  void setCustomItemDoubleMap(Map<CustomItem, Double> customItemDoubleMap) {
    this.customItemDoubleMap = customItemDoubleMap;
  }

  void setSocketGemDoubleMap(Map<SocketGem, Double> socketGemDoubleMap) {
    this.socketGemDoubleMap = socketGemDoubleMap;
  }

  void setTierDoubleMap(Map<Tier, Double> tierDoubleMap) {
    this.tierDoubleMap = tierDoubleMap;
  }

  public void setJunkItemDataDoubleMap(
      Map<String, Map<JunkItemData, Double>> junkItemDataDoubleMap) {
    this.junkItemDataDoubleMap = junkItemDataDoubleMap;
  }

  @Override
  public int hashCode() {
    return entityType != null ? entityType.hashCode() : 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LootCreatureMod that = (LootCreatureMod) o;

    return entityType == that.entityType;
  }

}
