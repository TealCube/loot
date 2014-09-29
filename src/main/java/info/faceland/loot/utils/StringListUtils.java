package info.faceland.loot.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public final class StringListUtils {

    private StringListUtils() {
        // do nothing
    }

    public static List<String> stripColor(List<String> l) {
        List<String> list = new ArrayList<>();
        for (String s : l) {
            list.add(ChatColor.stripColor(s));
        }
        return list;
    }

}
