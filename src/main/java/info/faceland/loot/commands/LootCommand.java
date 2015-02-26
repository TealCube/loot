/*
 * This file is part of Loot, licensed under the ISC License.
 *
 * Copyright (c) 2014 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package info.faceland.loot.commands;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Arg;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Command;
import com.tealcube.minecraft.bukkit.kern.methodcommand.FlagArg;
import com.tealcube.minecraft.bukkit.kern.methodcommand.Flags;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.items.prefabs.IdentityTome;
import info.faceland.loot.items.prefabs.ProtectionCharm;
import info.faceland.loot.items.prefabs.SocketExtender;
import info.faceland.loot.items.prefabs.UnidentifiedItem;
import info.faceland.loot.items.prefabs.UpgradeScroll;
import info.faceland.loot.math.LootRandom;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LootCommand {

    private final LootPlugin plugin;
    private final LootRandom random;

    public LootCommand(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @Command(identifier = "loot spawn", permissions = "loot.command.spawn")
    @Flags(identifier = {"c", "s", "t", "e", "se", "u", "id", "us", "ch"},
           description = {"custom", "socket gem", "tier", "enchantment", "socket extender", "unidentified", "tome",
                   "upgrade scroll", "charm"})
    public void spawnCommand(Player sender, @Arg(name = "amount", def = "1") int amount,
                             @Arg(name = "name", def = "") String name,
                             @FlagArg("c") boolean custom,
                             @FlagArg("s") boolean socket,
                             @FlagArg("t") boolean tier,
                             @FlagArg("e") boolean enchantment,
                             @FlagArg("se") boolean socketExtender,
                             @FlagArg("u") boolean unidentified,
                             @FlagArg("id") boolean tome,
                             @FlagArg("us") boolean upgradeScroll,
                             @FlagArg("ch") boolean charm) {
        if (custom) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getCustomItemManager().getRandomCustomItem(true).toItemStack(1));
                }
                MessageUtils.sendMessage(sender,
                        plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
                        new String[][]{{"%amount%", amount + ""}});
            } else {
                CustomItem ci = plugin.getCustomItemManager().getCustomItem(name);
                if (ci == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.custom-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(ci.toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (socket) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getSocketGemManager().getRandomSocketGem(true).toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                SocketGem sg = plugin.getSocketGemManager().getSocketGem(name);
                if (sg == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.gem-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(sg.toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (enchantment) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getEnchantmentStoneManager().getRandomEnchantmentStone(true).toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                EnchantmentTome es = plugin.getEnchantmentStoneManager().getEnchantmentStone(name);
                if (es == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.stone-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(es.toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (socketExtender) {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(new SocketExtender());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.socket-extender", ""),
                               new String[][]{{"%amount%", amount + ""}});
        } else if (unidentified) {
            for (int i = 0; i < amount; i++) {
                Tier t = plugin.getTierManager().getRandomTier(true);
                Material[] array = t.getAllowedMaterials().toArray(new Material[t.getAllowedMaterials().size()]);
                Material m = array[random.nextInt(array.length)];
                sender.getInventory().addItem(new UnidentifiedItem(m));
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.unidentified-item", ""),
                               new String[][]{{"%amount%", amount + ""}});
        } else if (tome) {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(new IdentityTome());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.identity-tome", ""),
                               new String[][]{{"%amount%", amount + ""}});
        } else if (tier) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND).build());
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                Tier t = plugin.getTierManager().getTier(name);
                if (t == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND).build());
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (upgradeScroll) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(new UpgradeScroll(UpgradeScroll.ScrollType.random(false)));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                UpgradeScroll.ScrollType type = UpgradeScroll.ScrollType.getByName(name);
                if (type == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(new UpgradeScroll(type));
                }
                MessageUtils.sendMessage(
                        sender, plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""));
            }
        } else if (charm) {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(new ProtectionCharm());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                               new String[][]{{"%amount%", amount + ""}});
        } else {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(
                        plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND).build());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                               new String[][]{{"%amount%", amount + ""}});
        }
    }

    @Command(identifier = "loot simulate", permissions = "loot.command.simulate")
    @Flags(identifier = {"c", "s", "t", "e", "se", "u", "id", "us", "ch"},
            description = {"custom", "socket gem", "tier", "enchantment", "socket extender", "unidentified", "tome",
                    "upgrade scroll", "charm"})
    public void simulateCommand(Player sender, @Arg(name = "amount", def = "1") int amount,
                                @Arg(name = "name", def = "") String name,
                                @FlagArg("c") boolean custom,
                                @FlagArg("s") boolean socket,
                                @FlagArg("t") boolean tier,
                                @FlagArg("e") boolean enchantment,
                                @FlagArg("se") boolean socketExtender,
                                @FlagArg("u") boolean unidentified,
                                @FlagArg("id") boolean tome,
                                @FlagArg("us") boolean upgradeScroll,
                                @FlagArg("ch") boolean charm) {
        double distanceFromSpawnSquared = sender.getLocation().distanceSquared(sender.getWorld().getSpawnLocation());
        if (custom) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getCustomItemManager().getRandomCustomItem(true, distanceFromSpawnSquared)
                                  .toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                CustomItem ci = plugin.getCustomItemManager().getCustomItem(name);
                if (ci == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.custom-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(ci.toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (socket) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getSocketGemManager().getRandomSocketGem(true, distanceFromSpawnSquared)
                                  .toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                SocketGem sg = plugin.getSocketGemManager().getSocketGem(name);
                if (sg == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.gem-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(sg.toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (enchantment) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getEnchantmentStoneManager()
                                  .getRandomEnchantmentStone(true, distanceFromSpawnSquared).toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                EnchantmentTome es = plugin.getEnchantmentStoneManager().getEnchantmentStone(name);
                if (es == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.stone-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(es.toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (socketExtender) {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(new SocketExtender());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.socket-extender", ""),
                               new String[][]{{"%amount%", amount + ""}});
        } else if (unidentified) {
            for (int i = 0; i < amount; i++) {
                Tier t = plugin.getTierManager().getRandomTier(true, distanceFromSpawnSquared);
                Material[] array = t.getAllowedMaterials().toArray(new Material[t.getAllowedMaterials().size()]);
                Material m = array[random.nextInt(array.length)];
                sender.getInventory().addItem(new UnidentifiedItem(m));
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.unidentified-item", ""),
                               new String[][]{{"%amount%", amount + ""}});
        } else if (tome) {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(new IdentityTome());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.identity-tome", ""),
                               new String[][]{{"%amount%", amount + ""}});
        } else if (tier) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getNewItemBuilder().withDistance(distanceFromSpawnSquared)
                                  .withItemGenerationReason(ItemGenerationReason.COMMAND).build());
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                Tier t = plugin.getTierManager().getTier(name);
                if (t == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND).build());
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (upgradeScroll) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(new UpgradeScroll(UpgradeScroll.ScrollType.random(false)));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                UpgradeScroll.ScrollType type = UpgradeScroll.ScrollType.getByName(name);
                if (type == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(new UpgradeScroll(type));
                }
                MessageUtils.sendMessage(
                        sender, plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""));
            }
        } else if (charm) {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(new ProtectionCharm());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                               new String[][]{{"%amount%", amount + ""}});
        } else {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(
                        plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND)
                              .withDistance(distanceFromSpawnSquared).build());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                               new String[][]{{"%amount%", amount + ""}});
        }
    }

    @Command(identifier = "loot give", permissions = "loot.command.give", onlyPlayers = false)
    @Flags(identifier = {"c", "s", "t", "e", "se", "u", "id", "us", "ch"},
            description = {"custom", "socket gem", "tier", "enchantment", "socket extender", "unidentified", "tome",
                    "upgrade scroll", "charm"})
    public void giveCommand(CommandSender sender,
                            @Arg(name = "player") Player target,
                            @Arg(name = "amount", def = "1") int amount,
                            @Arg(name = "name", def = "") String name,
                            @FlagArg("c") boolean custom,
                            @FlagArg("s") boolean socket,
                            @FlagArg("t") boolean tier,
                            @FlagArg("e") boolean enchantment,
                            @FlagArg("se") boolean socketExtender,
                            @FlagArg("u") boolean unidentified,
                            @FlagArg("id") boolean tome,
                            @FlagArg("us") boolean upgradeScroll,
                            @FlagArg("ch") boolean charm) {
        if (custom) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(
                            plugin.getCustomItemManager().getRandomCustomItem(true).toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                CustomItem ci = plugin.getCustomItemManager().getCustomItem(name);
                if (ci == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.custom-failure", ""));
                    MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.failure", ""));
                    target.updateInventory();
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(ci.toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
                MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
            }
        } else if (socket) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(
                            plugin.getSocketGemManager().getRandomSocketGem(true).toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
                MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                SocketGem sg = plugin.getSocketGemManager().getSocketGem(name);
                if (sg == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.gem-failure", ""));
                    MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.failure", ""));
                    target.updateInventory();
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(sg.toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
                MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
            }
        } else if (enchantment) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(
                            plugin.getEnchantmentStoneManager().getRandomEnchantmentStone(true).toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
                MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
            } else {
                EnchantmentTome es = plugin.getEnchantmentStoneManager().getEnchantmentStone(name);
                if (es == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.stone-failure", ""));
                    MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.failure", ""));
                    target.updateInventory();
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(es.toItemStack(1));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
                MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
            }
        } else if (socketExtender) {
            for (int i = 0; i < amount; i++) {
                target.getInventory().addItem(new SocketExtender());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.socket-extender", ""),
                               new String[][]{{"%amount%", amount + ""}});
            MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
        } else if (unidentified) {
            for (int i = 0; i < amount; i++) {
                Tier t = plugin.getTierManager().getRandomTier(true);
                Material[] array = t.getAllowedMaterials().toArray(new Material[t.getAllowedMaterials().size()]);
                Material m = array[random.nextInt(array.length)];
                target.getInventory().addItem(new UnidentifiedItem(m));
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.unidentified-item", ""),
                               new String[][]{{"%amount%", amount + ""}});
            MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
        } else if (tome) {
            for (int i = 0; i < amount; i++) {
                target.getInventory().addItem(new IdentityTome());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.identity-tome", ""),
                               new String[][]{{"%amount%", amount + ""}});
            MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
        } else if (tier) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(
                            plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND).build());
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
                MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
            } else {
                Tier t = plugin.getTierManager().getTier(name);
                if (t == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
                    MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.failure", ""));
                    target.updateInventory();
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(
                            plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND).build());
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
                MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
            }
        } else if (upgradeScroll) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(new UpgradeScroll(UpgradeScroll.ScrollType.random(false)));
                }
                MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""),
                                   new String[][]{{"%amount%", amount + ""}});
                MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
            } else {
                UpgradeScroll.ScrollType type = UpgradeScroll.ScrollType.getByName(name.toUpperCase());
                if (type == null) {
                    MessageUtils.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
                    target.updateInventory();
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(new UpgradeScroll(type));
                }
                MessageUtils.sendMessage(
                        sender, plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""));
                MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
            }
        } else if (charm) {
            for (int i = 0; i < amount; i++) {
                target.getInventory().addItem(new ProtectionCharm());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                               new String[][]{{"%amount%", amount + ""}});
            MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
        } else {
            for (int i = 0; i < amount; i++) {
                target.getInventory().addItem(
                        plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND).build());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                               new String[][]{{"%amount%", amount + ""}});
            MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
        }
    }

}
