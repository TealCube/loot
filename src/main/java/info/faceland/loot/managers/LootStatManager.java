package info.faceland.loot.managers;

import info.faceland.loot.api.managers.StatManager;
import info.faceland.loot.data.ItemRarity;
import info.faceland.loot.data.ItemStat;
import info.faceland.loot.math.LootRandom;
import java.util.HashMap;
import java.util.Map;

public class LootStatManager implements StatManager {
  private final Map<String, ItemStat> itemStats;
  private final LootRandom random;

  public LootStatManager() {
    this.itemStats = new HashMap<>();
    this.random = new LootRandom();
  }

  @Override
  public ItemStat getStat(String name) {
    if (!itemStats.containsKey(name)) {
      System.out.println("ERROR! No stat found for name " + name + "!");
      return null;
    }
    return itemStats.get(name);
  }

  @Override
  public void addStat(String name, ItemStat itemStat) {
    itemStats.put(name, itemStat);
  }

  @Override
  public void removeStat(String name) {
    itemStats.remove(name);
  }

  @Override
  public Map<String, ItemStat> getLoadedStats() {
    return itemStats;
  }

  @Override
  public String getFinalStat(String statName, int level, ItemRarity rarity) {
    ItemStat stat = itemStats.get(statName);
    return getFinalStat(stat, level, rarity);
  }

  @Override
  public String getFinalStat(ItemStat itemStat, int level, ItemRarity rarity) {
    double statRoll;
    double baseRollMultiplier;
    if (itemStat.getMinBaseValue() == itemStat.getMaxBaseValue()) {
      statRoll = itemStat.getMinBaseValue();
      baseRollMultiplier = 0;
    } else {
      baseRollMultiplier = Math.pow(random.nextDouble(), 2.5);
      statRoll = itemStat.getMinBaseValue() + baseRollMultiplier * (itemStat.getMaxBaseValue() - itemStat.getMinBaseValue());
    }
    statRoll += itemStat.getPerLevelIncrease() * level + itemStat.getPerRarityIncrease() * rarity.getPower();
    statRoll *= 1 + itemStat.getPerLevelMultiplier() * level + itemStat.getPerRarityMultiplier() * rarity.getPower();
    String returnString;
    if (baseRollMultiplier >= 0.9) {
      returnString = itemStat.getPerfectStatString();
    } else {
      returnString = itemStat.getStatString();
    }
    String value = Integer.toString((int)statRoll);
    return returnString.replace("{}", value);
  }
}
