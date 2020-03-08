package info.faceland.loot.data;

import java.util.ArrayList;
import java.util.List;

public class ItemStat {

  private double minBaseValue;
  private double maxBaseValue;
  private double perLevelIncrease;
  private double perLevelMultiplier;
  private double perRarityIncrease;
  private double perRarityMultiplier;
  private String statString;
  private String statPrefix;
  private String perfectStatPrefix;
  private String specialStatPrefix;
  private List<String> namePrefixes = new ArrayList<>();

  public double getMinBaseValue() {
    return minBaseValue;
  }

  public void setMinBaseValue(double minBaseValue) {
    this.minBaseValue = minBaseValue;
  }

  public double getMaxBaseValue() {
    return maxBaseValue;
  }

  public void setMaxBaseValue(double maxBaseValue) {
    this.maxBaseValue = maxBaseValue;
  }

  public double getPerLevelIncrease() {
    return perLevelIncrease;
  }

  public void setPerLevelIncrease(double perLevelIncrease) {
    this.perLevelIncrease = perLevelIncrease;
  }

  public double getPerLevelMultiplier() {
    return perLevelMultiplier;
  }

  public void setPerLevelMultiplier(double perLevelMultiplier) {
    this.perLevelMultiplier = perLevelMultiplier;
  }

  public double getPerRarityIncrease() {
    return perRarityIncrease;
  }

  public void setPerRarityIncrease(double perRarityIncrease) {
    this.perRarityIncrease = perRarityIncrease;
  }

  public double getPerRarityMultiplier() {
    return perRarityMultiplier;
  }

  public void setPerRarityMultiplier(double perRarityMultiplier) {
    this.perRarityMultiplier = perRarityMultiplier;
  }

  public String getStatString() {
    return statString;
  }

  public void setStatString(String statString) {
    this.statString = statString;
  }

  public String getStatPrefix() {
    return statPrefix;
  }

  public void setStatPrefix(String statPrefix) {
    this.statPrefix = statPrefix;
  }

  public String getPerfectStatPrefix() {
    return perfectStatPrefix;
  }

  public void setPerfectStatPrefix(String perfectStatPrefix) {
    this.perfectStatPrefix = perfectStatPrefix;
  }

  public String getSpecialStatPrefix() {
    return specialStatPrefix;
  }

  public void setSpecialStatPrefix(String specialStatPrefix) {
    this.specialStatPrefix = specialStatPrefix;
  }

  public List<String> getNamePrefixes() {
    return namePrefixes;
  }
}
