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
package info.faceland.loot.listeners.sockets;

import info.faceland.loot.api.data.GemCacheData;
import info.faceland.loot.api.managers.GemCacheManager;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.SocketGem.GemType;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.*;

public final class SocketsListener implements Listener {

  private GemCacheManager gemCacheManager;

  public SocketsListener(GemCacheManager gemCacheManager) {
    this.gemCacheManager = gemCacheManager;
  }

  //@EventHandler(priority = EventPriority.MONITOR)
  //public void onProjectileLaunch(ProjectileLaunchEvent event) {
  //    if (event.isCancelled() || !(event.getEntity().getShooter() instanceof Player)) {
  //        return;
  //    }
  //    Set<SocketGem> gems = getGems(((Player) event.getEntity().getShooter()).getItemInHand());
  //    List<String> names = new ArrayList<>();
  //    for (SocketGem gem : gems) {
  //        names.add(gem.getName());
  //    }
  //    event.getEntity().setMetadata("loot.gems", new FixedMetadataValue(plugin, names.toString()));
  //}

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.isCancelled()) {
      return;
    }
    Entity attacker = event.getDamager();
    Entity defender = event.getEntity();

    if (attacker instanceof Projectile) {
      if (((Projectile) attacker).getShooter() instanceof LivingEntity) {
        attacker = (LivingEntity) ((Projectile) attacker).getShooter();
      }
    }

    if (defender instanceof Player) {
      Set<SocketEffect> defenderEffects = new HashSet<>();
      Player defenderP = (Player) defender;
      GemCacheData defenderData = gemCacheManager.getGemCacheData(defenderP.getUniqueId());
      defenderEffects.addAll(defenderData.getArmorCache(SocketGem.GemType.WHEN_HIT));
      applyEffects(defenderEffects, defender, attacker);
    }

    if (!(attacker instanceof Player)) {
      return;
    }

    Set<SocketEffect> attackerEffects = new HashSet<>();
    Player attackerP = (Player) attacker;

    //if (event.getDamager() instanceof Projectile) {
    //    if (event.getDamager().hasMetadata("loot.gems")) {
    //        for (MetadataValue val : event.getDamager().getMetadata("loot.gems")) {
    //            if (!val.getOwningPlugin().equals(plugin)) {
    //                continue;
    //            }
    //            String blah = val.asString().replace("[", "").replace("]", "");
    //            for (String s : blah.split(",")) {
    //                SocketGem gem = plugin.getSocketGemManager().getSocketGem(s.trim());
    //                if (gem == null) {
    //                    continue;
    //                }
    //                attackerEffects.addAll(gem.getSocketEffects());
    //            }
    //        }
    //    }
    //} else {
    //    GemCacheData data = plugin.getGemCacheManager().getGemCacheData(attackerP.getUniqueId());
    //    data.updateWeaponCache();
    //    attackerEffects.addAll(data.getWeaponCache(SocketGem.GemType.ON_HIT));
    //}

    GemCacheData data = gemCacheManager.getGemCacheData(attackerP.getUniqueId());
    data.updateWeaponCache();
    attackerEffects.addAll(data.getWeaponCache(SocketGem.GemType.ON_HIT));

    applyEffects(attackerEffects, attacker, defender);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDeath(EntityDeathEvent event) {
    LivingEntity dyingEntity = event.getEntity();
    Player killingPlayer = dyingEntity.getKiller();
    if (killingPlayer == null) {
      return;
    }
    Set<SocketEffect> killingEffects = new HashSet<>();

    GemCacheData killingData = gemCacheManager.getGemCacheData(killingPlayer.getUniqueId());
    killingEffects.addAll(killingData.getWeaponCache(SocketGem.GemType.ON_KILL));

    applyEffects(killingEffects, killingPlayer, dyingEntity);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerSneak(PlayerToggleSneakEvent event) {
    if (!event.isSneaking()) {
      return;
    }
    Set<SocketEffect> sneakingEffects = new HashSet<>();

    GemCacheData killingData = gemCacheManager.getGemCacheData(event.getPlayer().getUniqueId());
    sneakingEffects.addAll(killingData.getArmorCache(GemType.ON_SNEAK));

    applyEffects(sneakingEffects, event.getPlayer(), null);
  }

  public static void applyEffects(Set<SocketEffect> effects, Entity applier, Entity recipient) {
    for (SocketEffect effect : effects) {
      switch (effect.getTarget()) {
        case SELF:
          if (applier instanceof LivingEntity) {
            effect.apply((LivingEntity) applier);
          }
          break;
        case OTHER:
          if (recipient instanceof LivingEntity) {
            effect.apply((LivingEntity) recipient);
          }
          break;
        case AREA:
          for (Entity e : recipient
              .getNearbyEntities(effect.getRadius(), effect.getRadius(), effect.getRadius())) {
            if (e instanceof LivingEntity) {
              effect.apply((LivingEntity) e);
            }
          }
          if (recipient instanceof LivingEntity) {
            effect.apply((LivingEntity) recipient);
          }
          break;
        default:
          break;
      }
    }
  }
}
