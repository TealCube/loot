package info.faceland.loot.data;

import org.bukkit.inventory.ItemStack;

public class BuiltItem {

  private ItemStack stack;
  private int ticksLived;

  public BuiltItem(ItemStack stack, int ticksLived) {
    this.stack = stack;
    this.ticksLived = ticksLived;
  }

  public ItemStack getStack() {
    return stack;
  }

  public int getTicksLived() {
    return ticksLived;
  }
}
