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
    boolean tooClose;
    if (lastPlayerLoc == null || lastEnemyLoc == null) {
      tooClose = false;
    } else if (newPlayerLoc.getWorld() != lastPlayerLoc.getWorld()) {
      tooClose = false;
    } else if (newEnemyLoc.getWorld() != lastEnemyLoc.getWorld()) {
      tooClose = false;
    } else if (newPlayerLoc.distanceSquared(lastPlayerLoc) < 2) {
      tooClose = true;
    } else if (newEnemyLoc.distanceSquared(lastEnemyLoc) < 2) {
      tooClose = true;
    } else {
      tooClose = false;
    }
    lastPlayerLoc = newPlayerLoc;
    lastEnemyLoc = newEnemyLoc;
    return tooClose;
  }

}
