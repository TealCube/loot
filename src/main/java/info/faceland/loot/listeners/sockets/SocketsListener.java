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
package info.faceland.loot.listeners.sockets;

import com.kill3rtaco.tacoserialization.SingleItemSerialization;
import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.hilt.HiltItemStack;
import com.tealcube.minecraft.bukkit.shade.google.common.base.Predicates;
import com.tealcube.minecraft.bukkit.shade.google.common.collect.Iterables;
import com.tealcube.minecraft.bukkit.shade.google.common.collect.Lists;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.data.GemCacheData;
import info.faceland.loot.api.math.Vec3;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.*;

public final class SocketsListener implements Listener {

    private LootPlugin plugin;
    private final Map<UUID, List<String>> gems;

    public SocketsListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.gems = new HashMap<>();
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

        if (defender instanceof Player) {
            Set<SocketEffect> defenderEffects = new HashSet<>();
            Player defenderP = (Player) defender;
            GemCacheData defenderData = plugin.getGemCacheManager().getGemCacheData(defenderP.getUniqueId());
            defenderEffects.addAll(defenderData.getArmorCache(SocketGem.GemType.WHEN_HIT));
            applyEffects(defenderEffects, defender, attacker);
        }

        if (!(attacker instanceof Player)) {
            if (attacker instanceof Projectile) {
                if (!(((Projectile) attacker).getShooter() instanceof Player)) {
                    return;
                }
                attacker = (Player) ((Projectile) attacker).getShooter();
            } else {
                return;
            }
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

        GemCacheData data = plugin.getGemCacheManager().getGemCacheData(attackerP.getUniqueId());
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

        GemCacheData killingData = plugin.getGemCacheManager().getGemCacheData(killingPlayer.getUniqueId());
        killingEffects.addAll(killingData.getWeaponCache(SocketGem.GemType.ON_KILL));

        applyEffects(killingEffects, killingPlayer, dyingEntity);
    }

    private void applyEffects(Set<SocketEffect> effects, Entity applier, Entity recipient) {
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!ChatColor.stripColor(event.getInventory().getName()).equals("Socket Gem Combiner")) {
            return;
        }
        if (event.getInventory().getSize() > 9) {
            return;
        }
        if (event.isShiftClick()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getHotbarButton() != -1) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        HiltItemStack his = new HiltItemStack(event.getCurrentItem());
        if (!his.getName().startsWith(ChatColor.GOLD + "Socket Gem - ")) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
        if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
            return;
        }
        his = new HiltItemStack(event.getCursor());
        if (!his.getName().startsWith(ChatColor.GOLD + "Socket Gem - ")) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof Chest)) {
            return;
        }
        Vec3 loc = new Vec3(((Chest) holder).getWorld().getName(), ((Chest) holder).getX(), ((Chest) holder).getY(),
                ((Chest) holder).getZ());
        if (!plugin.getChestManager().getChestLocations().contains(loc)) {
            return;
        }
        event.setCancelled(true);
        Inventory toShow = Bukkit.createInventory(null, 9, "Socket Gem Combiner");
        toShow.setMaxStackSize(1);
        List<String> toAdd = new ArrayList<>();
        toAdd.addAll(gems.containsKey(event.getPlayer().getUniqueId()) ? gems.get(event.getPlayer().getUniqueId()) : new ArrayList<String>());
        gems.remove(event.getPlayer().getUniqueId());
        for (String s : toAdd) {
            toShow.addItem(SingleItemSerialization.getItem(s));
        }
        event.getPlayer().openInventory(toShow);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!ChatColor.stripColor(event.getInventory().getName()).equals("Socket Gem Combiner")) {
            return;
        }
        if (event.getInventory().getSize() > 9) {
            return;
        }
        List<ItemStack> newResults = new ArrayList<>();
        List<ItemStack> contents = Lists.newArrayList(Iterables.filter(Arrays.asList(event.getInventory().getContents()),
                Predicates.notNull()));
        for (ItemStack content : contents) {
            HiltItemStack his = new HiltItemStack(content);
            if (!his.getName().startsWith(ChatColor.GOLD + "Socket Gem - ")) {
                MessageUtils.sendMessage(event.getPlayer(), "<green>All items must be Socket Gems in order to transmute.");
                return;
            }
        }
        while (contents.size() >= 4) {
            contents = contents.subList(4, contents.size());
            newResults.add(plugin.getSocketGemManager().getRandomSocketGemByBonus().toItemStack(1));
        }
        newResults.addAll(contents);
        List<String> toAdd = new ArrayList<>();
        for (ItemStack is : newResults) {
            toAdd.add(SingleItemSerialization.serializeItemAsString(is));
        }
        if (toAdd.size() > 0) {
            HumanEntity c = event.getPlayer();
            c.getWorld().playEffect(c.getLocation().add(0, 1, 0), Effect.SPELL, 0);
            c.getWorld().playSound(c.getLocation().add(0, 1, 0), Sound.ENTITY_ENDERMEN_SCREAM, 1.0f, 1.0f);
            MessageUtils.sendMessage(event.getPlayer(), "<green>Open the chest again to get your new Socket Gems!");
        }
        gems.put(event.getPlayer().getUniqueId(), toAdd);
    }

    private Set<SocketGem> getGems(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return new HashSet<>();
        }
        Set<SocketGem> gems = new HashSet<>();
        HiltItemStack item = new HiltItemStack(itemStack);
        List<String> lore = item.getLore();
        List<String> strippedLore = stripColor(lore);
        for (String key : strippedLore) {
            SocketGem gem = plugin.getSocketGemManager().getSocketGem(key);
            if (gem == null) {
                for (SocketGem g : plugin.getSocketGemManager().getSocketGems()) {
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

    private List<String> stripColor(List<String> strings) {
        List<String> ret = new ArrayList<>();
        for (String s : strings) {
            ret.add(ChatColor.stripColor(s));
        }
        return ret;
    }

}
