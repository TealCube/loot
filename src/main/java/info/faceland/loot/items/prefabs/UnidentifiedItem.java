package info.faceland.loot.items.prefabs;

import info.faceland.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;

public final class UnidentifiedItem extends HiltItemStack {

    public UnidentifiedItem(Material material) {
        super(material);
        setName(ChatColor.WHITE + "Unidentified Item");
        setLore(Arrays.asList(ChatColor.GRAY + "Drop an " + ChatColor.DARK_PURPLE + "Identity Tome" + ChatColor.GRAY
                              + "on", ChatColor.GRAY + "this item to identify it."));
    }

}
