package info.faceland.loot.commands;

import info.faceland.facecore.shade.command.Arg;
import info.faceland.facecore.shade.command.Command;
import info.faceland.facecore.shade.command.FlagArg;
import info.faceland.facecore.shade.command.Flags;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.enchantments.EnchantmentStone;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.items.prefabs.SocketExtender;
import info.faceland.loot.utils.messaging.Chatty;
import org.bukkit.entity.Player;

public final class LootCommand {

    private final LootPlugin plugin;

    public LootCommand(LootPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(identifier = "loot spawn", permissions = "loot.command.spawn")
    @Flags(identifier = {"c", "s", "t", "e", "se"}, description = {"custom", "socket gem", "tier", "enchantment"})
    public void spawnCommand(Player sender, @Arg(name = "amount", def = "1") int amount,
                             @Arg(name = "name", def = "") String name,
                             @FlagArg("c") boolean custom,
                             @FlagArg("s") boolean socket,
                             @FlagArg("t") boolean tier,
                             @FlagArg("e") boolean enchantment,
                             @FlagArg("se") boolean socketExtender) {
        if (custom) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getCustomItemManager().getRandomCustomItem(true).toItemStack(1));
                }
                Chatty.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                CustomItem ci = plugin.getCustomItemManager().getCustomItem(name);
                if (ci == null) {
                    Chatty.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.custom-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(ci.toItemStack(1));
                }
                Chatty.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (socket) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getSocketGemManager().getRandomSocketGem(true).toItemStack(1));
                }
                Chatty.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                SocketGem sg = plugin.getSocketGemManager().getSocketGem(name);
                if (sg == null) {
                    Chatty.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.gem-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(sg.toItemStack(1));
                }
                Chatty.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (enchantment) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getEnchantmentStoneManager().getRandomEnchantmentStone(true).toItemStack(1));
                }
                Chatty.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                EnchantmentStone es = plugin.getEnchantmentStoneManager().getEnchantmentStone(name);
                if (es == null) {
                    Chatty.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.stone-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(es.toItemStack(1));
                }
                Chatty.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else if (socketExtender) {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(new SocketExtender());
            }
            Chatty.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.socket-extender", ""),
                               new String[][]{{"%amount%", amount + ""}});
        } else if (tier) {
            if (name.equals("")) {
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND).build());
                }
                Chatty.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            } else {
                Tier t = plugin.getTierManager().getTier(name);
                if (t == null) {
                    Chatty.sendMessage(
                            sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    sender.getInventory().addItem(
                            plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND).build());
                }
                Chatty.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                                   new String[][]{{"%amount%", amount + ""}});
            }
        } else {
            for (int i = 0; i < amount; i++) {
                sender.getInventory().addItem(
                        plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND).build());
            }
            Chatty.sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
                               new String[][]{{"%amount%", amount + ""}});
        }
    }

}
