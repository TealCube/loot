package info.faceland.loot.events;

import info.faceland.loot.data.ItemRarity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LootDropEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  private double quantityMultiplier;
  private double qualityMultiplier;
  private List<ItemRarity> bonusTierItems = new ArrayList<>();
  private Location location;
  private double distance;
  private UUID looterUUID;
  private int monsterLevel;
  private LivingEntity entity;
  private String uniqueEntity;

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public UUID getLooterUUID() {
    return looterUUID;
  }

  public void setLooterUUID(UUID looterUUID) {
    this.looterUUID = looterUUID;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public int getMonsterLevel() {
    return monsterLevel;
  }

  public void setMonsterLevel(int monsterLevel) {
    this.monsterLevel = monsterLevel;
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

  public List<ItemRarity> getBonusTierItems() {
    return bonusTierItems;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public LivingEntity getEntity() {
    return entity;
  }

  public void setEntity(LivingEntity entity) {
    this.entity = entity;
  }

  public String getUniqueEntity() {
    return uniqueEntity;
  }

  public void setUniqueEntity(String uniqueEntity) {
    this.uniqueEntity = uniqueEntity;
  }

}
