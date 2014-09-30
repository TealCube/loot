package info.faceland.loot.listeners.spawning;

import info.faceland.facecore.shade.mcml.mcml.MCMLBuilder;
import info.faceland.facecore.shade.mcml.mcml.shade.fanciful.FancyMessage;
import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.enchantments.EnchantmentStone;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.math.LootRandom;
import info.faceland.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        CreatureMod mod = plugin.getCreatureModManager().getCreatureMod(event.getEntity().getType());
        if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.normal-item", 0D)) {
            // drop a normal random item
            double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity().getWorld()
                                                                                          .getSpawnLocation());
            Tier t = plugin.getTierManager().getRandomTier(true, distanceSquared, mod.getTierMults());
            HiltItemStack his = plugin.getNewItemBuilder().withTier(t).withItemGenerationReason(
                    ItemGenerationReason.MONSTER).build();
            event.getDrops().add(his);

            if (!t.isBroadcast()) {
                return;
            }

            Map<String, Object> replacements = new HashMap<>();
            replacements.put("{ITEM}", his);

            MCMLBuilder builder = new MCMLBuilder(
                    TextUtils.args(plugin.getSettings().getString("language.broadcast-found-item", ""),
                                   new String[][]{
                                           {"%player%", event.getEntity().getKiller().getDisplayName()},
                                           {"%item%", his.getName()}
                                   }), replacements);
            FancyMessage fancyMessage = builder.buildFancyMessage();
            for (Player player : Bukkit.getOnlinePlayers()) {
                fancyMessage.send(player);
            }
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.socket-gem", 0D)) {
            // drop a socket gem
            double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity().getWorld()
                                                                                          .getSpawnLocation());
            SocketGem sg = plugin.getSocketGemManager().getRandomSocketGem(true, distanceSquared,
                                                                           mod.getSocketGemMults());
            HiltItemStack his = sg.toItemStack(1);
            event.getDrops().add(his);

            if (!sg.isBroadcast()) {
                return;
            }

            Map<String, Object> replacements = new HashMap<>();
            replacements.put("{ITEM}", his);

            MCMLBuilder builder = new MCMLBuilder(
                    TextUtils.args(plugin.getSettings().getString("language.broadcast-found-item", ""),
                                   new String[][]{
                                           {"%player%", event.getEntity().getKiller().getDisplayName()},
                                           {"%item%", his.getName()}
                                   }), replacements);
            FancyMessage fancyMessage = builder.buildFancyMessage();
            for (Player player : Bukkit.getOnlinePlayers()) {
                fancyMessage.send(player);
            }
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.enchant-gem", 0D)) {
            // drop an enchant gem
            double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity().getWorld()
                                                                                          .getSpawnLocation());
            EnchantmentStone es = plugin.getEnchantmentStoneManager().getRandomEnchantmentStone(true, distanceSquared,
                                                                           mod.getEnchantmentStoneMults());
            HiltItemStack his = es.toItemStack(1);
            event.getDrops().add(his);

            if (!es.isBroadcast()) {
                return;
            }

            Map<String, Object> replacements = new HashMap<>();
            replacements.put("{ITEM}", his);

            MCMLBuilder builder = new MCMLBuilder(
                    TextUtils.args(plugin.getSettings().getString("language.broadcast-found-item", ""),
                                   new String[][]{
                                           {"%player%", event.getEntity().getKiller().getDisplayName()},
                                           {"%item%", his.getName()}
                                   }), replacements);
            FancyMessage fancyMessage = builder.buildFancyMessage();
            for (Player player : Bukkit.getOnlinePlayers()) {
                fancyMessage.send(player);
            }
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.upgrade-scroll", 0D)) {
            // drop an upgrade scroll
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.identity-tome", 0D)) {
            // drop an identity tome
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.custom-item", 0D)) {
            // drop a custom item
            double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity().getWorld()
                                                                                          .getSpawnLocation());
            CustomItem ci = plugin.getCustomItemManager().getRandomCustomItem(true, distanceSquared,
                                                                              mod.getCustomItemMults());
            HiltItemStack his = ci.toItemStack(1);
            event.getDrops().add(his);

            if (!ci.isBroadcast()) {
                return;
            }

            Map<String, Object> replacements = new HashMap<>();
            replacements.put("{ITEM}", his);

            MCMLBuilder builder = new MCMLBuilder(
                    TextUtils.args(plugin.getSettings().getString("language.broadcast-found-item", ""),
                                   new String[][]{
                                           {"%player%", event.getEntity().getKiller().getDisplayName()},
                                           {"%item%", his.getName()}
                                   }), replacements);
            FancyMessage fancyMessage = builder.buildFancyMessage();
            for (Player player : Bukkit.getOnlinePlayers()) {
                fancyMessage.send(player);
            }
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drop.socket-extender", 0D)) {
            // drop a socket extender
        } else {
            // do nothing
        }
    }

}
