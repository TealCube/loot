package info.faceland.loot.listeners.crafting;

import info.faceland.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public final class CraftingListener implements Listener {

    public CraftingListener() {
        // do nothing
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCraftItemEvent(CraftItemEvent event) {
        for (ItemStack is : event.getInventory().getMatrix()) {
            if (is == null || is.getType() == Material.AIR) {
                continue;
            }
            HiltItemStack his = new HiltItemStack(is);
            if (his.getName().equals(ChatColor.DARK_AQUA + "Socket Extender") ||
                his.getName().startsWith(ChatColor.BLUE + "Enchantment Tome - ") ||
                his.getName().startsWith(ChatColor.GOLD + "Socket Gem -") ||
                his.getName().equals(ChatColor.AQUA + "Charm of Protection")) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
