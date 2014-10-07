package info.faceland.loot.utils.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class InventoryUtil {

    private InventoryUtil() {
        // do nothing
    }

    public static int firstAtLeast(Inventory inventory, ItemStack itemStack, int amount) {
        if (inventory == null || itemStack == null) {
            return -1;
        }
        HashMap<Integer, ? extends ItemStack> map = inventory.all(itemStack);
        for (Map.Entry<Integer, ? extends ItemStack> entry : map.entrySet()) {
            if (entry.getValue().getAmount() >= amount) {
                return entry.getKey();
            }
        }
        return -1;
    }

}
