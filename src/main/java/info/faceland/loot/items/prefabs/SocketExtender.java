package info.faceland.loot.items.prefabs;

import info.faceland.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;

public final class SocketExtender extends HiltItemStack {

    public SocketExtender() {
        super(Material.NETHER_STAR);
        setAmount(1);
        setName(ChatColor.DARK_AQUA + "Socket Extender");
        setLore(Arrays.asList(ChatColor.GRAY + "Drag this item onto an item with a " + ChatColor.DARK_AQUA + "(+)",
                              ChatColor.GRAY + "to give that item an additional socket!"));
    }

}
