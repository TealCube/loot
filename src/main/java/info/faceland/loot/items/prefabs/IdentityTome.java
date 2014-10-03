package info.faceland.loot.items.prefabs;

import info.faceland.hilt.HiltBook;
import org.bukkit.ChatColor;

import java.util.Arrays;

public final class IdentityTome extends HiltBook {

    public IdentityTome() {
        super(TomeType.WRITTEN_BOOK);
        setTitle(ChatColor.DARK_PURPLE + "Identity Tome");
        setPages(Arrays.asList("Much identify", "Very magic", "So book", "Wow"));
    }

}
