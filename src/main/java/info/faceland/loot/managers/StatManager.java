package info.faceland.loot.managers;

import info.faceland.loot.data.ItemStat;
import info.faceland.loot.data.StatResponse;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.utils.InventoryUtil;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;

public class StatManager {

  private final Map<String, ItemStat> itemStats;
  private final LootRandom random;

  public StatManager() {
    this.itemStats = new HashMap<>();
    this.random = new LootRandom();
  }

  public ItemStat getStat(String name) {
    if (!itemStats.containsKey(name)) {
      System.out.println("ERROR! No stat found for name " + name + "!");
      return null;
    }
    return itemStats.get(name);
  }

  public void addStat(String name, ItemStat itemStat) {
    itemStats.put(name, itemStat);
  }

  public void removeStat(String name) {
    itemStats.remove(name);
  }

  public Map<String, ItemStat> getLoadedStats() {
    return itemStats;
  }

  public StatResponse getFinalStat(ItemStat itemStat, double level, double rarity,
      boolean special) {
    StatResponse response = new StatResponse();
    double statRoll;
    float baseRollMultiplier;
    if (itemStat.getMinBaseValue() == itemStat.getMaxBaseValue()) {
      statRoll = itemStat.getMinBaseValue();
      baseRollMultiplier = 0;
    } else {
      baseRollMultiplier = (float) Math.pow(random.nextDouble(), 2.5);
      statRoll =
          itemStat.getMinBaseValue() + baseRollMultiplier * (itemStat.getMaxBaseValue() - itemStat
              .getMinBaseValue());
    }
    response.setStatRoll(baseRollMultiplier);

    statRoll += itemStat.getPerLevelIncrease() * level + itemStat.getPerRarityIncrease() * rarity;
    statRoll *=
        1 + itemStat.getPerLevelMultiplier() * level + itemStat.getPerRarityMultiplier() * rarity;

    TextComponent component = new TextComponent();
    component.setItalic(false);
    if (special) {
      component.setColor(ChatColor.of(itemStat.getSpecialStatPrefix()));
      component.setObfuscated(true);
    } else {
      if (StringUtils.isNotBlank(itemStat.getStatPrefix())) {
        if (baseRollMultiplier >= 0.9) {
          component.setColor(ChatColor.of(itemStat.getPerfectStatPrefix()));
        } else {
          component.setColor(ChatColor.of(itemStat.getStatPrefix()));
        }
      } else {
        component.setColor(InventoryUtil.getRollColor(itemStat, baseRollMultiplier));
      }
    }

    String value = Integer.toString((int) statRoll);
    String statString = itemStat.getStatString().replace("{}", value);
    component.setText(statString);
    response.setStatString(component.toLegacyText());

    if (!itemStat.getNamePrefixes().isEmpty()) {
      response.setStatPrefix(itemStat.getNamePrefixes().get(random.nextInt(itemStat.getNamePrefixes().size())));
    }

    return response;
  }

  public String getMinStat(ItemStat itemStat, double level, double rarity) {
    double statRoll = itemStat.getMinBaseValue();
    statRoll += itemStat.getPerLevelIncrease() * level + itemStat.getPerRarityIncrease() * rarity;
    statRoll *=
        1 + itemStat.getPerLevelMultiplier() * level + itemStat.getPerRarityMultiplier() * rarity;
    String returnString = itemStat.getStatPrefix();
    returnString = returnString + itemStat.getStatString();
    String value = Integer.toString((int) statRoll);
    return returnString.replace("{}", value);
  }

  public String getMaxStat(ItemStat itemStat, double level, double rarity) {
    double statRoll = itemStat.getMaxBaseValue();
    statRoll += itemStat.getPerLevelIncrease() * level + itemStat.getPerRarityIncrease() * rarity;
    statRoll *=
        1 + itemStat.getPerLevelMultiplier() * level + itemStat.getPerRarityMultiplier() * rarity;
    String returnString = itemStat.getStatPrefix();
    returnString = returnString + itemStat.getStatString();
    String value = Integer.toString((int) statRoll);
    return returnString.replace("{}", value);
  }
}
