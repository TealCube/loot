package info.faceland.loot.listeners;

import static info.faceland.loot.utils.inventory.InventoryUtil.broadcast;
import static info.faceland.loot.utils.inventory.InventoryUtil.getFirstColor;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import com.tealcube.minecraft.bukkit.shade.google.common.collect.Sets;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.data.ItemRarity;
import info.faceland.loot.data.UniqueLoot;
import info.faceland.loot.events.LootDropEvent;
import info.faceland.loot.items.prefabs.IdentityTome;
import info.faceland.loot.items.prefabs.SocketExtender;
import info.faceland.loot.items.prefabs.UnidentifiedItem;
import info.faceland.loot.items.prefabs.UpgradeScroll;
import info.faceland.loot.items.prefabs.UpgradeScroll.ScrollType;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.utils.inventory.MaterialUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class LootDropListener implements Listener {

  private final LootPlugin plugin;
  private final LootRandom random;
  private final String itemFoundFormat;

  public LootDropListener(LootPlugin plugin) {
    this.plugin = plugin;
    this.random = new LootRandom();
    this.itemFoundFormat = plugin.getSettings().getString("language.broadcast.found-item", "");
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onLootDrop(LootDropEvent event) {
    double dropMultiplier = event.getQuantityMultiplier();
    double rarityMultiplier = event.getQualityMultiplier();
    int mobLevel = event.getMonsterLevel();
    UUID looterUUID = event.getLooterUUID();
    Player killer = Bukkit.getPlayer(looterUUID);

    if (StringUtils.isNotBlank(event.getUniqueEntity())) {
      if (plugin.getUniqueDropsManager().getData(event.getUniqueEntity()) != null) {
        UniqueLoot loot = plugin.getUniqueDropsManager().getData(event.getUniqueEntity());
        dropMultiplier *= loot.getQuantityMultiplier();
        rarityMultiplier *= loot.getQualityMultiplier();
        doUniqueDrops(loot, event.getLocation(), killer);
      }
    }

    if (random.nextDouble() < dropMultiplier * plugin.getSettings()
        .getDouble("config.drops.normal-drop", 0D)) {
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
      double multiQualityChance = plugin.getSettings()
          .getDouble("config.multi-quality-chance", 0.1);

      if (random.nextDouble() <= qualityChance) {
        while (random.nextDouble() <= multiQualityChance && qualityBonus < 5) {
          qualityBonus++;
        }
        upgradeItemQuality(his, qualityBonus);
      }

      int upgradeBonus = 1;
      double upgradeChance = plugin.getSettings().getDouble("config.random-upgrade-chance", 0.1);
      double multiUpgradeChance = plugin.getSettings()
          .getDouble("config.multi-upgrade-chance", 0.1);

      if (random.nextDouble() <= upgradeChance) {
        while (random.nextDouble() <= multiUpgradeChance && upgradeBonus < 9) {
          upgradeBonus++;
        }
        upgradeItem(his, upgradeBonus);
      }

      boolean broadcast = rarity.isBroadcast() || upgradeBonus > 4 || qualityBonus > 2;
      dropItem(event.getLocation(), his, killer, broadcast);
    }
    if (random.nextDouble() < dropMultiplier * plugin.getSettings()
        .getDouble("config.drops.craft-mat", 0D)) {
      Object[] matArr = plugin.getCraftMatManager().getCraftMaterials().keySet().toArray();
      Material m = (Material) matArr[random.nextInt(matArr.length)];

      int quality = 2;
      while (random.nextDouble() <= plugin.getSettings()
          .getDouble("config.drops.material-quality-up", 0.1D) &&
          quality < 3) {
        quality++;
      }

      double materialLevel = mobLevel - (mobLevel * 0.3 * random.nextDouble());
      HiltItemStack his = MaterialUtil.buildMaterial(
          m, plugin.getCraftMatManager().getCraftMaterials().get(m), (int) materialLevel, quality);
      his.setAmount(1 + random.nextInt(2));

      dropItem(event.getLocation(), his, killer, false);
    }
    if (random.nextDouble() < dropMultiplier * plugin.getSettings()
        .getDouble("config.drops.socket-gem", 0D)) {
      SocketGem sg;
      if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
        sg = plugin.getSocketGemManager().getRandomSocketGemByLevel(mobLevel);
      } else {
        sg = plugin.getSocketGemManager().getRandomSocketGem(true, event.getDistance());
      }

      HiltItemStack his = sg.toItemStack(1);
      dropItem(event.getLocation(), his, killer, sg.isBroadcast());
    }
    if (plugin.getSettings().getBoolean("config.custom-enchanting", true)) {
      if (random.nextDouble() < dropMultiplier * plugin.getSettings()
          .getDouble("config.drops.enchant-gem", 0D)) {
        EnchantmentTome es = plugin.getEnchantmentStoneManager()
            .getRandomEnchantmentStone(true, event.getDistance());
        HiltItemStack his = es.toItemStack(1);
        dropItem(event.getLocation(), his, killer, es.isBroadcast());
      }
    }
    if (random.nextDouble() < dropMultiplier * plugin.getSettings()
        .getDouble("config.drops.upgrade-scroll", 0D)) {
      UpgradeScroll us = new UpgradeScroll(UpgradeScroll.ScrollType.random(true));

      ScrollType scrollType = us.getScrollType();
      boolean broadcast = scrollType == ScrollType.ANCIENT || scrollType == ScrollType.AWAKENED ||
          scrollType == ScrollType.FLAWLESS || scrollType == ScrollType.DIM ||
          scrollType == ScrollType.SHINING || scrollType == ScrollType.ILLUMINATING ||
          scrollType == ScrollType.RADIANT;

      dropItem(event.getLocation(), us, null, broadcast);
    }
    if (random.nextDouble() < dropMultiplier * plugin.getSettings()
        .getDouble("config.drops.identity-tome", 0D)) {
      HiltItemStack his = new IdentityTome();
      dropItem(event.getLocation(), his, killer, false);
    }
    if (random.nextDouble() < dropMultiplier * plugin.getSettings()
        .getDouble("config.drops.custom-item", 0D)) {
      CustomItem ci;
      if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
        ci = plugin.getCustomItemManager().getRandomCustomItemByLevel(mobLevel);
      } else {
        ci = plugin.getCustomItemManager()
            .getRandomCustomItem(true, event.getDistance());
      }
      HiltItemStack his = ci.toItemStack(1);

      int qualityBonus = 1;
      if (ci.canBeQuality()) {
        double qualityChance = plugin.getSettings().getDouble("config.random-quality-chance", 0.1);
        double multiQualityChance = plugin.getSettings()
            .getDouble("config.multi-quality-chance", 0.1);

        if (random.nextDouble() <= qualityChance) {
          while (random.nextDouble() <= multiQualityChance && qualityBonus < 5) {
            qualityBonus++;
          }
          his = upgradeItemQuality(his, qualityBonus);
        }
      }
      boolean broadcast = ci.isBroadcast() || qualityBonus > 2;
      dropItem(event.getLocation(), his, killer, broadcast);
    }
    if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.socket-extender", 0D)) {
      HiltItemStack his = new SocketExtender();
      dropItem(event.getLocation(), his, killer, true);
    }
    // NOTE: Drop bonus should not be applied to Unidentified Items!
    if (random.nextDouble() < dropMultiplier * plugin.getSettings()
        .getDouble("config.drops.unidentified-item", 0D)) {
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

      dropItem(event.getLocation(), his, null, false);
    }
  }

  private void doUniqueDrops(UniqueLoot uniqueLoot, Location location, Player killer) {
    for (String gemString : uniqueLoot.getGemMap().keySet()) {
      if (uniqueLoot.getGemMap().get(gemString) > random.nextDouble()) {
        SocketGem gem = plugin.getSocketGemManager().getSocketGem(gemString);
        if (gem == null) {
          continue;
        }
        HiltItemStack his = gem.toItemStack(1);
        dropItem(location, his, killer, gem.isBroadcast());
      }
    }
    for (String tomeString : uniqueLoot.getTomeMap().keySet()) {
      if (uniqueLoot.getTomeMap().get(tomeString) > random.nextDouble()) {
        EnchantmentTome tome = plugin.getEnchantmentStoneManager().getEnchantmentStone(tomeString);
        if (tome == null) {
          continue;
        }
        HiltItemStack his = tome.toItemStack(1);
        dropItem(location, his, killer, tome.isBroadcast());
      }
    }
    for (String tableName : uniqueLoot.getCustomItemMap().keySet()) {
      double totalWeight = 0;
      for (double weight : uniqueLoot.getCustomItemMap().get(tableName).values()) {
        totalWeight += weight;
      }
      totalWeight *= random.nextDouble();
      double currentWeight = 0;
      for (String customName : uniqueLoot.getCustomItemMap().get(tableName).keySet()) {
        currentWeight += uniqueLoot.getCustomItemMap().get(tableName).get(customName);
        if (currentWeight >= totalWeight) {
          if ("NO_DROP_WEIGHT".equalsIgnoreCase(customName)) {
            break;
          }
          CustomItem ci = plugin.getCustomItemManager().getCustomItem(customName);
          if (ci == null) {
            break;
          }
          HiltItemStack his = ci.toItemStack(1);
          dropItem(location, his, killer, ci.isBroadcast());
          break;
        }
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
      String qualityEnhanceName = plugin.getSettings()
          .getString("language.quality." + upgradeBonus, "");
      String name = getFirstColor(his.getName()) + qualityEnhanceName + " " + his.getName();
      his.setName(name);
      break;
    }
    if (succeed) {
      his.setLore(lore);
    }
    return his;
  }

  private void dropItem(Location loc, HiltItemStack itemStack, Player looter, boolean broadcast) {
    Item drop = loc.getWorld().dropItemNaturally(loc, itemStack);
    if (looter != null) {
      applyOwnerMeta(drop, looter.getUniqueId());
      if (broadcast) {
        broadcast(looter, itemStack, itemFoundFormat);
      }
    }
  }

  private void applyOwnerMeta(Item drop, UUID owner) {
    drop.setMetadata("loot-owner", new FixedMetadataValue(plugin, owner));
    drop.setMetadata("loot-time", new FixedMetadataValue(plugin, System.currentTimeMillis()));
  }
}
