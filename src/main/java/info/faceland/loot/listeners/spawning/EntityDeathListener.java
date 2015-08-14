/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.listeners.spawning;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.bukkit.shade.fanciful.FancyMessage;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.events.LootDetermineChanceEvent;
import info.faceland.loot.items.prefabs.IdentityTome;
import info.faceland.loot.items.prefabs.RevealPowder;
import info.faceland.loot.items.prefabs.SocketExtender;
import info.faceland.loot.items.prefabs.UnidentifiedItem;
import info.faceland.loot.items.prefabs.UpgradeScroll;
import info.faceland.loot.math.LootRandom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;

public final class EntityDeathListener implements Listener {

    private final LootPlugin plugin;
    private final LootRandom random;

    public EntityDeathListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER) {
            return;
        }
        event.getEntity().setMetadata("loot.spawnreason", new FixedMetadataValue(plugin, true));
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
        if (!plugin.getSettings().getBoolean("config.neutral-mobs-drop", false)
            && !(event.getEntity() instanceof Monster)) {
            return;
        }
        if (event.getEntity().hasMetadata("loot.spawnreason")) {
            return;
        }
        double cancelChance = 1.0D;
        double xpMult = 1.0D;
        if (isWater(event.getEntity().getLocation())) {
            cancelChance *= 0.5D;
            xpMult *= 0.5D;
        }
        if (isWater(event.getEntity().getKiller().getLocation())) {
            cancelChance *= 0.5D;
            xpMult *= 0.5D;
        }
        double distanceFromWhereTagged = -1D;
        double taggerDistance = -1;
        if (plugin.getAnticheatManager().isTagged(event.getEntity())) {
            distanceFromWhereTagged = plugin.getAnticheatManager().getTag(
                event.getEntity()).getEntityLocation().distanceSquared(event.getEntity().getLocation());
            if (plugin.getAnticheatManager().getTag(event.getEntity()).getTaggerLocation(event.getEntity().getKiller
                ().getUniqueId()) != null) {
                taggerDistance = plugin.getAnticheatManager().getTag(event.getEntity())
                    .getTaggerLocation(event.getEntity().getKiller
                        ().getUniqueId())
                    .distanceSquared(event.getEntity().getKiller().getLocation());
            }
        }
        if (distanceFromWhereTagged >= 0 && distanceFromWhereTagged <= 3) {
            cancelChance *= 0.4D;
            xpMult *= 0.2D;
        }
        if (taggerDistance >= 0 && taggerDistance <= 3) {
            cancelChance *= 0.4D;
            xpMult *= 0.2D;
        }
        event.setDroppedExp(Math.max(3, (int) (xpMult * event.getDroppedExp())));
        if (random.nextDouble() >= cancelChance) {
            return;
        }
        LootDetermineChanceEvent chanceEvent = new LootDetermineChanceEvent(event.getEntity(), event.getEntity()
            .getKiller(), 1.0D);
        Bukkit.getPluginManager().callEvent(chanceEvent);
        double chance = chanceEvent.getChance();
        CreatureMod mod = plugin.getCreatureModManager().getCreatureMod(event.getEntity().getType());
        String mobName = event.getEntity().getCustomName();
        double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity()
                                                                                     .getWorld().getSpawnLocation());
        double dropBonus = 1.0;
        // NOTE: Drop bonus should not be applied to Unidentified or Custom items!
        if (mobName != null) {
            if (mobName.endsWith("[M]")) {
                dropBonus = 2.5;
            } else if (mobName.endsWith("[R]")) {
                dropBonus = 5.0;
            } else if (mobName.endsWith("[E]")) {
                dropBonus = 7.5;
            } else if (mobName.endsWith("[L]")) {
                dropBonus = 10.0;
            }
        }
        if (random.nextDouble() / chance < dropBonus * plugin.getSettings().getDouble("config.drops.normal-drop", 0D)) {
            SocketGem sg = plugin.getSocketGemManager().getRandomSocketGem(true, distanceSquared,
                                                                           mod != null ? mod.getSocketGemMults() :
                                                                           new HashMap<SocketGem, Double>());
            HiltItemStack his = sg.toItemStack(1);
            event.getDrops().add(his);
            if (sg.isBroadcast()) {
                broadcast(event, his);
            }
        }
        if (random.nextDouble() / chance < dropBonus * plugin.getSettings().getDouble("config.drops.socket-gem", 0D)) {
            SocketGem sg = plugin.getSocketGemManager().getRandomSocketGem(true, distanceSquared, mod != null ?
                                mod.getSocketGemMults() : new HashMap<SocketGem, Double>());
            HiltItemStack his = sg.toItemStack(1);
            event.getDrops().add(his);
            if (sg.isBroadcast()) {
                broadcast(event, his);
            }
        }
        if (random.nextDouble() / chance < dropBonus * plugin.getSettings().getDouble("config.drops.enchant-gem", 0D)) {
            EnchantmentTome es = plugin.getEnchantmentStoneManager().getRandomEnchantmentStone(true, distanceSquared,
                mod != null ? mod.getEnchantmentStoneMults() : new HashMap<EnchantmentTome, Double>());
            HiltItemStack his = es.toItemStack(1);
            event.getDrops().add(his);
            if (es.isBroadcast()) {
                broadcast(event, his);
            }
        }
        if (random.nextDouble() / chance <  dropBonus * plugin.getSettings().getDouble("config.drops.upgrade-scroll", 0D)) {
            event.getDrops().add(new UpgradeScroll(UpgradeScroll.ScrollType.random(true)));
        }
        if (random.nextDouble() / chance < dropBonus * plugin.getSettings().getDouble("config.drops.identity-tome", 0D)) {
            event.getDrops().add(new IdentityTome());
        }
        if (random.nextDouble() / chance < dropBonus * plugin.getSettings().getDouble("config.drops.reveal-powder", 0D)) {
            event.getDrops().add(new RevealPowder());
        }
        if (random.nextDouble() / chance < plugin.getSettings().getDouble("config.drops.custom-item", 0D)) {
            CustomItem ci = plugin.getCustomItemManager().getRandomCustomItem(true, distanceSquared, mod != null ?
                                mod.getCustomItemMults() : new HashMap<CustomItem, Double>());
            HiltItemStack his = ci.toItemStack(1);
            event.getDrops().add(his);
            if (ci.isBroadcast()) {
                broadcast(event, his);
            }
        }
        if (random.nextDouble() / chance < dropBonus * plugin.getSettings().getDouble("config.drops.socket-extender", 0D)) {
            event.getDrops().add(new SocketExtender());
        }
        if (random.nextDouble() / chance < plugin.getSettings().getDouble("config.drops.unidentified-item", 0D)) {
            Tier t = plugin.getTierManager().getRandomTier(true, distanceSquared);
            Material[] array = t.getAllowedMaterials().toArray(new Material[t.getAllowedMaterials().size()]);
            Material m = array[random.nextInt(array.length)];
            event.getDrops().add(new UnidentifiedItem(m));
        }
    }

    private void broadcast(EntityDeathEvent event, HiltItemStack his) {
        FancyMessage message = new FancyMessage("");
        String mess = plugin.getSettings().getString("language.broadcast.found-item", "");
        String[] split = mess.split(" ");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            String str = TextUtils.color(s);
            if (str.contains("%player%")) {
                message.then(str.replace("%player%", event.getEntity().getKiller().getDisplayName()));
            } else if (str.contains("%item%")) {
                message.then(str.replace("%item%", his.getName())).itemTooltip(his);
            } else {
                message.then(str);
            }
            if (i != split.length - 1) {
                message.then(" ");
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            message.send(p);
        }
    }

    private boolean isWater(Location location) {
        Block b = location.getBlock();
        return b.isLiquid();
    }

    private boolean isBlockWithinRadius(Material material, Location location, int radius) {
        int minX = location.getBlockX() - radius;
        int maxX = location.getBlockX() + radius;
        int minY = location.getBlockY() - radius;
        int maxY = location.getBlockY() + radius;
        int minZ = location.getBlockZ() - radius;
        int maxZ = location.getBlockZ() + radius;
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeathMonitor(EntityDeathEvent event) {
        if (plugin.getAnticheatManager().isTagged(event.getEntity())) {
            plugin.getAnticheatManager().removeTag(event.getEntity());
        }
    }

}
