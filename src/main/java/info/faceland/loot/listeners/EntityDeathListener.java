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
package info.faceland.loot.listeners;

import com.tealcube.minecraft.bukkit.TextUtils;
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
import info.faceland.loot.data.ItemRarity;
import info.faceland.loot.items.prefabs.IdentityTome;
import info.faceland.loot.items.prefabs.SocketExtender;
import info.faceland.loot.items.prefabs.UnidentifiedItem;
import info.faceland.loot.items.prefabs.UpgradeScroll;
import info.faceland.loot.items.prefabs.UpgradeScroll.ScrollType;
import info.faceland.loot.math.LootRandom;

import info.faceland.loot.utils.inventory.MaterialUtil;
import info.faceland.strife.attributes.StrifeAttribute;
import info.faceland.strife.data.AttributedEntity;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class EntityDeathListener implements Listener {

    private final LootPlugin plugin;
    private final LootRandom random;

    public EntityDeathListener(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom();
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
        if (!plugin.getSettings().getBoolean("config.neutral-mobs-drop", false) &&
            !(event.getEntity() instanceof Monster || event.getEntity() instanceof Ghast || event.getEntity() instanceof Slime)) {
            return;
        }
        if (!plugin.getSettings().getBoolean("config.spawner-mobs-drop", false)) {
            if (event.getEntity().hasMetadata("loot.spawnreason")) {
                return;
            }
        }
        double dropMultiplier = 1D;
        double rarityMultiplier = 1D;
        double dropPenalty = 1.0D;
        double expMultiplier = 1.0D;

        Player killer = event.getEntity().getKiller();

        if (!isWaterMob(event.getEntity())) {
            if (isWater(event.getEntity().getLocation())) {
                dropPenalty *= 0.6D;
                expMultiplier *= 0.6D;
            }
            if (isWater(killer.getLocation())) {
                dropPenalty *= 0.8D;
                expMultiplier *= 0.8D;
            }
        }

        if (isClimbing(killer.getLocation())) {
            dropPenalty *= 0.5D;
            expMultiplier *= 0.5D;
        }

        if (!isValidDamageType(event.getEntity().getLastDamageCause().getCause())) {
            dropPenalty *= 0.5D;
            expMultiplier *= 0.6D;
        }

        double distanceFromWhereTagged = -1D;
        double taggerDistance = -1;
        UUID bestTaggerLmao = null;
        if (plugin.getAnticheatManager().isTagged(event.getEntity())) {
            distanceFromWhereTagged = plugin.getAnticheatManager().getTag(
                    event.getEntity()).getEntityLocation().distanceSquared(event.getEntity().getLocation());
            if (plugin.getAnticheatManager().getTag(event.getEntity()).getTaggerLocation(killer.getUniqueId()) != null) {
                taggerDistance = plugin.getAnticheatManager().getTag(event.getEntity())
                        .getTaggerLocation(killer.getUniqueId()).distanceSquared(killer.getLocation());
            }
            bestTaggerLmao = plugin.getAnticheatManager().getTag(event.getEntity()).getHighestDamageTagger();
        }

        if (distanceFromWhereTagged < 2) {
            dropPenalty *= 0.8D;
            expMultiplier *= 0.7D;
            if (distanceFromWhereTagged < 0.8) {
                dropPenalty *= 0.4D;
                expMultiplier *= 0.4D;
            }
        }
        if (taggerDistance < 1.5) {
            dropPenalty *= 0.6D;
            expMultiplier *= 0.8D;
        }

        int mobLevel = 0;
        if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
            if (event.getEntity().getCustomName() != null) {
                mobLevel = NumberUtils.toInt(CharMatcher.DIGIT.retainFrom(ChatColor.stripColor(event.getEntity()
                        .getCustomName())));
                int playerLevel = killer.getLevel();
                if (playerLevel < 100) {
                    int range = plugin.getSettings().getInt("config.range-before-penalty", 15);
                    double levelDiff = playerLevel - mobLevel;
                    if (Math.abs(levelDiff) > range) {
                        if (levelDiff > 0) {
                            dropPenalty *= Math.max(1 - (levelDiff - range) * 0.1, 0.4);
                            expMultiplier *= Math.max(1 - (levelDiff - range) * 0.05, 0.3);
                        } else {
                            dropPenalty *= Math.max(1 - (-levelDiff - range) * 0.1, 0);
                            expMultiplier *= Math.max(1 - (-levelDiff - range) * 0.1, 0.05);
                        }
                    }
                }
            }
        }

        CreatureMod mod = plugin.getCreatureModManager().getCreatureMod(event.getEntity().getType());
        String mobName = event.getEntity().getCustomName();
        double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity()
                                                                                     .getWorld().getSpawnLocation());
        // Adding to drop rate based on rank of Elite Mobs
        if (mobName != null) {
            if (mobName.startsWith(ChatColor.GRAY + "")) {
                dropMultiplier = 1D;
                rarityMultiplier = 1D;
            } else if (mobName.startsWith(ChatColor.BLUE + "Magic")) {
                dropMultiplier = 10D;
                rarityMultiplier = 2D;
                expMultiplier *= 2D;
            } else if (mobName.startsWith(ChatColor.DARK_PURPLE + "Rare")) {
                dropMultiplier = 20D;
                rarityMultiplier = 3D;
                expMultiplier *= 3D;
            } else if (mobName.startsWith(ChatColor.RED + "Epic")) {
                dropMultiplier = 30D;
                rarityMultiplier = 4D;
                expMultiplier *= 4D;
            } else if (mobName.startsWith(ChatColor.GOLD + "Legendary")) {
                dropMultiplier = 40D;
                rarityMultiplier = 5D;
                expMultiplier *= 5D;
            } else if (mobName.startsWith(ChatColor.DARK_RED + "Boss")) {
                dropMultiplier = 35D;
                rarityMultiplier = 4D;
            }
        }

        event.setDroppedExp((int) (expMultiplier * event.getDroppedExp()));

        AttributedEntity pStats = plugin.getStrifePlugin().getEntityStatCache().getAttributedEntity(killer);
        double dropMultBonus = plugin.getStrifePlugin().getMultiplierManager().getDropMult();

        dropMultiplier += dropMultBonus + pStats.getAttribute(StrifeAttribute.ITEM_DISCOVERY) / 100;
        rarityMultiplier += pStats.getAttribute(StrifeAttribute.ITEM_RARITY) / 100;

        if (killer.hasPotionEffect(PotionEffectType.LUCK)) {
            rarityMultiplier += 0.1;
        }

        dropMultiplier *= dropPenalty;
        World w = event.getEntity().getWorld();

        if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.normal-drop", 0D)) {
            Tier tier = plugin.getTierManager().getRandomTier();
            ItemRarity rarity;

            if (rarityMultiplier == 1D) {
                rarity = plugin.getRarityManager().getRandomRarity();
            } else {
                rarity = plugin.getRarityManager().getRandomRarityWithBonus(rarityMultiplier);
            }

            HiltItemStack his = plugin.getNewItemBuilder()
                .withTier(tier)
                .withRarity(rarity)
                .withLevel(Math.max(1, Math.min(mobLevel - 2 + random.nextIntRange(0, 5), 100)))
                .withItemGenerationReason(ItemGenerationReason.MONSTER)
                .build();

            int qualityBonus = 1;
            double qualityChance = plugin.getSettings().getDouble("config.random-quality-chance", 0.1);
            double multiQualityChance = plugin.getSettings().getDouble("config.multi-quality-chance", 0.1);

            if (random.nextDouble() <= qualityChance) {
                while (random.nextDouble() <= multiQualityChance && qualityBonus < 5) {
                    qualityBonus++;
                }
                his = upgradeItemQuality(his, qualityBonus);
            }

            int upgradeBonus = 1;
            double upgradeChance = plugin.getSettings().getDouble("config.random-upgrade-chance", 0.1);
            double multiUpgradeChance = plugin.getSettings().getDouble("config.multi-upgrade-chance", 0.1);

            if (random.nextDouble() <= upgradeChance) {
                while (random.nextDouble() <= multiUpgradeChance && upgradeBonus < 9) {
                    upgradeBonus++;
                }
                his = upgradeItem(his, upgradeBonus);
            }

            Item drop = w.dropItemNaturally(event.getEntity().getLocation(), his);
            if (bestTaggerLmao != null) {
                applyOwnerMeta(drop, bestTaggerLmao);
                killer = Bukkit.getPlayer(bestTaggerLmao);
            }
            if (rarity.isBroadcast() || upgradeBonus > 4 || qualityBonus > 2) {
                broadcast(Bukkit.getPlayer(bestTaggerLmao), his);
            }
        }
        if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.craft-mat", 0D)) {
            Object[] matArr = plugin.getCraftMatManager().getCraftMaterials().keySet().toArray();
            Material m = (Material)matArr[random.nextInt(matArr.length)];

            int quality = 2;
            while (random.nextDouble() <= plugin.getSettings().getDouble("config.drops.material-quality-up", 0.1D) &&
                quality < 3) {
                quality++;
            }

            double materialLevel = mobLevel - (mobLevel * 0.3 * random.nextDouble());
            HiltItemStack his = MaterialUtil.buildMaterial(
                m, plugin.getCraftMatManager().getCraftMaterials().get(m), (int)materialLevel, quality);
            his.setAmount(1 + random.nextInt(2));

            Item drop = w.dropItemNaturally(event.getEntity().getLocation(), his);
            if (bestTaggerLmao != null) {
                applyOwnerMeta(drop, bestTaggerLmao);
                killer = Bukkit.getPlayer(bestTaggerLmao);
            }
        }
        if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.socket-gem", 0D)) {
            SocketGem sg;
            if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
                sg = plugin.getSocketGemManager().getRandomSocketGemByLevel(mobLevel);
            } else {
                sg = plugin.getSocketGemManager().getRandomSocketGem(true, distanceSquared, mod != null ?
                        mod.getSocketGemMults() : new HashMap<SocketGem, Double>());
            }

            HiltItemStack his = sg.toItemStack(1);
            Item drop = w.dropItemNaturally(event.getEntity().getLocation(), his);
            if (bestTaggerLmao != null) {
                applyOwnerMeta(drop, bestTaggerLmao);
                killer = Bukkit.getPlayer(bestTaggerLmao);
            }
            if (sg.isBroadcast()) {
                broadcast(Bukkit.getPlayer(bestTaggerLmao), his);
            }
        }
        if (plugin.getSettings().getBoolean("config.custom-enchanting", true)) {
            if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.enchant-gem", 0D)) {
                EnchantmentTome es = plugin.getEnchantmentStoneManager().getRandomEnchantmentStone(true, distanceSquared,
                        mod != null ? mod.getEnchantmentStoneMults() : new HashMap<EnchantmentTome, Double>());
                HiltItemStack his = es.toItemStack(1);
                Item drop = w.dropItemNaturally(event.getEntity().getLocation(), his);
                if (bestTaggerLmao != null) {
                    applyOwnerMeta(drop, bestTaggerLmao);
                    killer = Bukkit.getPlayer(bestTaggerLmao);
                }
                if (es.isBroadcast()) {
                    broadcast(Bukkit.getPlayer(bestTaggerLmao), his);
                }
            }
        }
        if (random.nextDouble() <  dropMultiplier * plugin.getSettings().getDouble("config.drops.upgrade-scroll", 0D)) {
            UpgradeScroll us = new UpgradeScroll(UpgradeScroll.ScrollType.random(true));
            Item drop = w.dropItemNaturally(event.getEntity().getLocation(), us);
            if (bestTaggerLmao != null) {
                applyOwnerMeta(drop, bestTaggerLmao);
            }

            ScrollType scrollType = us.getScrollType();
            if (scrollType == ScrollType.ANCIENT ||
                scrollType == ScrollType.AWAKENED ||
                scrollType == ScrollType.FLAWLESS ||
                scrollType == ScrollType.DIM ||
                scrollType == ScrollType.SHINING ||
                scrollType == ScrollType.ILLUMINATING ||
                scrollType == ScrollType.RADIANT) {
                broadcast(Bukkit.getPlayer(bestTaggerLmao), us);
            }
        }
        if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.identity-tome", 0D)) {
            HiltItemStack his = new IdentityTome();
            Item drop = w.dropItemNaturally(event.getEntity().getLocation(), his);
            if (bestTaggerLmao != null) {
                applyOwnerMeta(drop, bestTaggerLmao);
            }
        }
        if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.custom-item", 0D)) {
            CustomItem ci;
            if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
                ci = plugin.getCustomItemManager().getRandomCustomItemByLevel(mobLevel);
            } else {
                ci = plugin.getCustomItemManager().getRandomCustomItem(true, distanceSquared, mod != null ?
                        mod.getCustomItemMults() : new HashMap<CustomItem, Double>());
            }
            HiltItemStack his = ci.toItemStack(1);

            int qualityBonus = 1;
            if (ci.canBeQuality()) {
                double qualityChance = plugin.getSettings().getDouble("config.random-quality-chance", 0.1);
                double multiQualityChance = plugin.getSettings().getDouble("config.multi-quality-chance", 0.1);

                if (random.nextDouble() <= qualityChance) {
                    while (random.nextDouble() <= multiQualityChance && qualityBonus < 5) {
                        qualityBonus++;
                    }
                    his = upgradeItemQuality(his, qualityBonus);
                }
            }

            Item drop = w.dropItemNaturally(event.getEntity().getLocation(), his);
            if (bestTaggerLmao != null) {
                applyOwnerMeta(drop, bestTaggerLmao);
                killer = Bukkit.getPlayer(bestTaggerLmao);
            }
            if (ci.isBroadcast() || qualityBonus > 2) {
                broadcast(killer, his);
            }
        }
        if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.socket-extender", 0D)) {
            HiltItemStack his = new SocketExtender();
            broadcast(Bukkit.getPlayer(bestTaggerLmao), his);
            Item drop = w.dropItemNaturally(event.getEntity().getLocation(), his);
            if (bestTaggerLmao != null) {
                applyOwnerMeta(drop, bestTaggerLmao);
            }
        }
        // NOTE: Drop bonus should not be applied to Unidentified Items!
        if (random.nextDouble() < dropPenalty * plugin.getSettings().getDouble("config.drops.unidentified-item", 0D)) {
            Material m = Material.WOOD_SWORD;
            HiltItemStack his;
            if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
                his = new UnidentifiedItem(m, Math.min(mobLevel, 100));
            } else {
                his = new UnidentifiedItem(m, -1);
            }
            ItemMeta itemMeta = his.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            his.setItemMeta(itemMeta);
            Item drop = w.dropItemNaturally(event.getEntity().getLocation(), his);
            if (bestTaggerLmao != null) {
                applyOwnerMeta(drop, bestTaggerLmao);
            }
        }
    }

    private HiltItemStack upgradeItem(HiltItemStack his, int upgradeBonus) {
        boolean succeed = false;
        List<String> lore = his.getLore();
        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i);
            String ss = ChatColor.stripColor(s);
            if (!ss.startsWith("+")) {
                continue;
            }
            succeed = true;
            String loreLev = CharMatcher.DIGIT.or(CharMatcher.is('-')).retainFrom(ss);
            int loreLevel = NumberUtils.toInt(loreLev);
            lore.set(i, s.replace("+" + loreLevel, "+" + (loreLevel + upgradeBonus)));
            String name = getFirstColor(his.getName()) + ("+" + upgradeBonus) + " " + his.getName();
            his.setName(name);
            break;
        }
        if (succeed) {
            his.setLore(lore);
            if (upgradeBonus > 6) {
                his.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                his.setItemFlags(Sets.newHashSet(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES));
            }
        }
        return his;
    }

    private HiltItemStack upgradeItemQuality(HiltItemStack his, int upgradeBonus) {
        boolean succeed = false;
        List<String> lore = his.getLore();
        for (int i = 0; i < lore.size(); i++) {
            String s = lore.get(i);
            String ss = ChatColor.stripColor(s);
            if (!ss.startsWith("+")) {
                continue;
            }
            succeed = true;
            String loreLev = CharMatcher.DIGIT.or(CharMatcher.is('-')).retainFrom(ss);
            int loreLevel = NumberUtils.toInt(loreLev);
            lore.set(i, s.replace("+" + loreLevel, "+" + (loreLevel + upgradeBonus)));
            String qualityEnhanceName = plugin.getSettings().getString("language.quality." + upgradeBonus, "");
            String name = getFirstColor(his.getName()) + qualityEnhanceName + " " + his.getName();
            his.setName(name);
            break;
        }
        if (succeed) {
            his.setLore(lore);
        }
        return his;
    }

    private void broadcast(Player player, HiltItemStack his) {
        FancyMessage message = new FancyMessage("");
        String mess = plugin.getSettings().getString("language.broadcast.found-item", "");
        String[] split = mess.split(" ");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            String str = TextUtils.color(s);
            if (str.contains("%player%")) {
                message.then(str.replace("%player%", player.getDisplayName()));
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

    private boolean isClimbing(Location location) {
        Block b = location.getBlock();
        return b.getType() == Material.LADDER || b.getType() == Material.VINE;
    }

    private boolean isValidDamageType(DamageCause cause) {
        return cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.ENTITY_EXPLOSION ||
            cause == DamageCause.PROJECTILE || cause == DamageCause.MAGIC || cause == DamageCause.FIRE_TICK;
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

    public void applyOwnerMeta(Item drop, UUID owner) {
        drop.setMetadata("loot-owner", new FixedMetadataValue(plugin, owner));
        drop.setMetadata("loot-time", new FixedMetadataValue(plugin, System.currentTimeMillis()));
    }
}
