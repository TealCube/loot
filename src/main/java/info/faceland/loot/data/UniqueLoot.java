package info.faceland.loot.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UniqueLoot {

  private double quantityMultiplier;
  private double qualityMultiplier;
  private List<ItemRarity> bonusTierItems = new ArrayList<>();
  private Map<String, Double> gemMap;
  private Map<String, Double> tomeMap;
  private Map<String, Map<String, Double>> customItemMap;

  public UniqueLoot() {
    this.gemMap = new HashMap<>();
    this.tomeMap = new HashMap<>();
    this.customItemMap = new HashMap<>();
  }

  public double getQuantityMultiplier() {
    return quantityMultiplier;
  }

  public void setQuantityMultiplier(double quantityMultiplier) {
    this.quantityMultiplier = quantityMultiplier;
  }

  public double getQualityMultiplier() {
    return qualityMultiplier;
  }

  public void setQualityMultiplier(double qualityMultiplier) {
    this.qualityMultiplier = qualityMultiplier;
  }

  public List<ItemRarity> getBonusEquipment() {
    return bonusTierItems;
  }

  public Map<String, Double> getGemMap() {
    return gemMap;
  }

  public void setGemMap(Map<String, Double> gemMap) {
    this.gemMap = gemMap;
  }

  public Map<String, Double> getTomeMap() {
    return tomeMap;
  }

  public void setTomeMap(Map<String, Double> tomeMap) {
    this.tomeMap = tomeMap;
  }

  public Map<String, Map<String, Double>> getCustomItemMap() {
    return customItemMap;
  }

  public void setCustomItemMap(
      Map<String, Map<String, Double>> customItemMap) {
    this.customItemMap = customItemMap;
  }
}
