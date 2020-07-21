package info.faceland.loot.events;

import info.faceland.loot.api.events.LootEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LootCraftEvent extends LootEvent {

  private final ItemStack targetItem;
  private final Player player;

  public LootCraftEvent(Player player, ItemStack targetItem) {
    this.player = player;
    this.targetItem = targetItem;
  }

  public ItemStack getTargetItem() {
    return targetItem;
  }

  public Player getPlayer() {
    return player;
  }

}
