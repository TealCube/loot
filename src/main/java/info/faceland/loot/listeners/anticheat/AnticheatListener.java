/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package info.faceland.loot.listeners.anticheat;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.anticheat.AnticheatTag;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

public final class AnticheatListener implements Listener {

  private final LootPlugin plugin;
  private static final long MILLIS_PER_SEC = 1000;

  public AnticheatListener(LootPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
      event.getEntity().setMetadata("SPAWNED", new FixedMetadataValue(plugin, true));
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof LivingEntity)) {
      return;
    }
    LivingEntity victim = (LivingEntity) event.getEntity();
    if (plugin.getCreatureModManager().getCreatureMod(victim.getType()) == null) {
      if (plugin.getStrifePlugin() == null) {
        return;
      }
      if (!plugin.getStrifePlugin().getUniqueEntityManager().isUnique(victim)) {
        return;
      }
    }
    LivingEntity attacker = null;
    if (event.getDamager() instanceof Player) {
      attacker = (Player) event.getDamager();
    } else if (event.getDamager() instanceof Projectile) {
      if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
        attacker = ((Player) ((Projectile) event.getDamager()).getShooter()).getPlayer();
      }
    }
    if (attacker == null) {
      return;
    }
    if (!plugin.getAnticheatManager().isTagged(victim)) {
      plugin.getAnticheatManager().addTag(victim);
    }
    AnticheatTag tag = plugin.getAnticheatManager().getTag(victim);
    if (tag.getTaggerLocation(attacker.getUniqueId()) == null) {
      tag.setTaggerLocation(attacker.getUniqueId(), attacker.getLocation());
    } else {
      tag.setTaggerLocation(attacker.getUniqueId(), tag.getTaggerLocation(attacker.getUniqueId()));
    }
    tag.setTaggerDamage(attacker.getUniqueId(), event.getFinalDamage());
    plugin.getAnticheatManager().pushTag(victim, tag);
  }

  // Loot protection function. Return out of it before the end to allow an item to be picked up!
  // Makes it so only the owner of a drop can pick it up.
  @EventHandler(priority = EventPriority.LOWEST)
  public void onItemPickupEvent(EntityPickupItemEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!event.getItem().hasMetadata("loot-owner")) {
      return;
    }
    if (event.getItem().getMetadata("loot-owner").get(0) == null) {
      return;
    }
    // Fetching item lore that should have been applied in EntityDeathListener
    String owner = event.getItem().getMetadata("loot-owner").get(0).asString();
    long time = event.getItem().getMetadata("loot-time").get(0).asLong();

    // If the event player's UUID is the same as the owner UUID on the item, allow the pickup
    if (event.getEntity().getUniqueId().toString().equals(owner)) {
      return;
    }

    // If loot-protect-time seconds have passed, allow the item to be picked up!
    if ((System.currentTimeMillis() - time) >= plugin.getSettings().getInt("config.loot-protect-time", 10) *
        MILLIS_PER_SEC) {
      return;
    }
    event.setCancelled(true);
  }
}
