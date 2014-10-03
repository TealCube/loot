package info.faceland.loot.listeners.spawning;

import info.faceland.facecore.shade.voorhees.PrettyMessageFactory;
import info.faceland.facecore.shade.voorhees.api.IPrettyMessage;
import info.faceland.hilt.HiltItemStack;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.enchantments.EnchantmentStone;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.items.prefabs.SocketExtender;
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
        if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.normal-drop", 0D)) {
            // drop a normal random item
            double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity().getWorld()
                                                                                          .getSpawnLocation());
            Tier t = plugin.getTierManager().getRandomTier(true, distanceSquared, mod != null ? mod.getTierMults() :
                                                                                  new HashMap<Tier, Double>());
            HiltItemStack his = plugin.getNewItemBuilder().withTier(t).withItemGenerationReason(
                    ItemGenerationReason.MONSTER).build();
            event.getDrops().add(his);

            if (!t.isBroadcast()) {
                return;
            }

            broadcast(event, his);
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.socket-gem", 0D)) {
            // drop a socket gem
            double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity().getWorld()
                                                                                          .getSpawnLocation());
            SocketGem sg = plugin.getSocketGemManager().getRandomSocketGem(true, distanceSquared,
                                                                           mod != null ? mod.getSocketGemMults() :
                                                                           new HashMap<SocketGem, Double>());
            HiltItemStack his = sg.toItemStack(1);
            event.getDrops().add(his);

            if (!sg.isBroadcast()) {
                return;
            }

            broadcast(event, his);
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.enchant-gem", 0D)) {
            // drop an enchant gem
            double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity().getWorld()
                                                                                          .getSpawnLocation());
            EnchantmentStone es = plugin.getEnchantmentStoneManager().getRandomEnchantmentStone(
                    true, distanceSquared,
                    mod != null ? mod.getEnchantmentStoneMults() : new HashMap<EnchantmentStone, Double>());
            HiltItemStack his = es.toItemStack(1);
            event.getDrops().add(his);

            if (!es.isBroadcast()) {
                return;
            }

            broadcast(event, his);
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.upgrade-scroll", 0D)) {
            // drop an upgrade scroll
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.identity-tome", 0D)) {
            // drop an identity tome
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.custom-item", 0D)) {
            // drop a custom item
            double distanceSquared = event.getEntity().getLocation().distanceSquared(event.getEntity().getWorld()
                                                                                          .getSpawnLocation());
            CustomItem ci = plugin.getCustomItemManager().getRandomCustomItem(true, distanceSquared,
                                                                              mod != null ? mod.getCustomItemMults()
                                                                                          :
                                                                              new HashMap<CustomItem, Double>());
            HiltItemStack his = ci.toItemStack(1);
            event.getDrops().add(his);

            if (!ci.isBroadcast()) {
                return;
            }

            broadcast(event, his);
        } else if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.socket-extender", 0D)) {
            // drop a socket extender
            event.getDrops().add(new SocketExtender());
        } else {
            // do nothing
        }
    }

    private void broadcast(EntityDeathEvent event, HiltItemStack his) {
        IPrettyMessage message = PrettyMessageFactory.buildPrettyMessage();
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

}
