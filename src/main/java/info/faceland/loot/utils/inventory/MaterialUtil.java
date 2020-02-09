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
package info.faceland.loot.utils.inventory;

import static com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils.sendMessage;
import static info.faceland.loot.utils.inventory.InventoryUtil.broadcast;
import static info.faceland.loot.utils.inventory.InventoryUtil.getFirstColor;
import static org.bukkit.ChatColor.stripColor;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.data.ItemStat;
import info.faceland.loot.enchantments.EnchantmentTome;
import info.faceland.loot.items.prefabs.ShardOfFailure;
import info.faceland.loot.data.UpgradeScroll;
import info.faceland.loot.items.prefabs.SocketExtender;
import info.faceland.loot.math.LootRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import land.face.strife.StrifePlugin;
import land.face.strife.data.champion.LifeSkillType;
import land.face.strife.util.PlayerDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public final class MaterialUtil {

  private static final double BONUS_ESS_MULT = 0.21;
  private static final double TOOL_QUALITY_ESS_MULT = 0.06;
  private static final double MIN_BONUS_ESS_MULT = 0.08;
  private static final double BASE_ESSENCE_MULT = 0.65;
  private static final LootRandom random = new LootRandom();

  private static String upgradeFailureMsg;
  private static String upgradeItemDestroyMsg;
  private static String upgradeItemDestroyBroadcast;
  private static String upgradeItemDamageMsg;
  private static String upgradeSuccessMsg;
  private static String upgradeSuccessBroadcast;
  private static String enchantFailureMsg;
  private static String enchantTagNeededMsg;
  private static String enchantPointlessMsg;
  private static String enchantSuccessMsg;
  private static String extendSuccessMsg;
  private static String extendFailMsg;
  private static boolean enchantmentsStack;

  public static final String FAILURE_BONUS = ChatColor.RED + "Failure Bonus";

  public static void refreshConfig() {
    upgradeFailureMsg = LootPlugin.getInstance().getSettings()
        .getString("language.upgrade.failure", "");
    upgradeItemDestroyMsg = LootPlugin.getInstance().getSettings()
        .getString("language.upgrade.destroyed", "");
    upgradeItemDestroyBroadcast = LootPlugin.getInstance().getSettings()
        .getString("language.broadcast.destroyed-item");
    upgradeItemDamageMsg = LootPlugin.getInstance().getSettings()
        .getString("language.upgrade.damaged", "");
    upgradeSuccessMsg = LootPlugin.getInstance().getSettings()
        .getString("language.upgrade.success", "");
    upgradeSuccessBroadcast = LootPlugin.getInstance().getSettings()
        .getString("language.broadcast.upgraded-item");
    enchantFailureMsg = LootPlugin.getInstance().getSettings()
        .getString("language.enchant.failure", "");
    enchantTagNeededMsg = LootPlugin.getInstance().getSettings()
        .getString("language.enchant.needs-enchantable", "");
    enchantPointlessMsg = LootPlugin.getInstance().getSettings()
        .getString("language.enchant.pointless", "");
    enchantSuccessMsg = LootPlugin.getInstance().getSettings()
        .getString("language.enchant.success", "");
    enchantmentsStack = LootPlugin.getInstance().getSettings()
        .getBoolean("config.enchantments-stack", true);
    extendSuccessMsg = LootPlugin.getInstance().getSettings()
        .getString("language.extend.success", "");
    extendFailMsg = LootPlugin.getInstance().getSettings()
        .getString("language.extend.fail", "");
  }

  public static double getSuccessChance(Player player, int targetPlus, ItemStack scrollStack,
      UpgradeScroll scroll) {
    double success = scroll.getBaseSuccess();
    success -= scroll.getFlatDecay() * targetPlus;
    success *= 1 - (scroll.getPercentDecay() * targetPlus);
    success = Math.pow(success, scroll.getExponent());
    List<String> scrollLore = ItemStackExtensionsKt.getLore(scrollStack);
    for (String s : scrollLore) {
      if (ChatColor.stripColor(s).startsWith(FAILURE_BONUS)) {
        success += (1 - success) * (200D / (200 + MaterialUtil.getUpgradeLevel(s)));
        break;
      }
    }
    success += PlayerDataUtil.getEffectiveLifeSkill(player, LifeSkillType.ENCHANTING, true) * 0.001;
    return success;
  }

  public static double getUpgradeFailureDamagePercent(UpgradeScroll scroll, int itemPlus) {
    return random.nextDouble() * getMaxFailureDamagePercent(scroll, itemPlus);
  }

  public static double getMaxFailureDamagePercent(UpgradeScroll scroll, int itemPlus) {
    return (0.25 + itemPlus * 0.11) * scroll.getItemDamageMultiplier();
  }

  public static boolean canBeExtended(List<String> lore) {
    List<String> stripColor = InventoryUtil.stripColor(lore);
    return stripColor.contains("(+)");
  }

  public static void extendItem(Player player, ItemStack stack, ItemStack extender) {
    List<String> lore = new ArrayList<>(ItemStackExtensionsKt.getLore(stack));
    List<String> strippedLore = InventoryUtil.stripColor(lore);
    if (!canBeExtended(strippedLore)) {
      sendMessage(player, extendFailMsg);
      player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
      return;
    }
    int index = strippedLore.indexOf("(+)");
    lore.set(index, ChatColor.GOLD + "(Socket)");
    ItemStackExtensionsKt.setLore(stack, lore);

    sendMessage(player, extendSuccessMsg);
    player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1L, 2.0F);
    extender.setAmount(extender.getAmount() - 1);
  }

  public static void upgradeItem(Player player, ItemStack scrollStack, ItemStack stack) {
    if (!canBeUpgraded(scrollStack, stack)) {
      sendMessage(player, upgradeFailureMsg);
      player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
      return;
    }
    String targetName = stripColor(ItemStackExtensionsKt.getDisplayName(stack));
    UpgradeScroll scroll = LootPlugin.getInstance().getScrollManager().getScroll(scrollStack);

    int itemUpgradeLevel = MaterialUtil.getUpgradeLevel(targetName);
    int targetLevel = itemUpgradeLevel;
    if (stack.getType() == Material.BOOK || stack.getType() == Material.ARROW) {
      targetLevel += 3;
    } else {
      targetLevel += 1;
    }
    targetLevel = Math.min(targetLevel, 15);

    double successChance = getSuccessChance(player, targetLevel, scrollStack, scroll);

    scrollStack.setAmount(scrollStack.getAmount() - 1);

    // Failure Logic
    if (successChance < random.nextDouble()) {
      double damagePercentage = getUpgradeFailureDamagePercent(scroll, targetLevel);
      int damageAmount;
      if (stack.getType().getMaxDurability() >= 1) {
        damageAmount = (int) Math.floor(damagePercentage * stack.getType().getMaxDurability()) - 1;
      } else {
        damageAmount = 100;
      }
      damageAmount = Math.max(damageAmount, 1);
      if (damageAmount + stack.getDurability() >= stack.getType().getMaxDurability()) {
        sendMessage(player, upgradeItemDestroyMsg);
        broadcast(player, stack, upgradeItemDestroyBroadcast);
        player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
        stack.setAmount(0);
        if (itemUpgradeLevel > 5) {
          ShardOfFailure shardOfFailure = new ShardOfFailure(player.getName());
          shardOfFailure.setAmount(
              1 + random.nextIntRange(5, 3 + (int) (Math.pow(itemUpgradeLevel, 1.7) / 3)));
          player.getInventory().addItem(shardOfFailure);
        }
        return;
      }
      stack.setDurability((short) (stack.getDurability() + damageAmount));
      sendMessage(player, upgradeItemDamageMsg);
      return;
    }

    bumpItemPlus(stack, itemUpgradeLevel, 1, targetLevel - itemUpgradeLevel);

    double exp = 0.5f + (float) Math.pow(1.4, targetLevel);
    LootPlugin.getInstance().getStrifePlugin().getSkillExperienceManager()
        .addExperience(player, LifeSkillType.ENCHANTING, exp, false);

    sendMessage(player, upgradeSuccessMsg);
    player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 2F);

    if (targetLevel >= 10) {
      broadcast(player, stack, upgradeSuccessBroadcast);
    }
  }

  private static void bumpItemPlus(ItemStack stack, int currentLevel, int amount) {
    bumpItemPlus(stack, currentLevel, amount, amount);
  }

  private static void bumpItemPlus(ItemStack stack, int currentLevel, int statAmount,
      int plusAmount) {
    String itemName = ItemStackExtensionsKt.getDisplayName(stack);
    int newLevel = Math.max(Math.min(currentLevel + plusAmount, 15), 0);
    if (currentLevel == 0) {
      itemName = getFirstColor(itemName) + ("+" + newLevel) + " " + itemName;
    } else {
      itemName = itemName.replace("+" + currentLevel, "+" + newLevel);
    }
    ItemStackExtensionsKt.setDisplayName(stack, itemName);
    if (newLevel >= 10 && stack.getEnchantments().isEmpty()) {
      stack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
    }
    ItemStackExtensionsKt.addItemFlags(stack, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES,
        ItemFlag.HIDE_UNBREAKABLE);
    List<String> lore = ItemStackExtensionsKt.getLore(stack);
    for (int i = 0; i < lore.size(); i++) {
      String s = lore.get(i);
      String ss = stripColor(s);
      if (!ss.startsWith("+")) {
        continue;
      }
      String attributeText = CharMatcher.digit().or(CharMatcher.is('-')).retainFrom(ss);
      int attributeValue = NumberUtils.toInt(attributeText);
      lore.set(i, s.replace("+" + attributeValue, "+" + (attributeValue + statAmount)));
      break;
    }
    ItemStackExtensionsKt.setLore(stack, lore);
  }

  public static boolean meetsUpgradeRange(UpgradeScroll type, int itemPlus) {
    return itemPlus >= type.getMinLevel() && itemPlus <= type.getMaxLevel();
  }

  public static boolean isUpgradePossible(ItemStack stack) {
    if (isBannedUpgradeMaterial(stack)) {
      return false;
    }
    String targetName = stripColor(ItemStackExtensionsKt.getDisplayName(stack));
    if (LootPlugin.getInstance().getSettings().getStringList("config.cannot-be-upgraded",
        new ArrayList<>()).contains(targetName)) {
      return false;
    }
    List<String> lore = ItemStackExtensionsKt.getLore(stack);
    for (String s : lore) {
      if ("< Cannot be upgraded >".equals(stripColor(s))) {
        return false;
      }
    }
    return isEquipmentItem(stack);
  }

  public static boolean isEquipmentItem(ItemStack stack) {
    List<String> strip = InventoryUtil.stripColor(ItemStackExtensionsKt.getLore(stack));
    for (String s : strip) {
      if (s.startsWith("+")) {
        return true;
      }
    }
    return false;
  }

  public static boolean canBeUpgraded(ItemStack scrollStack, ItemStack stack) {
    if (!isUpgradePossible(stack)) {
      return false;
    }
    UpgradeScroll scroll = LootPlugin.getInstance().getScrollManager().getScroll(scrollStack);
    if (scroll == null) {
      return false;
    }
    if (!meetsUpgradeRange(scroll, getUpgradeLevel(ItemStackExtensionsKt.getDisplayName(stack)))) {
      return false;
    }
    return true;
  }

  public static boolean hasEnchantmentTag(ItemStack stack) {
    List<String> lore = new ArrayList<>(ItemStackExtensionsKt.getLore(stack));
    List<String> strippedLore = InventoryUtil.stripColor(lore);
    if (!strippedLore.contains("(Enchantable)")) {
      return false;
    }
    return true;
  }

  public static boolean isMatchingGroup(EnchantmentTome tome, Material material) {
    return LootPlugin.getInstance().getItemGroupManager().getMatchingItemGroups(material)
        .containsAll(tome.getItemGroups());
  }

  public static boolean canBeEnchanted(Player player, ItemStack tomeStack, ItemStack stack) {
    EnchantmentTome tome = getEnchantmentItem(tomeStack);
    if (tome == null) {
      return false;
    }
    if (!isMatchingGroup(tome, stack.getType())) {
      sendMessage(player, enchantFailureMsg);
      player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
      return false;
    }

    if (!hasEnchantmentTag(stack)) {
      sendMessage(player, enchantTagNeededMsg);
      player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 1F, 0.5F);
      return false;
    }
    return true;
  }

  public static boolean enchantItem(Player player, ItemStack tomeStack, ItemStack targetItem) {
    if (!canBeEnchanted(player, tomeStack, targetItem)) {
      return false;
    }
    EnchantmentTome tome = getEnchantmentItem(tomeStack);

    List<String> lore = new ArrayList<>(ItemStackExtensionsKt.getLore(targetItem));
    List<String> strippedLore = InventoryUtil.stripColor(lore);

    int index = strippedLore.indexOf("(Enchantable)");
    lore.remove(index);

    double enchantLevel = PlayerDataUtil
        .getEffectiveLifeSkill(player, LifeSkillType.ENCHANTING, true);

    List<String> added = new ArrayList<>();
    if (!tome.getLore().isEmpty()) {
      added.addAll(TextUtils.color(tome.getLore()));
    }

    if (!StringUtils.isBlank(tome.getStat())) {
      double rarity = getBonusMultiplier(enchantLevel);

      int itemLevel = MaterialUtil.getItemLevel(targetItem);
      double eLevel = Math.max(1, Math.min(enchantLevel, itemLevel * 2));

      ItemStat stat = LootPlugin.getInstance().getStatManager().getStat(tome.getStat());
      added.add(LootPlugin.getInstance().getStatManager().getFinalStat(stat, eLevel, rarity));
    }

    if (tome.getBar()) {
      double bonus = getBonusMultiplier(enchantLevel);
      double size = 8 + (25 * bonus);
      String bars = IntStream.range(0, (int) size).mapToObj(i -> "|")
          .collect(Collectors.joining(""));
      added.add(TextUtils.color("&9[" + bars + "&0&9]"));
    }

    lore.addAll(index, TextUtils.color(added));

    if (enchantmentsStack) {
      for (Map.Entry<Enchantment, Integer> entry : tome.getEnchantments().entrySet()) {
        if (targetItem.containsEnchantment(entry.getKey())) {
          int previousLevel = targetItem.getEnchantmentLevel(entry.getKey());
          int newLevel = previousLevel + entry.getValue();
          targetItem.removeEnchantment(entry.getKey());
          targetItem.addUnsafeEnchantment(entry.getKey(), newLevel);
        } else {
          targetItem.addUnsafeEnchantment(entry.getKey(), entry.getValue());
        }
      }
    } else {
      boolean fail = true;
      for (Map.Entry<Enchantment, Integer> entry : tome.getEnchantments().entrySet()) {
        if (targetItem.containsEnchantment(entry.getKey())) {
          if (targetItem.getEnchantmentLevel(entry.getKey()) < entry.getValue()) {
            targetItem.removeEnchantment(entry.getKey());
            targetItem.addUnsafeEnchantment(entry.getKey(), entry.getValue());
            fail = false;
          }
        } else {
          targetItem.addUnsafeEnchantment(entry.getKey(), entry.getValue());
          fail = false;
        }
      }
      if (fail) {
        sendMessage(player, enchantPointlessMsg);
        return false;
      }
    }

    tomeStack.setAmount(tomeStack.getAmount() - 1);
    ItemStackExtensionsKt.setLore(targetItem, lore);

    float weightDivisor = tome.getWeight() == 0 ? 2000 : (float) tome.getWeight();
    float exp = 3 + 2000 / weightDivisor;
    LootPlugin.getInstance().getStrifePlugin().getSkillExperienceManager()
        .addExperience(player, LifeSkillType.ENCHANTING, exp, false);
    sendMessage(player, enchantSuccessMsg);
    player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1L, 2.0F);
    return true;
  }

  public static EnchantmentTome getEnchantmentItem(String name) {
    String stoneName = stripColor(name.replace("Enchantment Tome - ", ""));
    return LootPlugin.getInstance().getEnchantTomeManager().getEnchantTome(stoneName);
  }

  public static EnchantmentTome getEnchantmentItem(ItemStack stack) {
    if (stack == null) {
      return null;
    }
    String name = ItemStackExtensionsKt.getDisplayName(stack);
    if (StringUtils.isBlank(name)) {
      return null;
    }
    if (!name.startsWith(ChatColor.BLUE + "Enchantment Tome - ")) {
      return null;
    }
    return getEnchantmentItem(name);
  }

  public static boolean isEnchantmentItem(ItemStack stack) {
    return getEnchantmentItem(stack) != null;
  }

  public static boolean isExtender(ItemStack stack) {
    return ItemStackExtensionsKt.getDisplayName(SocketExtender.EXTENDER)
        .equals(ItemStackExtensionsKt.getDisplayName(stack));
  }

  public static ItemStack buildMaterial(Material m, String name, int level, int quality) {
    ItemStack his = new ItemStack(m);
    ChatColor color;
    String prefix;
    switch (quality) {
      case 2:
        prefix = "Quality";
        color = ChatColor.BLUE;
        break;
      case 3:
        prefix = "Rare";
        color = ChatColor.DARK_PURPLE;
        break;
      case 4:
        prefix = "Grand";
        color = ChatColor.RED;
        break;
      case 5:
        prefix = "Perfect";
        color = ChatColor.GOLD;
        break;
      default:
        prefix = "Old";
        color = ChatColor.WHITE;
    }
    ItemStackExtensionsKt.setDisplayName(his, color + prefix + " " + name);
    List<String> lore = new ArrayList<>();
    lore.add(ChatColor.WHITE + "Item Level: " + Math.min(100, Math.max(1, 3 * (level / 3))));
    lore.add(ChatColor.WHITE + "Quality: " + color + IntStream.range(0, quality).mapToObj(i -> "✪")
        .collect(Collectors.joining("")));
    lore.add(ChatColor.YELLOW + "[ Crafting Component ]");
    ItemStackExtensionsKt.setLore(his, lore);

    return his;
  }

  public static int getQuality(ItemStack stack) {
    for (String line : ItemStackExtensionsKt.getLore(stack)) {
      if (ChatColor.stripColor(line).startsWith("Quality:")) {
        return (int) line.chars().filter(ch -> ch == '✪').count();
      }
    }
    return 1;
  }

  public static ItemStack buildEssence(String type, double itemLevel, double craftLevel,
      int toolQuality, List<String> possibleStats, boolean lucky) {
    int essLevel = 1 + (int) (itemLevel * 0.85 + random.nextDouble() * itemLevel * 0.125);

    String statString = ChatColor.stripColor(
        possibleStats.get(random.nextInt(possibleStats.size())));
    int statVal = getDigit(statString);

    double toolQualityBonus = rollMult(lucky) * (TOOL_QUALITY_ESS_MULT * toolQuality);
    double baseCraftBonus = MIN_BONUS_ESS_MULT * (craftLevel / 100);
    double bonusCraftBonus = rollMult(lucky, 1.3) * (BONUS_ESS_MULT * (craftLevel / 100));

    double essMult = BASE_ESSENCE_MULT + baseCraftBonus + bonusCraftBonus + toolQualityBonus;

    String newStatString = statString.replace(String.valueOf(statVal),
        String.valueOf((int) Math.max(1, statVal * essMult)));

    ItemStack shard = new ItemStack(Material.PRISMARINE_SHARD);
    ItemStackExtensionsKt.setDisplayName(shard, ChatColor.YELLOW + "Item Essence");

    List<String> esslore = new ArrayList<>();
    esslore.add("&fItem Level Requirement: " + essLevel);
    esslore.add("&fItem Type: " + type);
    esslore.add("&e" + newStatString);
    esslore.add("&7&oCraft this together with an");
    esslore.add("&7&ounfinished item to have a");
    esslore.add("&7&ochance of applying this stat!");
    esslore.add("&e[ Crafting Component ]");
    ItemStackExtensionsKt.setLore(shard, TextUtils.color(esslore));

    return shard;
  }

  public static int getUpgradeLevel(String name) {
    name = stripColor(name);
    String lev = CharMatcher.digit().or(CharMatcher.is('-')).negate().collapseFrom(name, ' ')
        .trim();
    return NumberUtils.toInt(lev.split(" ")[0], 0);
  }

  public static int getItemLevel(ItemStack stack) {
    if (stack.getItemMeta() == null) {
      return -1;
    }
    if (ItemStackExtensionsKt.getLore(stack).get(0) == null) {
      return -1;
    }
    String lvlReqString = ChatColor.stripColor(ItemStackExtensionsKt.getLore(stack).get(0));
    if (!lvlReqString.startsWith("Level Requirement:")) {
      return -1;
    }
    return getDigit(ItemStackExtensionsKt.getLore(stack).get(0));
  }

  public static int getToolLevel(ItemStack stack) {
    if (stack.getItemMeta() == null) {
      return -1;
    }
    if (ItemStackExtensionsKt.getLore(stack).get(0) == null) {
      return -1;
    }
    String lvlReqString = ChatColor.stripColor(ItemStackExtensionsKt.getLore(stack).get(0));
    if (!lvlReqString.startsWith("Craft Skill Requirement:")) {
      return -1;
    }
    return getDigit(ItemStackExtensionsKt.getLore(stack).get(0));
  }

  public static int getDigit(String string) {
    String lev = CharMatcher.digit().or(CharMatcher.is('-')).negate()
        .collapseFrom(ChatColor.stripColor(string), ' ').trim();
    return NumberUtils.toInt(lev.split(" ")[0], 0);
  }

  private static double rollMult(boolean lucky) {
    return rollMult(lucky, 2);
  }

  private static double rollMult(boolean lucky, double exponent) {
    if (!lucky) {
      return Math.pow(random.nextDouble(), exponent);
    }
    return random.nextDouble();
  }

  public static void applyTierLevelData(ItemStack stack, Tier tier, int level) {
    if (tier.getCustomDataStart() != -1) {
      double modelLevel = Math.max(0, level - 1);
      int customModel =
          tier.getCustomDataStart() + (int) Math.floor(modelLevel / tier.getCustomDataInterval());
      ItemStackExtensionsKt.setCustomModelData(stack, customModel);
    }
  }

  public static int getCustomData(ItemStack stack) {
    if (stack.getItemMeta() == null) {
      return -1;
    }
    if (!stack.getItemMeta().hasCustomModelData()) {
      return -1;
    }
    return stack.getItemMeta().getCustomModelData();
  }

  public static double getBaseEnchantBonus(double enchantSkill) {
    return enchantSkill * 0.005;
  }

  public static double getBonusMultiplier(double enchantSkill) {
    double enchantPower = Math.max(0, Math.min(1, enchantSkill / 100));
    double skillRoll = enchantPower * random.nextDouble();
    double dumbLuckRoll = (1 - enchantPower) * Math.pow(random.nextDouble(), 2.5);
    return skillRoll + dumbLuckRoll + getBaseEnchantBonus(enchantSkill);
  }

  private static boolean isBannedUpgradeMaterial(ItemStack item) {
    switch (item.getType()) {
      case EMERALD:
      case PAPER:
      case NETHER_STAR:
      case DIAMOND:
      case GHAST_TEAR:
      case ENCHANTED_BOOK:
      case NAME_TAG:
      case QUARTZ:
        return true;
      default:
        return false;
    }
  }
}
