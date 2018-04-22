package info.faceland.loot.data;

public class ItemStat {

  private double minBaseValue;
  private double maxBaseValue;
  private double perLevelIncrease;
  private double perLevelMultiplier;
  private double perRarityIncrease;
  private double perRarityMultiplier;
  private String statString;
  private String perfectStatString;

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

  public String getPerfectStatString() {
    return perfectStatString;
  }

  public void setPerfectStatString(String perfectStatString) {
    this.perfectStatString = perfectStatString;
  }
}
