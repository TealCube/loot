/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package info.faceland.loot.commands;

import static com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils.sendMessage;

import com.tealcube.minecraft.bukkit.TextUtils;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.data.ItemRarity;
import info.faceland.loot.data.UpgradeScroll;
import info.faceland.loot.enchantments.EnchantmentTome;
import info.faceland.loot.items.prefabs.ArcaneEnhancer;
import info.faceland.loot.items.prefabs.IdentityTome;
import info.faceland.loot.items.prefabs.PurifyingScroll;
import info.faceland.loot.items.prefabs.SocketExtender;
import info.faceland.loot.items.prefabs.UnidentifiedItem;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.menu.pawn.PawnMenu;
import info.faceland.loot.menu.upgrade.EnchantMenu;
import info.faceland.loot.tier.Tier;
import info.faceland.loot.utils.DropUtil;
import info.faceland.loot.utils.InventoryUtil;
import info.faceland.loot.utils.MaterialUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;
import se.ranzdo.bukkit.methodcommand.FlagArg;
import se.ranzdo.bukkit.methodcommand.Flags;
import se.ranzdo.bukkit.methodcommand.Wildcard;

public final class LootCommand {

  private final LootPlugin plugin;
  private final LootRandom random;
  private String awardFormat;
  private String awardFormatSelf;

  public LootCommand(LootPlugin plugin) {
    this.plugin = plugin;
    this.random = new LootRandom(System.currentTimeMillis());
    awardFormat = plugin.getSettings().getString("language.broadcast.reward-item", "");
    awardFormatSelf = plugin.getSettings().getString("language.broadcast.reward-item-self", "");
  }

  @Command(identifier = "loot reward", permissions = "loot.command.spawn", onlyPlayers = false)
  public void reward(CommandSender sender,
      @Arg(name = "target") Player target,
      @Arg(name = "minLevel", def = "1") int minLevel,
      @Arg(name = "maxLevel", def = "100") int maxLevel,
      @Arg(name = "rarity") String rarity) {
    Tier t = DropUtil.getTier(target);
    if (t == null) {
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
      return;
    }

    int level = minLevel + (int) ((maxLevel - minLevel) * Math.random());

    ItemRarity itemRarity = plugin.getRarityManager().getRarity(rarity);

    ItemStack item = plugin.getNewItemBuilder()
        .withTier(t)
        .withLevel(level)
        .withRarity(itemRarity)
        .withItemGenerationReason(ItemGenerationReason.COMMAND)
        .build().getStack();

    target.playSound(target.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
    target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    target.getInventory().addItem(item);
    if (itemRarity.isBroadcast()) {
      InventoryUtil.broadcast(target, item, awardFormat, true);
    } else {
      InventoryUtil.broadcast(target, item, awardFormatSelf, false);
    }
    sendMessage(sender, "Rewarded " + target.getName() + " successfully!");
  }

  @Command(identifier = "loot spawn", permissions = "loot.command.spawn")
  @Flags(identifier = {"c", "s", "t", "e", "se", "u", "id", "us", "rp"},
      description = {"custom", "socket gem", "tier", "enchantment", "socket extender",
          "unidentified", "tome",
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
      @FlagArg("us") boolean upgradeScroll) {
    if (custom) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          giveItem(sender, plugin.getCustomItemManager().getRandomCustomItem(true).toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
            new String[][]{{"%amount%", amount + ""}});
        return;
      }
      CustomItem ci = plugin.getCustomItemManager().getCustomItem(name);
      if (ci == null) {
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.custom-failure", ""));
        return;
      }
      for (int i = 0; i < amount; i++) {
        sender.getInventory().addItem(ci.toItemStack(1));
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
          new String[][]{{"%amount%", amount + ""}});
      return;
    }
    if (socket) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          giveItem(sender, plugin.getSocketGemManager().getRandomSocketGem(true).toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
            new String[][]{{"%amount%", amount + ""}});
        return;
      }
      SocketGem sg = plugin.getSocketGemManager().getSocketGem(name);
      if (sg == null) {
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.gem-failure", ""));
        return;
      }
      for (int i = 0; i < amount; i++) {
        sender.getInventory().addItem(sg.toItemStack(1));
      }
      sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
          new String[][]{{"%amount%", amount + ""}});
      return;
    }
    if (enchantment) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          sender.getInventory()
              .addItem(plugin.getEnchantTomeManager().getRandomEnchantTome().toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
            new String[][]{{"%amount%", amount + ""}});
        return;
      }
      EnchantmentTome es = plugin.getEnchantTomeManager().getEnchantTome(name);
      if (es == null) {
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.stone-failure", ""));
        return;
      }
      for (int i = 0; i < amount; i++) {
        sender.getInventory().addItem(es.toItemStack(1));
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
          new String[][]{{"%amount%", amount + ""}});
      return;
    }
    if (socketExtender) {
      for (int i = 0; i < amount; i++) {
        sender.getInventory().addItem(new SocketExtender());
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.socket-extender", ""),
          new String[][]{{"%amount%", amount + ""}});
      return;
    }
    if (unidentified) {
      for (int i = 0; i < amount; i++) {
        Tier t = plugin.getTierManager().getRandomTier();
        Material[] array = t.getAllowedMaterials()
            .toArray(new Material[t.getAllowedMaterials().size()]);
        Material m = array[random.nextInt(array.length)];
        if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
          sender.getInventory().addItem(new UnidentifiedItem(m, random.nextInt(100)));
        } else {
          sender.getInventory().addItem(new UnidentifiedItem(m, -1));
        }
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.unidentified-item", ""),
          new String[][]{{"%amount%", amount + ""}});
      return;
    }
    if (tome) {
      for (int i = 0; i < amount; i++) {
        sender.getInventory().addItem(new IdentityTome());
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.identity-tome", ""),
          new String[][]{{"%amount%", amount + ""}});
      return;
    }
    if (tier) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(
              plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND)
                  .build()
                  .getStack());
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.other-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      } else {
        Tier t = plugin.getTierManager().getTier(name);
        if (t == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
          return;
        }
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(plugin.getNewItemBuilder().withTier(t).build().getStack());
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.other-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      }
      return;
    }
    if (upgradeScroll) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(plugin.getScrollManager()
              .buildItemStack(plugin.getScrollManager().getRandomScroll()));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""),
            new String[][]{{"%amount%", amount + ""}});
        return;
      }
      UpgradeScroll scroll = plugin.getScrollManager().getScroll(name);
      if (scroll == null) {
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
        return;
      }
      for (int i = 0; i < amount; i++) {
        sender.getInventory().addItem(plugin.getScrollManager().buildItemStack(scroll));
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""));
      return;
    }
    for (int i = 0; i < amount; i++) {
      giveItem(sender, plugin.getNewItemBuilder()
          .withItemGenerationReason(ItemGenerationReason.COMMAND)
          .withLevel(sender.getLevel())
          .withTier(plugin.getTierManager().getRandomTier())
          .withRarity(plugin.getRarityManager().getRandomRarity())
          .build().getStack());
    }
    sendMessage(sender, plugin.getSettings().getString("language.commands.spawn.other-success", ""),
        new String[][]{{"%amount%", amount + ""}});
  }

  @Command(identifier = "loot materials", permissions = "loot.command.spawn", onlyPlayers = false)
  public void reward(CommandSender sender, @Arg(name = "target") Player target,
      @Arg(name = "itemLevel", def = "1") int itemLevel,
      @Arg(name = "itemQuality", def = "1") int quality) {

    quality = Math.min(5, Math.max(quality, 1));

    for (Material m : plugin.getCraftMatManager().getCraftMaterials().keySet()) {
      ItemStack itemStack = MaterialUtil.buildMaterial(m,
          plugin.getCraftMatManager().getCraftMaterials().get(m), itemLevel, quality);
      target.getInventory().addItem(itemStack);
    }
  }

  @Command(identifier = "loot give purify", permissions = "loot.command.spawn", onlyPlayers = false)
  public void givePurify(CommandSender sender, @Arg(name = "target") Player target,
      @Arg(name = "amount", def = "1") int amount) {

    ItemStack scroll = PurifyingScroll.get();
    scroll.setAmount(amount);
    target.getInventory().addItem(scroll);
  }

  @Command(identifier = "loot give enhance", permissions = "loot.command.spawn", onlyPlayers = false)
  public void giveEnhance(CommandSender sender, @Arg(name = "target") Player target,
      @Arg(name = "amount", def = "1") int amount) {

    ItemStack enhancer = ArcaneEnhancer.get();
    enhancer.setAmount(amount);
    target.getInventory().addItem(enhancer);
  }

  @Command(identifier = "loot simulate", permissions = "loot.command.simulate")
  @Flags(identifier = {"c", "s", "t", "e", "se", "u", "id", "us", "rp"},
      description = {"custom", "socket gem", "tier", "enchantment", "socket extender",
          "unidentified", "tome",
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
      @FlagArg("us") boolean upgradeScroll) {
    double distanceFromSpawnSquared = sender.getLocation()
        .distanceSquared(sender.getWorld().getSpawnLocation());
    if (custom) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(
              plugin.getCustomItemManager().getRandomCustomItem(true, distanceFromSpawnSquared)
                  .toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      } else {
        CustomItem ci = plugin.getCustomItemManager().getCustomItem(name);
        if (ci == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.custom-failure", ""));
          return;
        }
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(ci.toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      }
    } else if (socket) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(
              plugin.getSocketGemManager().getRandomSocketGem(true, distanceFromSpawnSquared)
                  .toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      } else {
        SocketGem sg = plugin.getSocketGemManager().getSocketGem(name);
        if (sg == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.gem-failure", ""));
          return;
        }
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(sg.toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      }
    } else if (enchantment) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(
              plugin.getEnchantTomeManager().getRandomEnchantTome().toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      } else {
        EnchantmentTome es = plugin.getEnchantTomeManager().getEnchantTome(name);
        if (es == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.stone-failure", ""));
          return;
        }
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(es.toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      }
    } else if (socketExtender) {
      for (int i = 0; i < amount; i++) {
        sender.getInventory().addItem(new SocketExtender());
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.socket-extender", ""),
          new String[][]{{"%amount%", amount + ""}});
    } else if (unidentified) {
      for (int i = 0; i < amount; i++) {
        Tier t = plugin.getTierManager().getRandomTier();
        Material[] array = t.getAllowedMaterials()
            .toArray(new Material[t.getAllowedMaterials().size()]);
        Material m = array[random.nextInt(array.length)];
        if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
          sender.getInventory().addItem(new UnidentifiedItem(m, random.nextInt(100)));
        } else {
          sender.getInventory().addItem(new UnidentifiedItem(m, -1));
        }
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.unidentified-item", ""),
          new String[][]{{"%amount%", amount + ""}});
    } else if (tome) {
      for (int i = 0; i < amount; i++) {
        sender.getInventory().addItem(new IdentityTome());
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.identity-tome", ""),
          new String[][]{{"%amount%", amount + ""}});
    } else if (tier) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(
              plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND)
                  .build().getStack());
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.other-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      } else {
        Tier t = plugin.getTierManager().getTier(name);
        if (t == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
          return;
        }
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(
              plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND)
                  .build().getStack());
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.other-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      }
    } else if (upgradeScroll) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(plugin.getScrollManager()
              .buildItemStack(plugin.getScrollManager().getRandomScroll()));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""),
            new String[][]{{"%amount%", amount + ""}});
      } else {
        UpgradeScroll scroll = plugin.getScrollManager().getScroll(name);
        if (scroll == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
          return;
        }
        for (int i = 0; i < amount; i++) {
          sender.getInventory().addItem(plugin.getScrollManager().buildItemStack(scroll));
        }
        sendMessage(
            sender, plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""));
      }
    } else {
      for (int i = 0; i < amount; i++) {
        sender.getInventory().addItem(
            plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND)
                .build().getStack());
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.other-success", ""),
          new String[][]{{"%amount%", amount + ""}});
    }
  }

  @Command(identifier = "loot give", permissions = "loot.command.give", onlyPlayers = false)
  @Flags(identifier = {"c", "s", "t", "e", "se", "u", "id", "us", "rp"},
      description = {"custom", "socket gem", "tier", "enchantment", "socket extender",
          "unidentified", "tome",
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
      @FlagArg("us") boolean upgradeScroll) {
    if (custom) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          giveItem(target, plugin.getCustomItemManager().getRandomCustomItem(true).toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      } else {
        CustomItem ci = plugin.getCustomItemManager().getCustomItem(name);
        if (ci == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.custom-failure", ""));
          sendMessage(target, plugin.getSettings().getString("language.commands.give.failure", ""));
          target.updateInventory();
          return;
        }
        for (int i = 0; i < amount; i++) {
          target.getInventory().addItem(ci.toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.custom-success", ""),
            new String[][]{{"%amount%", amount + ""}});
        sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
      }
    } else if (socket) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          target.getInventory().addItem(
              plugin.getSocketGemManager().getRandomSocketGem(true).toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
            new String[][]{{"%amount%", amount + ""}});
        sendMessage(target,
            plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
            new String[][]{{"%amount%", amount + ""}});
      } else {
        SocketGem sg = plugin.getSocketGemManager().getSocketGem(name);
        if (sg == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.gem-failure", ""));
          sendMessage(target, plugin.getSettings().getString("language.commands.give.failure", ""));
          target.updateInventory();
          return;
        }
        for (int i = 0; i < amount; i++) {
          target.getInventory().addItem(sg.toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.gem-success", ""),
            new String[][]{{"%amount%", amount + ""}});
        sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
      }
    } else if (enchantment) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          target.getInventory().addItem(
              plugin.getEnchantTomeManager().getRandomEnchantTome().toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
            new String[][]{{"%amount%", amount + ""}});
        sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
      } else {
        EnchantmentTome es = plugin.getEnchantTomeManager().getEnchantTome(name);
        if (es == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.stone-failure", ""));
          sendMessage(target, plugin.getSettings().getString("language.commands.give.failure", ""));
          target.updateInventory();
          return;
        }
        for (int i = 0; i < amount; i++) {
          target.getInventory().addItem(es.toItemStack(1));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.stone-success", ""),
            new String[][]{{"%amount%", amount + ""}});
        sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
      }
    } else if (socketExtender) {
      for (int i = 0; i < amount; i++) {
        target.getInventory().addItem(new SocketExtender());
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.socket-extender", ""),
          new String[][]{{"%amount%", amount + ""}});
      sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
    } else if (unidentified) {
      for (int i = 0; i < amount; i++) {
        Tier t = plugin.getTierManager().getRandomTier();
        Material[] array = t.getAllowedMaterials()
            .toArray(new Material[t.getAllowedMaterials().size()]);
        Material m = array[random.nextInt(array.length)];
        if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
          target.getInventory().addItem(new UnidentifiedItem(m, random.nextInt(100)));
        } else {
          target.getInventory().addItem(new UnidentifiedItem(m, -1));
        }
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.unidentified-item", ""),
          new String[][]{{"%amount%", amount + ""}});
      sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
    } else if (tome) {
      for (int i = 0; i < amount; i++) {
        target.getInventory().addItem(new IdentityTome());
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.identity-tome", ""),
          new String[][]{{"%amount%", amount + ""}});
      sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
    } else if (tier) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          plugin.getNewItemBuilder()
              .withTier(plugin.getTierManager().getRandomTier())
              .withRarity(plugin.getRarityManager().getRandomRarity())
              .withLevel(random.nextIntRange(1, 100))
              .withItemGenerationReason(ItemGenerationReason.COMMAND).build();
          giveItem(target,
              plugin.getNewItemBuilder().withItemGenerationReason(ItemGenerationReason.COMMAND)
                  .build().getStack());
          target.getInventory().addItem();
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.other-success", ""),
            new String[][]{{"%amount%", amount + ""}});
        sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
      } else {
        Tier t = plugin.getTierManager().getTier(name);
        ItemRarity r = plugin.getRarityManager().getRandomRarity();
        if (t == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
          sendMessage(target, plugin.getSettings().getString("language.commands.give.failure", ""));
          target.updateInventory();
          return;
        }
        for (int i = 0; i < amount; i++) {
          target.getInventory()
              .addItem(plugin.getNewItemBuilder().withTier(t).withRarity(r).build().getStack());
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.other-success", ""),
            new String[][]{{"%amount%", amount + ""}});
        sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
      }
    } else if (upgradeScroll) {
      if (name.equals("")) {
        for (int i = 0; i < amount; i++) {
          UpgradeScroll scroll = plugin.getScrollManager().getRandomScroll();
          target.getInventory().addItem(plugin.getScrollManager().buildItemStack(scroll));
        }
        sendMessage(sender,
            plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""),
            new String[][]{{"%amount%", amount + ""}});
        sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
      } else {
        UpgradeScroll scroll = plugin.getScrollManager().getScroll(name);
        if (scroll == null) {
          sendMessage(
              sender, plugin.getSettings().getString("language.commands.spawn.other-failure", ""));
          target.updateInventory();
          return;
        }
        for (int i = 0; i < amount; i++) {
          target.getInventory().addItem(plugin.getScrollManager().buildItemStack(scroll));
        }
        sendMessage(
            sender, plugin.getSettings().getString("language.commands.spawn.upgrade-scroll", ""));
        sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
      }
    } else {
      for (int i = 0; i < amount; i++) {
        target.getInventory().addItem(
            plugin.getNewItemBuilder()
                .withLevel(random.nextIntRange(1, 100))
                .withRarity(plugin.getRarityManager().getRandomRarity())
                .withTier(plugin.getTierManager().getRandomTier())
                .withItemGenerationReason(ItemGenerationReason.COMMAND).build().getStack());
      }
      sendMessage(sender,
          plugin.getSettings().getString("language.commands.spawn.other-success", ""),
          new String[][]{{"%amount%", amount + ""}});
      sendMessage(target, plugin.getSettings().getString("language.commands.give.receive", ""));
    }
  }

  @Command(identifier = "loot reload", permissions = "loot.command.reload", onlyPlayers = false)
  public void reloadSubcommand(CommandSender sender) {
    plugin.disable();
    plugin.enable();
    sendMessage(sender,
        plugin.getSettings().getString("language.command.reload", "&aLoot reloaded!"));
  }

  @Command(identifier = "loot upgrade", permissions = "loot.command.reload")
  public void upgradeSubcommand(CommandSender sender, @Arg(name = "player") Player target) {
    if (target == null || !target.isValid()) {
      return;
    }
    EnchantMenu menu = new EnchantMenu(plugin);
    menu.open(target);
  }

  @Command(identifier = "loot pawn", permissions = "loot.command.reload", onlyPlayers = false)
  public void pawnSubcommand(CommandSender sender, @Arg(name = "player") Player target) {
    if (target == null || !target.isValid()) {
      return;
    }
    PawnMenu menu = new PawnMenu(plugin);
    menu.open(target);
  }

  @Command(identifier = "loot renametag", permissions = "loot.command.renametag", onlyPlayers = true)
  public void renameSubcommand(Player sender, @Arg(name = "item name") @Wildcard String newLore) {
    ItemStack heldItem = new ItemStack(sender.getEquipment().getItemInMainHand());
    if (heldItem.getType() != Material.NAME_TAG) {
      sendMessage(sender, plugin.getSettings().getString("language.command.renamefail", ""));
      return;
    }
    if (!(ItemStackExtensionsKt.getDisplayName(heldItem)
        .equals(ChatColor.WHITE + "Item Rename Tag"))) {
      sendMessage(sender, plugin.getSettings().getString("language.command.renamefail", ""));
      return;
    }
    if (isIllegalName(ChatColor.stripColor(newLore))) {
      sendMessage(sender, plugin.getSettings().getString("language.command.invalidname", ""));
      return;
    }
    if (newLore.length() > 20 || newLore.startsWith("+") || Character.isDigit(newLore.charAt(0)) ||
        Character.isDigit(newLore.charAt(1))) {
      sendMessage(sender, plugin.getSettings().getString("language.command.invalidname", ""));
      return;
    }
    List<String> lore = ItemStackExtensionsKt.getLore(heldItem);
    lore.set(3, ChatColor.WHITE + ChatColor.stripColor(TextUtils.color(newLore)));
    ItemStackExtensionsKt.setLore(heldItem, lore);
    sender.getEquipment().setItemInMainHand(heldItem);
    sender.updateInventory();
    sendMessage(sender, plugin.getSettings().getString("language.command.renamesuccess", ""));
  }

  private void giveItem(Player player, ItemStack itemStack) {
    player.getInventory().addItem(itemStack);
  }

  private boolean isIllegalName(String name) {
    return name.equals("Socket Extender") || name.startsWith("Enchantment Tome") ||
        name.startsWith("Socket Gem") || name.startsWith("Scroll Augment") ||
        name.endsWith("Upgrade Scroll") || name.equals("Faceguy's Tears") ||
        name.equals("Identity Tome") || name.equals("Unidentified Item") ||
        name.equals("Item Rename Tag") || name.equals("Magic Crystal") ||
        name.equals("Soul Stone");
  }
}
