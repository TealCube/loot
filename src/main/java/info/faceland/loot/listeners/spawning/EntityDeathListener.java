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
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.fanciful.FancyMessage;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import com.tealcube.minecraft.bukkit.shade.google.common.collect.Sets;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.ItemGenerationReason;
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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
        double dropBonus = 1.0D;
        double dropPenalty = 1.0D;
        double xpMult = 1.0D;
        if (isWater(event.getEntity().getLocation())) {
            dropPenalty *= 0.4D;
            xpMult *= 0.4D;
        }
        if (isWater(event.getEntity().getKiller().getLocation())) {
            dropPenalty *= 0.7D;
            xpMult *= 0.7D;
        }
        double distanceFromWhereTagged = -1D;
        double taggerDistance = -1;
        UUID bestTaggerLmao = null;
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
            bestTaggerLmao = plugin.getAnticheatManager().getTag(event.getEntity()).getHighestDamageTagger();
        }
        if (event.getEntity().getKiller().isSneaking()) {
            dropPenalty *= 0.5D;
            xpMult *= 0.5D;
        }
        if (distanceFromWhereTagged >= 0 && distanceFromWhereTagged <= 2.5) {
            dropPenalty *= 0.8D;
            xpMult *= 0.7D;
            if (distanceFromWhereTagged <= 1.2) {
                dropPenalty *= 0.5D;
                xpMult *= 0.5D;
            }
        }
        if (taggerDistance >= 0 && taggerDistance <= 2.0 && event.getEntity().getKiller().getItemInHand().getType() !=
                Material.BOW) {
            dropPenalty *= 0.7D;
            xpMult *= 0.7D;
        }
        if (plugin.getSettings().getBoolean("config.scale-with-level-diff", false)) {
            if (event.getEntity().getKiller() instanceof Player) {
                if (event.getEntity().getCustomName() != null) {
                    int mobLevel = NumberUtils.toInt(CharMatcher.DIGIT.retainFrom(ChatColor.stripColor(event.getEntity()
                            .getCustomName())));
                    int playerLevel = event.getEntity().getKiller().getLevel();
                    int range = plugin.getSettings().getInt("config.range-before-penalty", 15);
                    double levelDiff = Math.abs(mobLevel - playerLevel);
                    if (levelDiff > range) {
                        dropPenalty *= Math.max(1 - ((levelDiff - range) / 10), 0);
                        xpMult *= Math.max(1 - ((levelDiff - range) / 20), 0.1D);
                    }
                }
            }
        }
        event.setDroppedExp(Math.max(3, (int) (xpMult * event.getDroppedExp())));

        CreatureMod mod = plugin.getCreatureModManager().getCreatureMod(event.getEntity().getType());
        String mobName = event.getEntity().getCustomName();
        double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity()
                                                                                     .getWorld().getSpawnLocation());
        // Adding to drop rate based on rank of Elite Mobs
        if (mobName != null) {
            if (mobName.startsWith(ChatColor.BLUE + "Magic")) {
                dropBonus = 3.0D;
            } else if (mobName.startsWith(ChatColor.DARK_PURPLE + "Rare")) {
                dropBonus = 7.0D;
            } else if (mobName.startsWith(ChatColor.RED + "Epic")) {
                dropBonus = 12.0D;
            } else if (mobName.startsWith(ChatColor.GOLD + "Legendary")) {
                dropBonus = 18.0D;
            }
        }
        dropBonus *= dropPenalty;

        // Adding to bonus drop based on Strife stat Item Discovery. It is added on, not multiplied!
        LootDetermineChanceEvent chanceEvent = new LootDetermineChanceEvent(event.getEntity(), event.getEntity()
                .getKiller(), 0.0D);
        Bukkit.getPluginManager().callEvent(chanceEvent);
        dropBonus += chanceEvent.getChance() * dropPenalty;

        World w = event.getEntity().getWorld();

        if (random.nextDouble() < dropBonus * plugin.getSettings().getDouble("config.drops.normal-drop", 0D)) {
            Tier t = plugin.getTierManager().getRandomTier(true, distanceSquared, mod != null ? mod.getTierMults() :
            new HashMap<Tier, Double>());
            HiltItemStack his = plugin.getNewItemBuilder().withTier(t).withItemGenerationReason(ItemGenerationReason.MONSTER).build();
            int upgradeBonus = 0;
            double upgradeChance = plugin.getSettings().getDouble("config.random-upgrade-chance", 0.1);
            while (random.nextDouble() <= upgradeChance && upgradeBonus < 9) {
                upgradeBonus++;
            }
            if (upgradeBonus > 0) {
                his = upgradeItem(his, upgradeBonus);
            }

            if (bestTaggerLmao != null) {
                w.dropItemNaturally(event.getEntity().getLocation(), his).setMetadata("Loot", new FixedMetadataValue
                        (plugin, bestTaggerLmao.toString()));
            } else {
                w.dropItemNaturally(event.getEntity().getLocation(), his);
            }
            if (t.isBroadcast() || upgradeBonus > 6) {
                broadcast(event, his);
            }
        }
        if (random.nextDouble() < dropBonus * plugin.getSettings().getDouble("config.drops.socket-gem", 0D)) {
            SocketGem sg = plugin.getSocketGemManager().getRandomSocketGem(true, distanceSquared, mod != null ?
                                mod.getSocketGemMults() : new HashMap<SocketGem, Double>());
            HiltItemStack his = sg.toItemStack(1);
            if (bestTaggerLmao != null) {
                w.dropItemNaturally(event.getEntity().getLocation(), his).setMetadata("Loot", new FixedMetadataValue
                        (plugin, bestTaggerLmao.toString()));
            } else {
                w.dropItemNaturally(event.getEntity().getLocation(), his);
            }
            if (sg.isBroadcast()) {
                broadcast(event, his);
            }
        }
        if (random.nextDouble() < dropBonus * plugin.getSettings().getDouble("config.drops.enchant-gem", 0D)) {
            EnchantmentTome es = plugin.getEnchantmentStoneManager().getRandomEnchantmentStone(true, distanceSquared,
                mod != null ? mod.getEnchantmentStoneMults() : new HashMap<EnchantmentTome, Double>());
            HiltItemStack his = es.toItemStack(1);
            if (bestTaggerLmao != null) {
                w.dropItemNaturally(event.getEntity().getLocation(), his).setMetadata("Loot", new FixedMetadataValue
                        (plugin, bestTaggerLmao.toString()));
            } else {
                w.dropItemNaturally(event.getEntity().getLocation(), his);
            }
            if (es.isBroadcast()) {
                broadcast(event, his);
            }
        }
        if (random.nextDouble() <  dropBonus * plugin.getSettings().getDouble("config.drops.upgrade-scroll", 0D)) {
            HiltItemStack his = new UpgradeScroll(UpgradeScroll.ScrollType.random(true));
            if (bestTaggerLmao != null) {
                w.dropItemNaturally(event.getEntity().getLocation(), his).setMetadata("Loot", new FixedMetadataValue
                        (plugin, bestTaggerLmao.toString()));
            } else {
                w.dropItemNaturally(event.getEntity().getLocation(), his);
            }
        }
        if (random.nextDouble() < dropBonus * plugin.getSettings().getDouble("config.drops.identity-tome", 0D)) {
            HiltItemStack his = new IdentityTome();
            if (bestTaggerLmao != null) {
                w.dropItemNaturally(event.getEntity().getLocation(), his).setMetadata("Loot", new FixedMetadataValue
                        (plugin, bestTaggerLmao.toString()));
            } else {
                w.dropItemNaturally(event.getEntity().getLocation(), his);
            }
        }
        if (random.nextDouble() < dropBonus * plugin.getSettings().getDouble("config.drops.reveal-powder", 0D)) {
            HiltItemStack his = new RevealPowder();
            if (bestTaggerLmao != null) {
                w.dropItemNaturally(event.getEntity().getLocation(), his).setMetadata("Loot", new FixedMetadataValue
                        (plugin, bestTaggerLmao.toString()));
            } else {
                w.dropItemNaturally(event.getEntity().getLocation(), his);
            }
        }
        if (random.nextDouble() < dropBonus * plugin.getSettings().getDouble("config.drops.custom-item", 0D)) {
            CustomItem ci = plugin.getCustomItemManager().getRandomCustomItem(true, distanceSquared, mod != null ?
                                mod.getCustomItemMults() : new HashMap<CustomItem, Double>());
            HiltItemStack his = ci.toItemStack(1);
            int upgradeBonus = 0;
            double upgradeChance = plugin.getSettings().getDouble("config.random-upgrade-chance", 0.1);
            while (random.nextDouble() <= upgradeChance && upgradeBonus < 9) {
                upgradeBonus++;
            }
            if (upgradeBonus > 0) {
                his = upgradeItem(his, upgradeBonus);
            }
            if (bestTaggerLmao != null) {
                w.dropItemNaturally(event.getEntity().getLocation(), his).setMetadata("Loot", new FixedMetadataValue
                        (plugin, bestTaggerLmao.toString()));
            } else {
                w.dropItemNaturally(event.getEntity().getLocation(), his);
            }
            if (ci.isBroadcast() || upgradeBonus > 6) {
                broadcast(event, his);
            }
        }
        if (random.nextDouble() < dropBonus * plugin.getSettings().getDouble("config.drops.socket-extender", 0D)) {
            HiltItemStack his = new SocketExtender();
            if (bestTaggerLmao != null) {
                w.dropItemNaturally(event.getEntity().getLocation(), his).setMetadata("Loot", new FixedMetadataValue
                        (plugin, bestTaggerLmao.toString()));
            } else {
                w.dropItemNaturally(event.getEntity().getLocation(), his);
            }
            broadcast(event, his);
        }
        // NOTE: Drop bonus should not be applied to Unidentified Items!
        if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.unidentified-item", 0D)) {
            Tier t = plugin.getTierManager().getRandomTier(true, distanceSquared);
            Material[] array = t.getAllowedMaterials().toArray(new Material[t.getAllowedMaterials().size()]);
            Material m = array[random.nextInt(array.length)];
            HiltItemStack his = new UnidentifiedItem(m);
            if (bestTaggerLmao != null) {
                w.dropItemNaturally(event.getEntity().getLocation(), his).setMetadata("Loot", new FixedMetadataValue
                        (plugin, bestTaggerLmao.toString()));
            } else {
                w.dropItemNaturally(event.getEntity().getLocation(), his);
            }
        }
    }

    public HiltItemStack upgradeItem(HiltItemStack his, int upgradeBonus) {
        boolean succeed = false;
        List<String> lore = his.getLore();
        for (String s : lore) {
            if (ChatColor.stripColor(s).startsWith("+")) {
                succeed = true;
                break;
            }
        }
        if (!succeed) {
            return his;
        }
        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i);
            String ss = ChatColor.stripColor(s);
            if (!ss.startsWith("+")) {
                continue;
            }
            String loreLev = CharMatcher.DIGIT.or(CharMatcher.is('-')).retainFrom(ss);
            int loreLevel = NumberUtils.toInt(loreLev);
            lore.set(i, s.replace("+" + loreLevel, "+" + (loreLevel + upgradeBonus)));
            String name = getFirstColor(his.getName()) + ("+" + upgradeBonus) + " " + his.getName();
            his.setName(name);
            break;
        }
        his.setLore(lore);
        if (upgradeBonus > 6) {
            his.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            his.setItemFlags(Sets.newHashSet(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag
                    .HIDE_UNBREAKABLE));
        }
        return his;
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

    private ChatColor getFirstColor(String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            if (!s.substring(i, i + 1).equals(ChatColor.COLOR_CHAR + "")) {
                continue;
            }
            ChatColor c = ChatColor.getByChar(s.substring(i + 1, i + 2));
            if (c != null) {
                return c;
            }
        }
        return ChatColor.RESET;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeathMonitor(EntityDeathEvent event) {
        if (plugin.getAnticheatManager().isTagged(event.getEntity())) {
            plugin.getAnticheatManager().removeTag(event.getEntity());
        }
    }

}
