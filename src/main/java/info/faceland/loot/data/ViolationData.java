package info.faceland.loot.data;

import org.bukkit.Location;

public class ViolationData {

  private int violationLevel;
  private Location lastPlayerLoc;
  private Location lastEnemyLoc;

  public ViolationData() {
    this.violationLevel = 0;
  }

  public int getViolationLevel() {
    return violationLevel;
  }

  public void setViolationLevel(int violationLevel) {
    this.violationLevel = violationLevel;
  }

  public Location getLastPlayerLoc() {
    return lastPlayerLoc;
  }

  public void setLastPlayerLoc(Location lastPlayerLoc) {
    this.lastPlayerLoc = lastPlayerLoc;
  }

  public Location getLastEnemyLoc() {
    return lastEnemyLoc;
  }

  public void setLastEnemyLoc(Location lastEnemyLoc) {
    this.lastEnemyLoc = lastEnemyLoc;
  }

  public boolean isEntityTooClose(Location newPlayerLoc, Location newEnemyLoc) {
    if (newPlayerLoc.distanceSquared(lastPlayerLoc) < 2) {
      return true;
    }
    if (newEnemyLoc.distanceSquared(lastEnemyLoc) < 2) {
      return true;
    }
    return false;
  }

}
