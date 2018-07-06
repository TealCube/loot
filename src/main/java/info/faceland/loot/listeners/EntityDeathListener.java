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
package info.faceland.loot.listeners;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.data.JunkItemData;
import info.faceland.loot.data.ViolationData;
import info.faceland.loot.events.LootDropEvent;
import info.faceland.loot.math.LootRandom;

import info.faceland.strife.attributes.StrifeAttribute;
import info.faceland.strife.data.AttributedEntity;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.UUID;

public final class EntityDeathListener implements Listener {

  private final LootPlugin plugin;
  private final LootRandom random;
  private final Map<Player, ViolationData> violationMap;

  public EntityDeathListener(LootPlugin plugin) {
    this.plugin = plugin;
    this.random = new LootRandom();
    this.violationMap = new HashMap<>();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDeathEvent(EntityDeathEvent event) {
    if (event instanceof PlayerDeathEvent) {
      return;
    }
    String uniqueEntity = fetchUniqueId(event.getEntity());
    CreatureMod creatureMod;
    if (uniqueEntity == null && !plugin.getSettings().getStringList("config.enabled-worlds", new ArrayList<>())
        .contains(event.getEntity().getWorld().getName())) {
      return;
    }
    creatureMod = plugin.getCreatureModManager().getCreatureMod(event.getEntityType());
    if (creatureMod != null) {
      dropDrops(event, creatureMod);
    }
    if (creatureMod == null && uniqueEntity == null) {
      return;
    }
    if (event.getEntity().getKiller() == null) {
      return;
    }

    Player killer = event.getEntity().getKiller();
    double bonusExpMult = 1D;
    double bonusDropMult = 1D;
    double bonusRarityMult = 1D;
    double penaltyMult = 1D;

    if (!isValidDamageType(event.getEntity().getLastDamageCause().getCause())) {
      penaltyMult *= 0.3D;
    }

    UUID looter = null;
    if (plugin.getAnticheatManager().isTagged(event.getEntity())) {
      looter = plugin.getAnticheatManager().getTag(event.getEntity()).getRandomWeightedLooter();
    }

    handleAntiCheeseViolations(killer, event.getEntity());
    double vl = violationMap.get(killer).getViolationLevel();
    penaltyMult *= Math.max(0.1, Math.min(1, 2.25 - vl * 0.5));

    double distance = event.getEntity().getLocation().distanceSquared(event.getEntity()
        .getWorld().getSpawnLocation());

    AttributedEntity pStats = plugin.getStrifePlugin().getEntityStatCache()
        .getAttributedEntity(killer);
    double dropMultBonus = plugin.getStrifePlugin().getMultiplierManager().getDropMult();

    bonusDropMult += dropMultBonus + pStats.getAttribute(StrifeAttribute.ITEM_DISCOVERY) / 100;
    bonusRarityMult += pStats.getAttribute(StrifeAttribute.ITEM_RARITY) / 100;
    if (killer.hasPotionEffect(PotionEffectType.LUCK)) {
      bonusRarityMult += 0.5;
    }

    int level = 0;
    if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
      level = levelFromString(event.getEntity().getCustomName());
      int playerLevel = event.getEntity().getKiller().getLevel();
      if (playerLevel < 100) {
        int range = plugin.getSettings().getInt("config.range-before-penalty", 15);
        double levelDiff = playerLevel - level;
        if (Math.abs(levelDiff) > range) {
          if (levelDiff > 0) {
            penaltyMult *= Math.max(1 - (levelDiff - range) * 0.05, 0.4);
          } else {
            penaltyMult *= Math.max(1 - (-levelDiff - range) * 0.1, 0.05);
          }
        }
      }
    }

    double exp = 0;
    if (creatureMod != null && creatureMod.getExperienceExpression() != null) {
      exp = creatureMod.getExperienceExpression().setVariable("LEVEL", level).evaluate();
    }
    if (uniqueEntity != null) {
      exp = plugin.getStrifePlugin().getUniqueEntityManager().getLoadedUniquesMap()
          .get(uniqueEntity).getExperience();
    }
    event.setDroppedExp((int) (exp * bonusExpMult * penaltyMult));

    LootDropEvent lootEvent = new LootDropEvent();
    lootEvent.setLocation(event.getEntity().getLocation());
    lootEvent.setLooterUUID(looter);
    lootEvent.setMonsterLevel(level);
    lootEvent.setQualityMultiplier(bonusRarityMult * penaltyMult);
    lootEvent.setQuantityMultiplier(bonusDropMult * penaltyMult);
    lootEvent.setDistance(distance);
    if (uniqueEntity != null) {
      lootEvent.setUniqueEntity(uniqueEntity);
    }
    Bukkit.getPluginManager().callEvent(lootEvent);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDeathMonitor(EntityDeathEvent event) {
    if (plugin.getAnticheatManager().isTagged(event.getEntity())) {
      plugin.getAnticheatManager().removeTag(event.getEntity());
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDeathAutoOrb(EntityDeathEvent event) {
    if (event.getEntity().getKiller() == null) {
      return;
    }
    World w = event.getEntity().getWorld();
    Entity e = w
        .spawnEntity(event.getEntity().getKiller().getEyeLocation(), EntityType.EXPERIENCE_ORB);
    ((ExperienceOrb) e).setExperience(event.getDroppedExp());
    event.setDroppedExp(0);
  }

  private void handleAntiCheeseViolations(Player killer, Entity victim) {
    if (!violationMap.containsKey(killer)) {
      violationMap.put(killer, new ViolationData());
    }
    boolean violation = false;
    if (!isWaterMob(victim)) {
      if (isWater(victim.getLocation()) || isWater(killer.getLocation())) {
        violation = true;
      }
    }
    if (isClimbing(killer.getLocation())) {
      violation = true;
    }
    if (violationMap.get(killer).isEntityTooClose(killer.getLocation(), victim.getLocation())) {
      violation = true;
    }
    ViolationData data = violationMap.get(killer);
    if (violation) {
      data.setViolationLevel(Math.min(4, data.getViolationLevel() + 1));
    } else {
      data.setViolationLevel(Math.max(0, data.getViolationLevel() - 1));
    }
  }

  private void dropDrops(EntityDeathEvent event, CreatureMod mod) {
    if (mod.getJunkMults().isEmpty()) {
      return;
    }
    event.getDrops().clear();
    for (JunkItemData drop : mod.getJunkMults().keySet()) {
      if (mod.getJunkMults().get(drop) >= random.nextDouble()) {
        event.getDrops().add(drop.toItemStack());
      }
    }
  }

  private boolean isWater(Location location) {
    Block b = location.getBlock();
    return b.isLiquid();
  }

  private boolean isClimbing(Location location) {
    Block b = location.getBlock();
    return b.getType() == Material.LADDER || b.getType() == Material.VINE;
  }

  private boolean isValidDamageType(DamageCause cause) {
    return cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.ENTITY_EXPLOSION ||
        cause == DamageCause.PROJECTILE || cause == DamageCause.MAGIC
        || cause == DamageCause.FIRE_TICK || cause == DamageCause.WITHER;
  }

  private boolean isWaterMob(Entity entity) {
    switch (entity.getType()) {
      case GUARDIAN:
        return true;
      case ELDER_GUARDIAN:
        return true;
      case SQUID:
        return true;
      default:
        return false;
    }
  }

  private int levelFromString(String string) {
    return NumberUtils.toInt(CharMatcher.DIGIT.retainFrom(ChatColor.stripColor(string)));
  }

  private boolean isUnique(LivingEntity livingEntity) {
    if (plugin.getStrifePlugin() == null) {
      return false;
    }
    return plugin.getStrifePlugin().getUniqueEntityManager().isUnique(livingEntity);
  }

  private String fetchUniqueId(LivingEntity livingEntity) {
    if (plugin.getStrifePlugin() == null) {
      return null;
    }
    if (!plugin.getStrifePlugin().getUniqueEntityManager().isUnique(livingEntity)) {
      return null;
    }
    return plugin.getStrifePlugin().getUniqueEntityManager().getLiveUniquesMap().get(livingEntity)
        .getUniqueEntity().getId();
  }
}
