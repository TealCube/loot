package info.faceland.loot.data;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class JunkItemData {

  private final Material material;
  private final int max;
  private final int min;

  public JunkItemData(Material material, int max, int min) {
    this.material = material;
    this.max = max;
    this.min = min;
  }

  public Material getMaterial() {
    return material;
  }

  public int getMax() {
    return max;
  }

  public int getMin() {
    return min;
  }

  public ItemStack toItemStack() {
    return new ItemStack(material, ThreadLocalRandom.current().nextInt(min, max + 1));
  }

}
