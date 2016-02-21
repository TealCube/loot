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
package info.faceland.loot.commands;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import com.tealcube.minecraft.bukkit.shade.google.common.collect.Sets;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.math.Vec3;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.items.prefabs.*;
import info.faceland.loot.math.LootRandom;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;
import se.ranzdo.bukkit.methodcommand.FlagArg;
import se.ranzdo.bukkit.methodcommand.Flags;

import java.util.List;

public final class LootCommand {

    private final LootPlugin plugin;
    private final LootRandom random;

    public LootCommand(LootPlugin plugin) {
        this.plugin = plugin;
        this.random = new LootRandom(System.currentTimeMillis());
    }

    @Command(identifier = "loot spawn", permissions = "loot.command.spawn")
    @Flags(identifier = {"c", "s", "t", "e", "se", "u", "id", "us", "rp"},
           description = {"custom", "socket gem", "tier", "enchantment", "socket extender", "unidentified", "tome",
                   "upgrade scroll", "reveal powder"})
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
                             @FlagArg("rp") boolean revealPowder) {
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
                if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
                    sender.getInventory().addItem(new UnidentifiedItem(m, random.nextInt(100)));
                } else {
                    sender.getInventory().addItem(new UnidentifiedItem(m, -1));
                }
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
        } else if (revealPowder) {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(new RevealPowder());
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
    @Flags(identifier = {"c", "s", "t", "e", "se", "u", "id", "us", "rp"},
            description = {"custom", "socket gem", "tier", "enchantment", "socket extender", "unidentified", "tome",
                    "upgrade scroll", "reveal powder"})
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
                                @FlagArg("rp") boolean revealPowder) {
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
                if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
                    sender.getInventory().addItem(new UnidentifiedItem(m, random.nextInt(100)));
                } else {
                    sender.getInventory().addItem(new UnidentifiedItem(m, -1));
                }
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
        } else if (revealPowder) {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(new RevealPowder());
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
    @Flags(identifier = {"c", "s", "t", "e", "se", "u", "id", "us", "rp"},
            description = {"custom", "socket gem", "tier", "enchantment", "socket extender", "unidentified", "tome",
                    "upgrade scroll", "reveal powder"})
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
                            @FlagArg("rp") boolean revealPowder) {
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
        } else if (revealPowder) {
            for (int i = 0; i < amount; i++) {
                target.getInventory().addItem(new RevealPowder());
            }
            MessageUtils.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.reveal-powder", ""),
                                     new String[][]{{"%amount%", amount + ""}});
            MessageUtils.sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
        } else if (unidentified) {
            for (int i = 0; i < amount; i++) {
                Tier t = plugin.getTierManager().getRandomTier(true);
                Material[] array = t.getAllowedMaterials().toArray(new Material[t.getAllowedMaterials().size()]);
                Material m = array[random.nextInt(array.length)];
                if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
                    target.getInventory().addItem(new UnidentifiedItem(m, random.nextInt(100)));
                } else {
                    target.getInventory().addItem(new UnidentifiedItem(m, -1));
                }
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

    @Command(identifier = "loot chest", permissions = "loot.command.chest", onlyPlayers = true)
    public void chestSubcommand(Player sender) {
        List<Block> blocks = sender.getLineOfSight(Sets.newHashSet(Material.AIR), 10);
        for (Block block : blocks) {
            if (block.getType() == Material.CHEST) {
                Vec3 loc = new Vec3(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
                plugin.getChestManager().addChestLocation(loc);
                MessageUtils.sendMessage(sender, "<green>You added a gem combiner chest!");
                return;
            }
        }
        MessageUtils.sendMessage(sender, "<red>You could not add a chest.");
    }

    @Command(identifier = "loot reload", permissions = "loot.command.reload", onlyPlayers = false)
    public void reloadSubcommand(CommandSender sender) {
        plugin.disable();
        plugin.enable();
        MessageUtils.sendMessage(sender, "<green>Loot has been reloaded.");
    }

}
