package info.faceland.loot.listeners.sockets;

import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.utils.StringListUtils;
import info.faceland.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public final class SocketsListener implements Listener {

    private LootPlugin plugin;

    public SocketsListener(LootPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCursor() == null
            || event.getCurrentItem().getType() == Material.AIR || event.getCursor().getType() == Material.AIR) {
            return;
        }
        HiltItemStack currentItem = new HiltItemStack(event.getCurrentItem());
        HiltItemStack cursor = new HiltItemStack(event.getCursor());

        if (!cursor.getName().startsWith(ChatColor.GOLD + "Socket Gem -")) {
            return;
        }

        String gemName = ChatColor.stripColor(cursor.getName().replace(ChatColor.GOLD + "Socket Gem - ", ""));
        SocketGem gem = plugin.getSocketGemManager().getSocketGem(gemName);

        if (gem == null) {
            return;
        }

        List<String> lore = currentItem.getLore();
        List<String> strippedLore = StringListUtils.stripColor(lore);
        if (!strippedLore.contains("(Socket)")) {
            return;
        }
        int index = strippedLore.indexOf("(Socket)");

        lore.set(index, ChatColor.GOLD + gem.getName());
        lore.addAll(index + 1, TextUtils.color(gem.getLore()));

        currentItem.setLore(lore);

        event.setCurrentItem(currentItem);
        event.setCursor(null);
        event.setCancelled(true);
        event.setResult(Event.Result.DENY);
    }

}
