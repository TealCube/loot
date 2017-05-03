package info.faceland.loot.utils.inventory;

import com.tealcube.minecraft.bukkit.TextUtils;
import info.faceland.loot.api.managers.SocketGemManager;
import info.faceland.loot.api.sockets.SocketGem;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class GemUtil {

    private GemUtil() {
        // do nothing
    }

    public static Set<SocketGem> getGems(SocketGemManager manager, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return new HashSet<>();
        }
        Set<SocketGem> gems = new HashSet<>();
        HiltItemStack item = new HiltItemStack(itemStack);
        List<String> lore = item.getLore();
        List<String> strippedLore = new ArrayList<>();
        for (String s : lore) {
            strippedLore.add(ChatColor.stripColor(s));
        }
        for (String key : strippedLore) {
            SocketGem gem = manager.getSocketGem(key);
            if (gem == null) {
                for (SocketGem g : manager.getSocketGems()) {
                    if (!g.isTriggerable()) {
                        continue;
                    }
                    if (key.equals(ChatColor.stripColor(TextUtils.color(
                            g.getTriggerText() != null ? g.getTriggerText() : "")))) {
                        gem = g;
                        break;
                    }
                }
                if (gem == null) {
                    continue;
                }
            }
            gems.add(gem);
        }
        return gems;
    }

}
