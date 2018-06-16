package info.faceland.loot.listeners;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class OrbListener implements Listener {

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDeathAutoOrb(EntityDeathEvent event) {
    if (event.getEntity().getKiller() == null) {
      return;
    }
    World w = event.getEntity().getWorld();
    Entity e = w.spawnEntity(event.getEntity().getKiller().getEyeLocation(), EntityType.EXPERIENCE_ORB);
    ((ExperienceOrb) e).setExperience(event.getDroppedExp());
    event.setDroppedExp(0);
  }
}
