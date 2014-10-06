package info.faceland.loot.items.prefabs;

import info.faceland.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;

public final class ProtectionCharm extends HiltItemStack {

    public ProtectionCharm() {
        super(Material.DIAMOND);
        setName(ChatColor.AQUA + "Charm of Protection");
        setLore(Arrays.asList(ChatColor.WHITE + "Prevents one failed upgrade. Consumed on use."));
    }

}
