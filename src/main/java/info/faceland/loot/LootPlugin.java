/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package info.faceland.loot;

import com.tealcube.minecraft.bukkit.facecore.logging.PluginLogger;
import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import com.tealcube.minecraft.bukkit.shade.objecthunter.exp4j.ExpressionBuilder;
import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.creatures.CreatureModBuilder;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.enchantments.EnchantmentTomeBuilder;
import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.items.CustomItemBuilder;
import info.faceland.loot.api.items.ItemBuilder;
import info.faceland.loot.api.managers.*;
import info.faceland.loot.api.math.Vec3;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.SocketGemBuilder;
import info.faceland.loot.api.sockets.effects.SocketEffect;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.api.tier.TierBuilder;
import info.faceland.loot.commands.LootCommand;
import info.faceland.loot.creatures.LootCreatureModBuilder;
import info.faceland.loot.data.ItemRarity;
import info.faceland.loot.data.ItemStat;
import info.faceland.loot.data.JunkItemData;
import info.faceland.loot.data.UniqueLoot;
import info.faceland.loot.enchantments.LootEnchantmentTomeBuilder;
import info.faceland.loot.groups.LootItemGroup;
import info.faceland.loot.io.SmartTextFile;
import info.faceland.loot.items.LootCustomItemBuilder;
import info.faceland.loot.items.LootItemBuilder;
import info.faceland.loot.listeners.DeconstructListener;
import info.faceland.loot.listeners.EnchantDegradeListener;
import info.faceland.loot.listeners.InteractListener;
import info.faceland.loot.listeners.LootDropListener;
import info.faceland.loot.listeners.StrifeListener;
import info.faceland.loot.listeners.anticheat.AnticheatListener;
import info.faceland.loot.listeners.crafting.CraftingListener;
import info.faceland.loot.listeners.sockets.CombinerListener;
import info.faceland.loot.listeners.sockets.SocketsListener;
import info.faceland.loot.listeners.EntityDeathListener;
import info.faceland.loot.managers.*;
import info.faceland.loot.recipe.EquipmentRecipeBuilder;
import info.faceland.loot.sockets.LootSocketGemBuilder;
import info.faceland.loot.sockets.effects.LootSocketPotionEffect;
import info.faceland.loot.tier.LootTierBuilder;
import info.faceland.strife.StrifePlugin;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.SmartYamlConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.HandlerList;

import se.ranzdo.bukkit.methodcommand.CommandHandler;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public final class LootPlugin extends FacePlugin {

  private PluginLogger debugPrinter;
  private EquipmentRecipeBuilder recipeBuilder;
  private VersionedSmartYamlConfiguration itemsYAML;
  private VersionedSmartYamlConfiguration statsYAML;
  private VersionedSmartYamlConfiguration rarityYAML;
  private VersionedSmartYamlConfiguration tierYAML;
  private VersionedSmartYamlConfiguration corestatsYAML;
  private VersionedSmartYamlConfiguration customItemsYAML;
  private VersionedSmartYamlConfiguration socketGemsYAML;
  private VersionedSmartYamlConfiguration languageYAML;
  private VersionedSmartYamlConfiguration configYAML;
  private VersionedSmartYamlConfiguration creaturesYAML;
  private VersionedSmartYamlConfiguration identifyingYAML;
  private VersionedSmartYamlConfiguration enchantmentTomesYAML;
  private VersionedSmartYamlConfiguration craftBasesYAML;
  private VersionedSmartYamlConfiguration craftMaterialsYAML;
  private VersionedSmartYamlConfiguration uniqueDropsYAML;
  private SmartYamlConfiguration chestsYAML;
  private MasterConfiguration settings;
  private ItemGroupManager itemGroupManager;
  private TierManager tierManager;
  private StatManager statManager;
  private RarityManager rarityManager;
  private NameManager nameManager;
  private CustomItemManager customItemManager;
  private SocketGemManager socketGemManager;
  private CreatureModManager creatureModManager;
  private EnchantmentTomeManager enchantmentStoneManager;
  private AnticheatManager anticheatManager;
  private ChestManager chestManager;
  private GemCacheManager gemCacheManager;
  private LootCraftBaseManager lootCraftBaseManager;
  private LootCraftMatManager lootCraftMatManager;
  private UniqueDropsManager uniqueDropsManager;

  private StrifePlugin strifePlugin;

  @Override
  public void enable() {
    debugPrinter = new PluginLogger(this);
    recipeBuilder = new EquipmentRecipeBuilder(this);
    recipeBuilder.setupAllRecipes();
    configYAML = defaultLoadConfig("config.yml");
    itemsYAML = defaultLoadConfig("items.yml");
    statsYAML = defaultLoadConfig("stats.yml");
    rarityYAML = defaultLoadConfig("rarity.yml");
    tierYAML = defaultLoadConfig("tier.yml");
    corestatsYAML = defaultLoadConfig("corestats.yml");
    customItemsYAML = defaultLoadConfig("customItems.yml");
    socketGemsYAML = defaultLoadConfig("socketGems.yml");
    languageYAML = defaultLoadConfig("language.yml");
    creaturesYAML = defaultLoadConfig("creatures.yml");
    identifyingYAML = defaultLoadConfig("identifying.yml");
    enchantmentTomesYAML = defaultLoadConfig("enchantmentTomes.yml");
    craftBasesYAML = defaultLoadConfig("craftBases.yml");
    craftMaterialsYAML = defaultLoadConfig("craftMaterials.yml");
    uniqueDropsYAML = defaultLoadConfig("strifeUniqueDrops.yml");
    chestsYAML = new SmartYamlConfiguration(new File(getDataFolder(), "chests.yml"));
    chestsYAML.load();

    settings = MasterConfiguration
        .loadFromFiles(corestatsYAML, languageYAML, configYAML, identifyingYAML);

    itemGroupManager = new LootItemGroupManager();
    tierManager = new LootTierManager();
    statManager = new LootStatManager();
    rarityManager = new LootRarityManager();
    nameManager = new LootNameManager();
    customItemManager = new LootCustomItemManager();
    socketGemManager = new LootSocketGemManager();
    creatureModManager = new LootCreatureModManager();
    enchantmentStoneManager = new LootEnchantmentTomeManager();
    anticheatManager = new LootAnticheatManager();
    chestManager = new LootChestManager();
    gemCacheManager = new LootGemCacheManager(this);
    lootCraftBaseManager = new LootCraftBaseManager();
    lootCraftMatManager = new LootCraftMatManager();
    uniqueDropsManager = new LootUniqueDropsManager();

    loadItemGroups();
    loadCraftBases();
    loadCraftMaterials();
    loadStats();
    loadRarities();
    loadTiers();
    loadNames();
    loadCustomItems();
    loadSocketGems();
    loadEnchantmentStones();
    loadCreatureMods();
    loadUniqueDrops();
    loadChests();

    strifePlugin = (StrifePlugin) Bukkit.getPluginManager().getPlugin("Strife");

    CommandHandler handler = new CommandHandler(this);
    handler.registerCommands(new LootCommand(this));
    Bukkit.getPluginManager().registerEvents(new EntityDeathListener(this), this);
    Bukkit.getPluginManager().registerEvents(new SocketsListener(this), this);
    Bukkit.getPluginManager().registerEvents(new CombinerListener(this), this);
    Bukkit.getPluginManager().registerEvents(new InteractListener(this), this);
    Bukkit.getPluginManager().registerEvents(new DeconstructListener(this), this);
    Bukkit.getPluginManager().registerEvents(new CraftingListener(this), this);
    Bukkit.getPluginManager().registerEvents(new AnticheatListener(this), this);
    Bukkit.getPluginManager().registerEvents(new EnchantDegradeListener(this), this);
    Bukkit.getPluginManager().registerEvents(new LootDropListener(this), this);

    if (strifePlugin != null) {
      Bukkit.getPluginManager().registerEvents(new StrifeListener(this), this);
    } else {
      System.out.println("Strife not detected. No load crit/evade gems!");
    }
    debug("v" + getDescription().getVersion() + " enabled");
  }

  private void loadChests() {
    for (Vec3 loc : getChestManager().getChestLocations()) {
      getChestManager().removeChestLocation(loc);
    }
    Set<Vec3> locs = new HashSet<>();
    List<String> locStrings = chestsYAML.getStringList("locs");
    for (String s : locStrings) {
      locs.add(Vec3.fromString(s));
    }
    for (Vec3 loc : locs) {
      getChestManager().addChestLocation(loc);
    }
    debug("Loaded chests: " + locs.size());
  }

  private void saveChests() {
    List<String> locs = new ArrayList<>();
    for (Vec3 loc : getChestManager().getChestLocations()) {
      locs.add(loc.toString());
      getChestManager().removeChestLocation(loc);
    }
    chestsYAML.set("locs", locs);
    chestsYAML.save();
  }

  @Override
  public void disable() {
    HandlerList.unregisterAll(this);

    saveChests();

    chestManager = null;
    anticheatManager = null;
    enchantmentStoneManager = null;
    creatureModManager = null;
    socketGemManager = null;
    customItemManager = null;
    nameManager = null;
    tierManager = null;
    statManager = null;
    rarityManager = null;
    itemGroupManager = null;
    gemCacheManager = null;
    lootCraftBaseManager = null;
    lootCraftMatManager = null;
    uniqueDropsManager = null;
    settings = null;
    identifyingYAML = null;
    creaturesYAML = null;
    configYAML = null;
    languageYAML = null;
    customItemsYAML = null;
    corestatsYAML = null;
    tierYAML = null;
    itemsYAML = null;
    statsYAML = null;
    rarityYAML = null;
    debugPrinter = null;
    strifePlugin = null;
  }

  public void debug(String... messages) {
    debug(Level.INFO, messages);
  }

  public void debug(Level level, String... messages) {
    if (debugPrinter != null && (settings == null || settings.getBoolean("config.debug", false))) {
      debugPrinter.log(level, Arrays.asList(messages));
    }
  }

  private void loadEnchantmentStones() {
    for (EnchantmentTome es : getEnchantmentStoneManager().getEnchantmentStones()) {
      getEnchantmentStoneManager().removeEnchantmentStone(es.getName());
    }
    Set<EnchantmentTome> stones = new HashSet<>();
    List<String> loadedStones = new ArrayList<>();
    for (String key : enchantmentTomesYAML.getKeys(false)) {
      if (!enchantmentTomesYAML.isConfigurationSection(key)) {
        continue;
      }
      ConfigurationSection cs = enchantmentTomesYAML.getConfigurationSection(key);
      EnchantmentTomeBuilder builder = getNewEnchantmentStoneBuilder(key);
      builder.withDescription(cs.getString("description"));
      builder.withWeight(cs.getDouble("weight"));
      builder.withDistanceWeight(cs.getDouble("distance-weight"));
      builder.withLore(cs.getStringList("lore"));
      builder.withStat(cs.getString("stat", ""));
      builder.withBar(cs.getBoolean("enable-bar", true));
      builder.withBroadcast(cs.getBoolean("broadcast", false));
      List<ItemGroup> groups = new ArrayList<>();
      for (String groop : cs.getStringList("item-groups")) {
        ItemGroup g = itemGroupManager.getItemGroup(groop);
        if (g == null) {
          continue;
        }
        groups.add(g);
      }
      Map<Enchantment, Integer> enchantments = new HashMap<>();
      if (cs.isConfigurationSection("enchantments")) {
        ConfigurationSection enchCS = cs.getConfigurationSection("enchantments");
        for (String eKey : enchCS.getKeys(false)) {
          Enchantment ench = Enchantment.getByName(eKey);
          if (ench == null) {
            continue;
          }
          int i = enchCS.getInt(eKey);
          enchantments.put(ench, i);
        }
      }
      builder.withEnchantments(enchantments);
      builder.withItemGroups(groups);
      EnchantmentTome stone = builder.build();
      stones.add(stone);
      loadedStones.add(stone.getName());
    }
    for (EnchantmentTome es : stones) {
      getEnchantmentStoneManager().addEnchantmentStone(es);
    }
    debug("Loaded enchantment stones: " + loadedStones.toString());
  }

  private void loadUniqueDrops() {
    for (String key : uniqueDropsYAML.getKeys(false)) {
      if (!uniqueDropsYAML.isConfigurationSection(key)) {
        continue;
      }
      ConfigurationSection cs = uniqueDropsYAML.getConfigurationSection(key);
      UniqueLoot uniqueLoot = new UniqueLoot();
      uniqueLoot.setQuantityMultiplier(cs.getDouble("quantity-multiplier", 1D));
      uniqueLoot.setQuantityMultiplier(cs.getDouble("rarity-multiplier", 1D));
      if (cs.getConfigurationSection("gem-drops") != null) {
        for (String g : cs.getConfigurationSection("gem-drops").getKeys(false)) {
          uniqueLoot.getGemMap().put(g, cs.getConfigurationSection("gem-drops").getDouble(g));
        }
      }
      if (cs.getConfigurationSection("tome-drops") != null) {
        for (String t : cs.getConfigurationSection("tome-drops").getKeys(false)) {
          uniqueLoot.getTomeMap().put(t, cs.getConfigurationSection("tome-drops").getDouble(t));
        }
      }
      ConfigurationSection cds = cs.getConfigurationSection("custom-drops");
      if (cds != null) {
        for (String tableName : cds.getKeys(false)) {
          Map<String, Double> tableMap = new HashMap<>();
          for (String customName : cds.getConfigurationSection(tableName).getKeys(false)) {
            tableMap.put(customName, cds.getConfigurationSection(tableName).getDouble(customName));
          }
          uniqueLoot.getCustomItemMap().put(tableName, tableMap);
        }
      }
      uniqueDropsManager.addData(key, uniqueLoot);
    }
  }

  private void loadCreatureMods() {
    for (CreatureMod cm : getCreatureModManager().getCreatureMods()) {
      getCreatureModManager().removeCreatureMod(cm.getEntityType());
    }
    Set<CreatureMod> mods = new HashSet<>();
    List<String> loadedMods = new ArrayList<>();
    for (String key : creaturesYAML.getKeys(false)) {
      if (!creaturesYAML.isConfigurationSection(key)) {
        continue;
      }
      ConfigurationSection cs = creaturesYAML.getConfigurationSection(key);
      CreatureModBuilder builder = getNewCreatureModBuilder(EntityType.valueOf(key));
      for (String fieldKey : cs.getKeys(false)) {
        if (fieldKey.equals("custom-items")) {
          Map<CustomItem, Double> map = new HashMap<>();
          for (String k : cs.getConfigurationSection("custom-items").getKeys(false)) {
            if (!cs.isConfigurationSection("custom-items." + k)) {
              continue;
            }
            CustomItem ci = customItemManager.getCustomItem(k);
            if (ci == null) {
              continue;
            }
            map.put(ci, cs.getDouble("custom-items." + k));
          }
          builder.withCustomItemMults(map);
        } else if (fieldKey.equals("experience")) {
          String expStr = cs.getString("experience", "");
          if (StringUtils.isBlank(expStr)) {
            builder.withExpression(null);
          } else {
            builder.withExpression(new ExpressionBuilder(expStr).variables("LEVEL").build());
          }
        } else if (fieldKey.equals("socket-gems")) {
          Map<SocketGem, Double> map = new HashMap<>();
          for (String k : cs.getConfigurationSection("socket-gems").getKeys(false)) {
            if (!cs.isConfigurationSection("socket-gems." + k)) {
              continue;
            }
            SocketGem sg = socketGemManager.getSocketGem(k);
            if (sg == null) {
              continue;
            }
            map.put(sg, cs.getDouble("socket-gems." + k));
          }
          builder.withSocketGemMults(map);
        } else if (fieldKey.equals("tiers")) {
          Map<Tier, Double> map = new HashMap<>();
          for (String k : cs.getConfigurationSection("tiers").getKeys(false)) {
            if (!cs.isConfigurationSection("tiers." + k)) {
              continue;
            }
            Tier t = tierManager.getTier(k);
            if (t == null) {
              continue;
            }
            map.put(t, cs.getDouble("tiers." + k));
          }
          builder.withTierMults(map);
        } else if (fieldKey.equals("enchantment-stone")) {
          Map<EnchantmentTome, Double> map = new HashMap<>();
          for (String k : cs.getConfigurationSection("enchantment-stones").getKeys(false)) {
            if (!cs.isConfigurationSection("enchantment-stones." + k)) {
              continue;
            }
            EnchantmentTome es = enchantmentStoneManager.getEnchantmentStone(k);
            if (es == null) {
              continue;
            }
            map.put(es, cs.getDouble("enchantment-stones." + k));
          }
          builder.withEnchantmentStoneMults(map);
        } else if (fieldKey.equals("drops")) {
          Map<JunkItemData, Double> map = new HashMap<>();
          for (String k : cs.getConfigurationSection("drops").getKeys(false)) {
            Material material;
            try {
              material = Material.valueOf(cs.getString("drops." + k + ".material"));
            } catch (Exception e) {
              getLogger().warning("Invalid material in creature junk drops!");
              continue;
            }
            int min = cs.getInt("drops." + k + ".min-amount");
            int max = cs.getInt("drops." + k + ".max-amount");
            double chance = cs.getDouble("drops." + k + ".chance");
            JunkItemData jid = new JunkItemData(material, min, max);
            map.put(jid, chance);
          }
          builder.withJunkMults(map);
        }
      }
      CreatureMod mod = builder.build();
      mods.add(mod);
      loadedMods.add(mod.getEntityType().name());
    }
    for (CreatureMod cm : mods) {
      creatureModManager.addCreatureMod(cm);
    }
    System.out.println("Loaded creature mods: " + loadedMods.toString());
  }

  private void loadSocketGems() {
    for (SocketGem sg : getSocketGemManager().getSocketGems()) {
      getSocketGemManager().removeSocketGem(sg.getName());
    }
    Set<SocketGem> gems = new HashSet<>();
    List<String> loadedSocketGems = new ArrayList<>();
    for (String key : socketGemsYAML.getKeys(false)) {
      if (!socketGemsYAML.isConfigurationSection(key)) {
        continue;
      }
      ConfigurationSection cs = socketGemsYAML.getConfigurationSection(key);
      SocketGemBuilder builder = getNewSocketGemBuilder(key);
      builder.withPrefix(cs.getString("prefix"));
      builder.withSuffix(cs.getString("suffix"));
      builder.withLore(cs.getStringList("lore"));
      builder.withWeight(cs.getDouble("weight"));
      builder.withDistanceWeight(cs.getDouble("distance-weight"));
      builder.withBonusWeight(cs.getDouble("bonus-weight"));
      builder.withWeightPerLevel(cs.getDouble("weight-per-level"));
      List<SocketEffect> effects = new ArrayList<>();
      for (String eff : cs.getStringList("effects")) {
        effects.add(LootSocketPotionEffect.parseString(eff));
      }
      builder.withSocketEffects(effects);
      List<ItemGroup> groups = new ArrayList<>();
      for (String groop : cs.getStringList("item-groups")) {
        ItemGroup g = itemGroupManager.getItemGroup(groop);
        if (g == null) {
          continue;
        }
        groups.add(g);
      }
      builder.withItemGroups(groups);
      builder.withBroadcast(cs.getBoolean("broadcast"));
      builder.withTriggerable(cs.getBoolean("triggerable"));
      builder.withTriggerText(cs.getString("trigger-text"));
      builder.withGemType(SocketGem.GemType.fromName(cs.getString("gem-type")));
      SocketGem gem = builder.build();
      gems.add(gem);
      loadedSocketGems.add(gem.getName());
    }
    for (SocketGem sg : gems) {
      getSocketGemManager().addSocketGem(sg);
    }
    debug("Loaded socket gems: " + loadedSocketGems.toString());
  }

  private void loadCustomItems() {
    for (CustomItem ci : getCustomItemManager().getCustomItems()) {
      getCustomItemManager().removeCustomItem(ci.getName());
    }
    Set<CustomItem> customItems = new HashSet<>();
    List<String> loaded = new ArrayList<>();
    for (String key : customItemsYAML.getKeys(false)) {
      if (!customItemsYAML.isConfigurationSection(key)) {
        continue;
      }
      ConfigurationSection cs = customItemsYAML.getConfigurationSection(key);
      CustomItemBuilder builder = getNewCustomItemBuilder(key);
      builder.withMaterial(Material.getMaterial(cs.getString("material")));
      builder.withDisplayName(cs.getString("display-name"));
      builder.withLore(cs.getStringList("lore"));
      builder.withWeight(cs.getDouble("weight"));
      builder.withDistanceWeight(cs.getDouble("distance-weight"));
      builder.withLevelBase(cs.getInt("level-base"));
      builder.withLevelRange(cs.getInt("level-range"));
      builder.withBroadcast(cs.getBoolean("broadcast"));
      builder.withQuality(cs.getBoolean("can-be-quality-enhanced"));
      CustomItem ci = builder.build();
      customItems.add(ci);
      loaded.add(ci.getName());
    }
    for (CustomItem ci : customItems) {
      getCustomItemManager().addCustomItem(ci);
    }
    debug("Loaded custom items: " + loaded.toString());
  }

  private void loadNames() {
    for (String s : getNameManager().getPrefixes()) {
      getNameManager().removePrefix(s);
    }
    for (String s : getNameManager().getSuffixes()) {
      getNameManager().removeSuffix(s);
    }

    File prefixFile = new File(getDataFolder(), "prefix.txt");
    File suffixFile = new File(getDataFolder(), "suffix.txt");

    SmartTextFile.writeToFile(getResource("prefix.txt"), prefixFile, true);
    SmartTextFile.writeToFile(getResource("suffix.txt"), suffixFile, true);

    SmartTextFile smartPrefixFile = new SmartTextFile(prefixFile);
    SmartTextFile smartSuffixFile = new SmartTextFile(suffixFile);

    for (String s : smartPrefixFile.read()) {
      getNameManager().addPrefix(s);
    }
    for (String s : smartSuffixFile.read()) {
      getNameManager().addSuffix(s);
    }

    debug("Loaded prefixes: " + getNameManager().getPrefixes().size(),
        "Loaded suffixes: " + getNameManager()
            .getSuffixes().size());
  }

  private void loadItemGroups() {
    for (ItemGroup ig : getItemGroupManager().getItemGroups()) {
      getItemGroupManager().removeItemGroup(ig.getName());
    }
    Set<ItemGroup> itemGroups = new HashSet<>();
    List<String> loadedItemGroups = new ArrayList<>();
    for (String key : itemsYAML.getKeys(false)) {
      if (!itemsYAML.isList(key)) {
        continue;
      }
      List<String> list = itemsYAML.getStringList(key);
      ItemGroup ig = new LootItemGroup(key, false);
      for (String s : list) {
        Material m = Material.getMaterial(s);
        if (m == Material.AIR) {
          continue;
        }
        ig.addMaterial(m);
      }
      itemGroups.add(ig);
      loadedItemGroups.add(key);
    }
    for (ItemGroup ig : itemGroups) {
      getItemGroupManager().addItemGroup(ig);
    }
    debug("Loaded item groups: " + loadedItemGroups.toString());
  }

  private void loadCraftBases() {
    Map<Material, String> craftBases = new HashMap<>();
    for (String key : craftBasesYAML.getKeys(false)) {
      if (!craftBasesYAML.isString(key)) {
        continue;
      }
      String string = craftBasesYAML.getString(key);
      craftBases.put(Material.valueOf(key), string);
    }
    for (Material mat : craftBases.keySet()) {
      getCraftBaseManager().addCraftBase(mat, craftBases.get(mat));
    }
    debug("Loaded item groups: " + getCraftBaseManager().getCraftBases());
  }

  private void loadCraftMaterials() {
    Map<Material, String> craftMaterials = new HashMap<>();
    for (String key : craftMaterialsYAML.getKeys(false)) {
      if (!craftMaterialsYAML.isString(key)) {
        continue;
      }
      String string = craftMaterialsYAML.getString(key);
      craftMaterials.put(Material.valueOf(key), string);
    }
    for (Material mat : craftMaterials.keySet()) {
      getCraftMatManager().addCraftMaterial(mat, craftMaterials.get(mat));
    }
    debug("Loaded item groups: " + getCraftBaseManager().getCraftBases());
  }

  private void loadRarities() {
    for (String rarityName : getRarityManager().getLoadedRarities().keySet()) {
      getStatManager().removeStat(rarityName);
    }
    for (String key : rarityYAML.getKeys(false)) {
      if (!rarityYAML.isConfigurationSection(key)) {
        continue;
      }
      ConfigurationSection cs = rarityYAML.getConfigurationSection(key);
      ItemRarity rarity = new ItemRarity();
      rarity.setBroadcast(cs.getBoolean("broadcast"));
      rarity.setName(cs.getString("name"));
      rarity.setColor(ChatColor.valueOf(cs.getString("color")));
      rarity.setWeight(cs.getDouble("weight"));
      rarity.setIdWeight(cs.getDouble("id-weight"));
      rarity.setPower(cs.getDouble("power"));
      rarity.setMinimumBonusStats(cs.getInt("min-bonus-stats"));
      rarity.setMaximumBonusStats(cs.getInt("max-bonus-stats") + 1);
      rarity.setEnchantments(cs.getInt("enchantments"));
      rarity.setMinimumSockets(cs.getInt("min-sockets"));
      rarity.setMaximumSockets(cs.getInt("max-sockets") + 1);
      rarity.setExtenderSlots(cs.getInt("extend-slots"));
      getRarityManager().addRarity(key, rarity);
    }
    debug("Loaded rarities: " + getRarityManager().getLoadedRarities().toString());
  }

  private void loadStats() {
    for (String statName : getStatManager().getLoadedStats().keySet()) {
      getStatManager().removeStat(statName);
    }
    for (String key : statsYAML.getKeys(false)) {
      if (!statsYAML.isConfigurationSection(key)) {
        continue;
      }
      ConfigurationSection cs = statsYAML.getConfigurationSection(key);
      ItemStat stat = new ItemStat();
      stat.setMinBaseValue(cs.getDouble("min-base-value"));
      stat.setMaxBaseValue(cs.getDouble("max-base-value"));
      stat.setPerLevelIncrease(cs.getDouble("per-level-increase"));
      stat.setPerLevelMultiplier(cs.getDouble("per-level-multiplier"));
      stat.setPerRarityIncrease(cs.getDouble("per-rarity-increase"));
      stat.setPerRarityMultiplier(cs.getDouble("per-rarity-multiplier"));
      stat.setStatString(cs.getString("stat-string"));
      stat.setPerfectStatString(cs.getString("perfect-stat-string"));
      getStatManager().addStat(key, stat);
    }
    debug("Loaded stats: " + getStatManager().getLoadedStats().keySet().toString());
  }

  private void loadTiers() {
    for (Tier t : getTierManager().getLoadedTiers()) {
      getTierManager().removeTier(t.getName());
    }
    Set<Tier> tiers = new HashSet<>();
    List<String> loadedTiers = new ArrayList<>();
    for (String key : tierYAML.getKeys(false)) {
      if (!tierYAML.isConfigurationSection(key)) {
        continue;
      }
      ConfigurationSection cs = tierYAML.getConfigurationSection(key);
      TierBuilder builder = getNewTierBuilder(key);
      builder.withName(cs.getString("tier-name"));
      builder.withLevelRequirement(cs.getBoolean("level-req"));
      builder.withPrimaryStat(getStatManager().getLoadedStats().get(cs.getString("primary-stat")));
      List<ItemStat> secondaryStats = new ArrayList<>();
      for (String statName : cs.getStringList("secondary-stats")) {
        secondaryStats.add(getStatManager().getStat(statName));
      }
      builder.withSecondaryStats(secondaryStats);
      List<ItemStat> bonusStats = new ArrayList<>();
      for (String statName : cs.getStringList("bonus-stats")) {
        bonusStats.add(getStatManager().getStat(statName));
      }
      builder.withBonusStats(bonusStats);
      builder.withSpawnWeight(cs.getDouble("spawn-weight"));
      builder.withIdentifyWeight(cs.getDouble("identify-weight"));
      List<String> sl = cs.getStringList("item-groups");
      Set<ItemGroup> itemGroups = new HashSet<>();
      for (String s : sl) {
        ItemGroup ig;
        if (s.startsWith("-")) {
          ig = getItemGroupManager().getItemGroup(s.substring(1));
          if (ig == null) {
            continue;
          }
          itemGroups.add(!ig.isInverse() ? ig.getInverse() : ig);
        } else {
          ig = getItemGroupManager().getItemGroup(s);
          if (ig == null) {
            continue;
          }
          itemGroups.add(!ig.isInverse() ? ig : ig.getInverse());
        }
      }
      builder.withItemGroups(itemGroups);
      Tier t = builder.build();
      loadedTiers.add(t.getName());
      tiers.add(t);
    }
    debug("Loaded tiers: " + loadedTiers.toString());
    for (Tier t : tiers) {
      getTierManager().addTier(t);
    }
  }

  private VersionedSmartYamlConfiguration defaultLoadConfig(String fileName) {
    VersionedSmartYamlConfiguration config = new VersionedSmartYamlConfiguration(
        new File(getDataFolder(), fileName), getResource(fileName),
        VersionedConfiguration.VersionUpdateType.BACKUP_NO_UPDATE);
    if (config.update()) {
      getLogger().info("Updating " + fileName);
      debug("Updating " + fileName);
    }
    return config;
  }

  public TierBuilder getNewTierBuilder(String name) {
    return new LootTierBuilder(name);
  }

  public ItemBuilder getNewItemBuilder() {
    return new LootItemBuilder(this);
  }

  public CustomItemBuilder getNewCustomItemBuilder(String name) {
    return new LootCustomItemBuilder(name);
  }

  public SocketGemBuilder getNewSocketGemBuilder(String name) {
    return new LootSocketGemBuilder(name);
  }

  public CreatureModBuilder getNewCreatureModBuilder(EntityType entityType) {
    return new LootCreatureModBuilder(entityType);
  }

  public EnchantmentTomeBuilder getNewEnchantmentStoneBuilder(String name) {
    return new LootEnchantmentTomeBuilder(name);
  }

  public TierManager getTierManager() {
    return tierManager;
  }

  public StatManager getStatManager() {
    return statManager;
  }

  public RarityManager getRarityManager() {
    return rarityManager;
  }

  public ItemGroupManager getItemGroupManager() {
    return itemGroupManager;
  }

  public NameManager getNameManager() {
    return nameManager;
  }

  public MasterConfiguration getSettings() {
    return settings;
  }

  public CustomItemManager getCustomItemManager() {
    return customItemManager;
  }

  public SocketGemManager getSocketGemManager() {
    return socketGemManager;
  }

  public CreatureModManager getCreatureModManager() {
    return creatureModManager;
  }

  public EnchantmentTomeManager getEnchantmentStoneManager() {
    return enchantmentStoneManager;
  }

  public AnticheatManager getAnticheatManager() {
    return anticheatManager;
  }

  public ChestManager getChestManager() {
    return chestManager;
  }

  public GemCacheManager getGemCacheManager() {
    return gemCacheManager;
  }

  public LootCraftBaseManager getCraftBaseManager() {
    return lootCraftBaseManager;
  }

  public LootCraftMatManager getCraftMatManager() {
    return lootCraftMatManager;
  }

  public UniqueDropsManager getUniqueDropsManager() {
    return uniqueDropsManager;
  }

  public StrifePlugin getStrifePlugin() {
    return strifePlugin;
  }
}
