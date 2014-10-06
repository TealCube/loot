package info.faceland.loot.listeners.anticheat;

import info.faceland.loot.LootPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class AnticheatListener implements Listener {

    private final LootPlugin plugin;

    public AnticheatListener(LootPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Monster)) {
            return;
        }
        if (!plugin.getAnticheatManager().isTagged((LivingEntity) event.getEntity())) {
            plugin.getAnticheatManager().tag((LivingEntity) event.getEntity());
        }
    }

}
