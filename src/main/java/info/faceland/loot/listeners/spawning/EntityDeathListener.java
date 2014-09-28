package info.faceland.loot.listeners.spawning;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.math.LootRandom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

public final class EntityDeathListener implements Listener {

    private final LootPlugin plugin;
    private final LootRandom random;

    public EntityDeathListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event instanceof PlayerDeathEvent) {
            return;
        }
        if (event.getEntity().getKiller() == null) {
            return;
        }
        if (!plugin.getSettings().getStringList("config.enabled-worlds", new ArrayList<String>())
                   .contains(event.getEntity().getWorld().getName())) {
            return;
        }
        if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.normal-item", 0D)) {
            // drop a normal random item
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.socket-gem", 0D)) {
            // drop a socket gem
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.enchant-gem", 0D)) {
            // drop an enchant gem
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.upgrade-scroll", 0D)) {
            // drop an upgrade scroll
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.identity-tome", 0D)) {
            // drop an identity tome
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.custom-item", 0D)) {
            // drop a custom item
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.socket-extender", 0D)) {
            // drop a socket extender
        } else {
            // do nothing
        }
    }

}
