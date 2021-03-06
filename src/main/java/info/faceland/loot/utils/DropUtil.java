package info.faceland.loot.utils;

import static info.faceland.loot.utils.InventoryUtil.broadcast;
import static info.faceland.loot.utils.InventoryUtil.getFirstColor;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.data.BuiltItem;
import info.faceland.loot.data.ItemRarity;
import info.faceland.loot.data.UniqueLoot;
import info.faceland.loot.data.UpgradeScroll;
import info.faceland.loot.enchantments.EnchantmentTome;
import info.faceland.loot.events.LootDropEvent;
import info.faceland.loot.items.prefabs.ArcaneEnhancer;
import info.faceland.loot.items.prefabs.IdentityTome;
import info.faceland.loot.items.prefabs.PurifyingScroll;
import info.faceland.loot.items.prefabs.SocketExtender;
import info.faceland.loot.items.prefabs.UnidentifiedItem;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.sockets.SocketGem;
import info.faceland.loot.tier.Tier;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.inventivetalent.glow.GlowAPI;
import org.inventivetalent.glow.GlowAPI.Color;

public class DropUtil implements Listener {

  private static LootPlugin plugin;

  private static String itemFoundFormat;
  private static Map<EntityType, Double> specialStatEntities;
  private static Map<String, Double> specialStatWorlds;

  private static double customizedTierChance;
  private static double normalDropChance;
  private static double scrollDropChance;
  private static double socketDropChance;
  private static double tomeDropChance;
  private static double enhancerDropChance;
  private static double purityDropChance;

  private static LootRandom random;

  public static void refresh() {
    plugin = LootPlugin.getInstance();
    itemFoundFormat = plugin.getSettings().getString("language.broadcast.found-item", "");
    specialStatEntities = plugin.fetchSpecialStatEntities();
    specialStatWorlds = plugin.fetchSpecialStatWorlds();

    customizedTierChance = plugin.getSettings()
        .getDouble("config.drops.customized-tier-chance", 0D);

    normalDropChance = plugin.getSettings().getDouble("config.drops.normal-drop", 0D);
    scrollDropChance = plugin.getSettings().getDouble("config.drops.upgrade-scroll", 0D);
    socketDropChance = plugin.getSettings().getDouble("config.drops.socket-gem", 0D);
    tomeDropChance = plugin.getSettings().getDouble("config.drops.enchant-gem", 0D);
    enhancerDropChance = plugin.getSettings().getDouble("config.drops.arcane-enhancer", 0D);
    purityDropChance = plugin.getSettings().getDouble("config.drops.purity-scroll", 0D);

    random = new LootRandom();
  }

  public static void dropLoot(LootDropEvent event) {
    Player killer = Bukkit.getPlayer(event.getLooterUUID());
    if (killer == null) {
      return;
    }
    double dropMultiplier = event.getQuantityMultiplier();
    double rarityMultiplier = event.getQualityMultiplier();
    float mobLevel = event.getMonsterLevel();
    float itemLevelRange = Math.max(5, mobLevel / 10);
    float itemLevel = (float) (mobLevel - (Math.random() * itemLevelRange));
    itemLevel = Math.min(100, Math.max(1, (int) itemLevel));

    List<ItemRarity> bonusDrops = new ArrayList<>(event.getBonusTierItems());
    if (StringUtils.isNotBlank(event.getUniqueEntity())) {
      if (plugin.getUniqueDropsManager().getData(event.getUniqueEntity()) != null) {
        UniqueLoot loot = plugin.getUniqueDropsManager().getData(event.getUniqueEntity());
        dropMultiplier *= loot.getQuantityMultiplier();
        rarityMultiplier *= loot.getQualityMultiplier();
        doUniqueDrops(loot, event.getLocation(), killer);
        bonusDrops.addAll(loot.getBonusEquipment());
      }
    }

    EntityType entityType = event.getEntity().getType();
    String worldName = event.getLocation().getWorld().getName();
    boolean specialStat = addSpecialStat(entityType, worldName);
    boolean normalDrop = dropMultiplier * normalDropChance > random.nextDouble();

    while (bonusDrops.size() > 0 || normalDrop) {
      Tier tier = getTier(killer);
      ItemRarity rarity;

      if (rarityMultiplier == 1D) {
        rarity = plugin.getRarityManager().getRandomRarity();
      } else {
        rarity = plugin.getRarityManager().getRandomRarityWithBonus(rarityMultiplier);
      }
      if (bonusDrops.size() > 0) {
        ItemRarity dropRarity = bonusDrops.get(random.nextIntRange(0, bonusDrops.size()));
        if (dropRarity.getPower() > rarity.getPower()) {
          rarity = dropRarity;
        }
        bonusDrops.remove(dropRarity);
      } else {
        normalDrop = false;
      }

      BuiltItem builtItem = plugin.getNewItemBuilder()
          .withTier(tier)
          .withRarity(rarity)
          .withLevel((int) itemLevel)
          .withItemGenerationReason(ItemGenerationReason.MONSTER)
          .withSpecialStat(specialStat)
          .build();

      ItemStack tierItem = builtItem.getStack();

      int qualityBonus = 1;
      double qualityChance = plugin.getSettings().getDouble("config.random-quality-chance", 0.1);
      double multiQualityChance = plugin.getSettings().getDouble("config.multi-quality-chance", 0.1);

      if (random.nextDouble() <= qualityChance) {
        while (random.nextDouble() <= multiQualityChance && qualityBonus < 5) {
          qualityBonus++;
        }
        upgradeItemQuality(tierItem, qualityBonus);
      }

      int upgradeBonus = 1;
      double upgradeChance = plugin.getSettings().getDouble("config.random-upgrade-chance", 0.1);
      double multiUpgradeChance = plugin.getSettings().getDouble("config.multi-upgrade-chance", 0.1);

      if (random.nextDouble() <= upgradeChance) {
        while (random.nextDouble() <= multiUpgradeChance && upgradeBonus < 15) {
          upgradeBonus++;
        }
        upgradeItem(tierItem, upgradeBonus);
      }

      boolean broadcast = rarity.isBroadcast() || upgradeBonus > 4 || qualityBonus > 2;
      Color glowColor = rarity.isBroadcast() ? Color.valueOf(rarity.getColor().name()) : null;
      dropItem(event.getLocation(), tierItem, killer, builtItem.getTicksLived(), broadcast, glowColor);
    }

    if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.craft-mat", 0D)) {
      Object[] matArr = plugin.getCraftMatManager().getCraftMaterials().keySet().toArray();
      Material m = (Material) matArr[random.nextInt(matArr.length)];

      int quality = 2;
      while (random.nextDouble() <= plugin.getSettings()
          .getDouble("config.drops.material-quality-up", 0.1D) &&
          quality < 3) {
        quality++;
      }

      double materialLevel = mobLevel - (mobLevel * 0.3 * random.nextDouble());
      ItemStack his = MaterialUtil.buildMaterial(
          m, plugin.getCraftMatManager().getCraftMaterials().get(m), (int) materialLevel, quality);
      his.setAmount(1 + random.nextInt(2));

      dropItem(event.getLocation(), his, killer, false, null);
    }
    if (random.nextDouble() < dropMultiplier * socketDropChance) {
      SocketGem sg;
      if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
        sg = plugin.getSocketGemManager().getRandomSocketGemByLevel((int) mobLevel);
      } else {
        sg = plugin.getSocketGemManager().getRandomSocketGem(true, event.getDistance());
      }

      assert sg != null;
      ItemStack his = sg.toItemStack(1);
      dropItem(event.getLocation(), his, killer, sg.isBroadcast(), sg.isBroadcast() ? Color.GREEN : null);
    }
    if (plugin.getSettings().getBoolean("config.custom-enchanting", true)) {
      if (random.nextDouble() < dropMultiplier * tomeDropChance) {
        EnchantmentTome es = plugin.getEnchantTomeManager().getRandomEnchantTome(rarityMultiplier);
        assert es != null;
        ItemStack his = es.toItemStack(1);
        dropItem(event.getLocation(), his, killer, es.isBroadcast(), es.isBroadcast() ? Color.BLUE : null);
      }
      if (random.nextDouble() < dropMultiplier * enhancerDropChance) {
        dropItem(event.getLocation(), ArcaneEnhancer.get(), killer, true, Color.RED);
      }
      if (random.nextDouble() < dropMultiplier * purityDropChance) {
        dropItem(event.getLocation(), PurifyingScroll.get(), killer, false, null);
      }
    }
    if (random.nextDouble() < dropMultiplier * scrollDropChance) {
      UpgradeScroll us = plugin.getScrollManager().getRandomScroll();
      ItemStack stack = plugin.getScrollManager().buildItemStack(us);
      dropItem(event.getLocation(), stack, killer, us.isBroadcast(), us.isBroadcast() ? Color.DARK_GREEN : null);
    }
    if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.identity-tome", 0D)) {
      ItemStack his = new IdentityTome();
      dropItem(event.getLocation(), his, killer, false, null);
    }
    if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.custom-item", 0D)) {
      CustomItem ci;
      if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
        ci = plugin.getCustomItemManager().getRandomCustomItemByLevel((int) mobLevel);
      } else {
        ci = plugin.getCustomItemManager().getRandomCustomItem(true, event.getDistance());
      }

      ItemStack stack = ci.toItemStack(1);

      int qualityBonus = 1;
      if (ci.canBeQuality()) {
        double qualityChance = plugin.getSettings().getDouble("config.random-quality-chance", 0.1);
        double multiQualityChance = plugin.getSettings().getDouble("config.multi-quality-chance", 0.1);

        if (random.nextDouble() <= qualityChance) {
          while (random.nextDouble() <= multiQualityChance && qualityBonus < 5) {
            qualityBonus++;
          }
          upgradeItemQuality(stack, qualityBonus);
        }
      }
      boolean broadcast = ci.isBroadcast() || qualityBonus > 2;
      dropItem(event.getLocation(), stack, killer, broadcast, broadcast ? Color.GOLD : null);
    }
    if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.socket-extender", 0D)) {
      ItemStack his = new SocketExtender();
      dropItem(event.getLocation(), his, killer, true, Color.AQUA);
    }
    // NOTE: Drop bonus should not be applied to Unidentified Items!
    if (random.nextDouble() < dropMultiplier * plugin.getSettings().getDouble("config.drops.unidentified-item", 0D)) {
      Material m = Material.WOODEN_SWORD;
      ItemStack his;
      if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
        his = new UnidentifiedItem(m, (int) itemLevel);
      } else {
        his = new UnidentifiedItem(m, -1);
      }
      ItemMeta itemMeta = his.getItemMeta();
      assert itemMeta != null;
      itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
      his.setItemMeta(itemMeta);

      dropItem(event.getLocation(), his, null, false, null);
    }
  }

  private static void doUniqueDrops(UniqueLoot uniqueLoot, Location location, Player killer) {
    for (String gemString : uniqueLoot.getGemMap().keySet()) {
      if (uniqueLoot.getGemMap().get(gemString) > random.nextDouble()) {
        SocketGem gem = plugin.getSocketGemManager().getSocketGem(gemString);
        if (gem == null) {
          continue;
        }
        ItemStack his = gem.toItemStack(1);
        dropItem(location, his, killer, gem.isBroadcast(), gem.isBroadcast() ? Color.GREEN : null);
      }
    }
    for (String tomeString : uniqueLoot.getTomeMap().keySet()) {
      if (uniqueLoot.getTomeMap().get(tomeString) > random.nextDouble()) {
        EnchantmentTome tome = plugin.getEnchantTomeManager().getEnchantTome(tomeString);
        if (tome == null) {
          continue;
        }
        ItemStack his = tome.toItemStack(1);
        dropItem(location, his, killer, tome.isBroadcast(), tome.isBroadcast() ? Color.BLUE : null);
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
          ItemStack his = ci.toItemStack(1);
          dropItem(location, his, killer, ci.isBroadcast(), ci.isBroadcast() ? Color.GOLD : null);
          break;
        }
      }
    }
  }

  private static void upgradeItem(ItemStack his, int upgradeBonus) {
    int currentLevel = MaterialUtil.getUpgradeLevel(his);
    if (his.getType().getMaxDurability() <= 1) {
      int upgrade = (int) (Math.ceil((double) upgradeBonus / 3));
      int plusAmount = upgrade * 3;
      MaterialUtil.bumpItemPlus(his, currentLevel, upgrade, plusAmount);
    } else {
      MaterialUtil.bumpItemPlus(his, currentLevel, upgradeBonus, upgradeBonus);
    }
  }

  private static ItemStack upgradeItemQuality(ItemStack his, int upgradeBonus) {
    boolean succeed = false;
    List<String> lore = ItemStackExtensionsKt.getLore(his);
    for (int i = 0; i < lore.size(); i++) {
      String s = lore.get(i);
      String ss = ChatColor.stripColor(s);
      if (!ss.startsWith("+")) {
        continue;
      }
      succeed = true;
      String loreLev = CharMatcher.digit().or(CharMatcher.is('-')).retainFrom(ss);
      int loreLevel = NumberUtils.toInt(loreLev);
      lore.set(i, s.replace("+" + loreLevel, "+" + (loreLevel + upgradeBonus)));
      String qualityEnhanceName = plugin.getSettings().getString("language.quality." + upgradeBonus, "");
      String name = getFirstColor(Objects.requireNonNull(ItemStackExtensionsKt.getDisplayName(his))) +
          qualityEnhanceName + " " + ItemStackExtensionsKt.getDisplayName(his);
      ItemStackExtensionsKt.setDisplayName(his, name);
      break;
    }
    if (succeed) {
      ItemStackExtensionsKt.setLore(his, lore);
    }
    return his;
  }

  private static void dropItem(Location loc, ItemStack itemStack, Player looter, boolean broadcast, Color glowColor) {
    dropItem(loc, itemStack, looter, 0, broadcast, glowColor);
  }

  private static void dropItem(Location loc, ItemStack itemStack, Player looter, int ticksLived, boolean broadcast,
      Color glowColor) {
    Item drop = Objects.requireNonNull(loc.getWorld()).dropItemNaturally(loc, itemStack);
    if (glowColor != null) {
      GlowAPI.setGlowing(drop, true, looter);
      GlowAPI.setGlowing(drop, glowColor, looter);
    }
    if (ticksLived != 0) {
      drop.setTicksLived(ticksLived);
    }
    if (looter != null) {
      applyDropProtection(drop, looter.getUniqueId());
      if (broadcast) {
        broadcast(looter, itemStack, itemFoundFormat);
      }
    }
  }

  private static void applyDropProtection(Item drop, UUID owner) {
    drop.setOwner(owner);
    Bukkit.getScheduler().runTaskLater(plugin, () -> clearDropProtection(drop), 400L);
  }

  private static void clearDropProtection(Item drop) {
    if (drop != null) {
      drop.setOwner(null);
    }
  }

  private static boolean addSpecialStat(EntityType entityType, String worldName) {
    return (specialStatEntities.containsKey(entityType) && random.nextDouble() < specialStatEntities
        .get(entityType))
        || ((specialStatWorlds.containsKey(worldName)) && random.nextDouble() < specialStatWorlds
        .get(worldName));
  }

  public static Tier getTier(Player killer) {
    if (customizedTierChance < random.nextDouble()) {
      return plugin.getTierManager().getRandomTier();
    }
    List<Material> wornMaterials = getWornMaterials(killer);
    List<Tier> wornTiers = new ArrayList<>();
    for (Material m : wornMaterials) {
      Set<Tier> tiers = plugin.getItemGroupManager().getMaterialGroup(m);
      if (tiers != null) {
        wornTiers.addAll(tiers);
      }
    }
    if (wornTiers.isEmpty()) {
      return plugin.getTierManager().getRandomTier();
    }
    return wornTiers.get(random.nextIntRange(0, wornTiers.size()));
  }

  private static List<Material> getWornMaterials(Player player) {
    List<Material> materials = new ArrayList<>();
    for (ItemStack stack : player.getEquipment().getArmorContents()) {
      if (stack == null || stack.getType() == Material.AIR) {
        continue;
      }
      materials.add(stack.getType());
    }
    ItemStack handItem = player.getEquipment().getItemInMainHand();
    if (handItem.getType() != Material.AIR) {
      materials.add(handItem.getType());
    }
    ItemStack offItem = player.getEquipment().getItemInMainHand();
    if (offItem.getType() != Material.AIR) {
      materials.add(offItem.getType());
    }
    return materials;
  }
}
