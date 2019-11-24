package info.faceland.loot.listeners;

import static info.faceland.loot.utils.inventory.InventoryUtil.broadcast;
import static info.faceland.loot.utils.inventory.InventoryUtil.getFirstColor;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.ItemGenerationReason;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.data.BuiltItem;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class LootDropListener implements Listener {

  private final LootPlugin plugin;
  private final LootRandom random;
  private final String itemFoundFormat;
  private final Map<EntityType, Double> specialStatEntities;
  private final Map<String, Double> specialStatWorlds;

  private final double customizedTierChance;

  private final double normalDropChance;
  private final double socketDropChance;
  private final double tomeDropChance;

  public LootDropListener(LootPlugin plugin) {
    this.plugin = plugin;
    random = new LootRandom();
    itemFoundFormat = plugin.getSettings().getString("language.broadcast.found-item", "");
    specialStatEntities = plugin.fetchSpecialStatEntities();
    specialStatWorlds = plugin.fetchSpecialStatWorlds();

    customizedTierChance = plugin.getSettings()
        .getDouble("config.drops.customized-tier-chance", 0D);

    normalDropChance = plugin.getSettings().getDouble("config.drops.normal-drop", 0D);
    socketDropChance = plugin.getSettings().getDouble("config.drops.socket-gem", 0D);
    tomeDropChance = plugin.getSettings().getDouble("config.drops.enchant-gem", 0D);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onLootDrop(LootDropEvent event) {
    Player killer = Bukkit.getPlayer(event.getLooterUUID());
    if (killer == null) {
      return;
    }
    double dropMultiplier = event.getQuantityMultiplier();
    double rarityMultiplier = event.getQualityMultiplier();
    int mobLevel = event.getMonsterLevel();

    if (StringUtils.isNotBlank(event.getUniqueEntity())) {
      if (plugin.getUniqueDropsManager().getData(event.getUniqueEntity()) != null) {
        UniqueLoot loot = plugin.getUniqueDropsManager().getData(event.getUniqueEntity());
        dropMultiplier *= loot.getQuantityMultiplier();
        rarityMultiplier *= loot.getQualityMultiplier();
        doUniqueDrops(loot, event.getLocation(), killer);
      }
    }

    EntityType entityType = event.getEntity().getType();
    String worldName = event.getLocation().getWorld().getName();
    boolean specialStat = addSpecialStat(entityType, worldName);

    if (random.nextDouble() < dropMultiplier * normalDropChance) {
      Tier tier = getTier(killer);
      ItemRarity rarity;

      if (rarityMultiplier == 1D) {
        rarity = plugin.getRarityManager().getRandomRarity();
      } else {
        rarity = plugin.getRarityManager().getRandomRarityWithBonus(rarityMultiplier);
      }

      BuiltItem builtItem = plugin.getNewItemBuilder()
          .withTier(tier)
          .withRarity(rarity)
          .withLevel(Math.max(1, Math.min(mobLevel - 2 + random.nextIntRange(0, 5), 100)))
          .withItemGenerationReason(ItemGenerationReason.MONSTER)
          .withSpecialStat(specialStat)
          .build();

      ItemStack tierItem = builtItem.getStack();

      int qualityBonus = 1;
      double qualityChance = plugin.getSettings().getDouble("config.random-quality-chance", 0.1);
      double multiQualityChance = plugin.getSettings()
          .getDouble("config.multi-quality-chance", 0.1);

      if (random.nextDouble() <= qualityChance) {
        while (random.nextDouble() <= multiQualityChance && qualityBonus < 5) {
          qualityBonus++;
        }
        upgradeItemQuality(tierItem, qualityBonus);
      }

      int upgradeBonus = 1;
      double upgradeChance = plugin.getSettings().getDouble("config.random-upgrade-chance", 0.1);
      double multiUpgradeChance = plugin.getSettings()
          .getDouble("config.multi-upgrade-chance", 0.1);

      if (random.nextDouble() <= upgradeChance) {
        while (random.nextDouble() <= multiUpgradeChance && upgradeBonus < 9) {
          upgradeBonus++;
        }
        upgradeItem(tierItem, upgradeBonus);
      }

      boolean broadcast = rarity.isBroadcast() || upgradeBonus > 4 || qualityBonus > 2;
      dropItem(event.getLocation(), tierItem, killer, builtItem.getTicksLived(), broadcast);
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
      ItemStack his = MaterialUtil.buildMaterial(
          m, plugin.getCraftMatManager().getCraftMaterials().get(m), (int) materialLevel, quality);
      his.setAmount(1 + random.nextInt(2));

      dropItem(event.getLocation(), his, killer, false);
    }
    if (random.nextDouble() < dropMultiplier * socketDropChance) {
      SocketGem sg;
      if (plugin.getSettings().getBoolean("config.beast.beast-mode-activate", false)) {
        sg = plugin.getSocketGemManager().getRandomSocketGemByLevel(mobLevel);
      } else {
        sg = plugin.getSocketGemManager().getRandomSocketGem(true, event.getDistance());
      }

      ItemStack his = sg.toItemStack(1);
      dropItem(event.getLocation(), his, killer, sg.isBroadcast());
    }
    if (plugin.getSettings().getBoolean("config.custom-enchanting", true)) {
      if (random.nextDouble() < dropMultiplier * tomeDropChance) {
        EnchantmentTome es = plugin.getEnchantmentStoneManager()
            .getRandomEnchantmentStone(true, event.getDistance());
        ItemStack his = es.toItemStack(1);
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
      ItemStack his = new IdentityTome();
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

      ItemStack stack = ci.toItemStack(1);

      int qualityBonus = 1;
      if (ci.canBeQuality()) {
        double qualityChance = plugin.getSettings().getDouble("config.random-quality-chance", 0.1);
        double multiQualityChance = plugin.getSettings()
            .getDouble("config.multi-quality-chance", 0.1);

        if (random.nextDouble() <= qualityChance) {
          while (random.nextDouble() <= multiQualityChance && qualityBonus < 5) {
            qualityBonus++;
          }
          stack = upgradeItemQuality(stack, qualityBonus);
        }
      }
      boolean broadcast = ci.isBroadcast() || qualityBonus > 2;
      dropItem(event.getLocation(), stack, killer, broadcast);
    }
    if (random.nextDouble() < plugin.getSettings().getDouble("config.drops.socket-extender", 0D)) {
      ItemStack his = new SocketExtender();
      dropItem(event.getLocation(), his, killer, true);
    }
    // NOTE: Drop bonus should not be applied to Unidentified Items!
    if (random.nextDouble() < dropMultiplier * plugin.getSettings()
        .getDouble("config.drops.unidentified-item", 0D)) {
      Material m = Material.WOODEN_SWORD;
      ItemStack his;
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
        ItemStack his = gem.toItemStack(1);
        dropItem(location, his, killer, gem.isBroadcast());
      }
    }
    for (String tomeString : uniqueLoot.getTomeMap().keySet()) {
      if (uniqueLoot.getTomeMap().get(tomeString) > random.nextDouble()) {
        EnchantmentTome tome = plugin.getEnchantmentStoneManager().getEnchantmentStone(tomeString);
        if (tome == null) {
          continue;
        }
        ItemStack his = tome.toItemStack(1);
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
          ItemStack his = ci.toItemStack(1);
          dropItem(location, his, killer, ci.isBroadcast());
          break;
        }
      }
    }
  }

  private ItemStack upgradeItem(ItemStack his, int upgradeBonus) {
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
      String name = getFirstColor(ItemStackExtensionsKt.getDisplayName(his)) +
          ("+" + upgradeBonus) + " " + ItemStackExtensionsKt.getDisplayName(his);
      ItemStackExtensionsKt.setDisplayName(his, name);
      break;
    }
    if (succeed) {
      ItemStackExtensionsKt.setLore(his, lore);
      if (upgradeBonus > 6) {
        his.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        ItemStackExtensionsKt.addItemFlags(his, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
      }
    }
    return his;
  }

  private ItemStack upgradeItemQuality(ItemStack his, int upgradeBonus) {
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
      String qualityEnhanceName = plugin.getSettings()
          .getString("language.quality." + upgradeBonus, "");
      String name = getFirstColor(ItemStackExtensionsKt.getDisplayName(his)) +
          qualityEnhanceName + " " + ItemStackExtensionsKt.getDisplayName(his);
      ItemStackExtensionsKt.setDisplayName(his, name);
      break;
    }
    if (succeed) {
      ItemStackExtensionsKt.setLore(his, lore);
    }
    return his;
  }

  private void dropItem(Location loc, ItemStack itemStack, Player looter, boolean broadcast) {
    dropItem(loc, itemStack, looter, 0, broadcast);
  }

  private void dropItem(Location loc, ItemStack itemStack, Player looter, int ticksLived,
      boolean broadcast) {
    Item drop = Objects.requireNonNull(loc.getWorld()).dropItemNaturally(loc, itemStack);
    if (ticksLived != 0) {
      drop.setTicksLived(ticksLived);
    }
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

  private boolean addSpecialStat(EntityType entityType, String worldName) {
    return (specialStatEntities.containsKey(entityType) && random.nextDouble() < specialStatEntities
        .get(entityType))
        || ((specialStatWorlds.containsKey(worldName)) && random.nextDouble() < specialStatWorlds
        .get(worldName));
  }

  private Tier getTier(Player killer) {
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

  private List<Material> getWornMaterials(Player player) {
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
