package info.faceland.loot.events;

import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LootDeconstructEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  private DeconstructType deconstructType;
  private HiltItemStack cursorItem;
  private HiltItemStack targetItem;
  private Player player;
  private boolean cancelled;

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public DeconstructType getDeconstructType() {
    return deconstructType;
  }

  public void setDeconstructType(
      DeconstructType deconstructType) {
    this.deconstructType = deconstructType;
  }

  public HiltItemStack getCursorItem() {
    return cursorItem;
  }

  public void setCursorItem(HiltItemStack cursorItem) {
    this.cursorItem = cursorItem;
  }

  public HiltItemStack getTargetItem() {
    return targetItem;
  }

  public void setTargetItem(HiltItemStack targetItem) {
    this.targetItem = targetItem;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public boolean isCancelled() {
    return cancelled;
  }

  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  public enum DeconstructType {
    CRAFTING,
    ENCHANTING
  }
}
