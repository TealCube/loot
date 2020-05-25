package info.faceland.loot.data;

import org.bukkit.inventory.ItemStack;

public class LootResponse {

  private final ItemStack itemStack;
  private final boolean valuable;

  public LootResponse(ItemStack itemStack, boolean valuable) {
    this.valuable = valuable;
    this.itemStack = itemStack;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public boolean isValuable() {
    return valuable;
  }
}
