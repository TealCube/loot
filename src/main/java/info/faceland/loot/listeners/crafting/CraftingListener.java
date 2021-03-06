/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package info.faceland.loot.listeners.crafting;

import static com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils.sendMessage;
import static info.faceland.loot.utils.InventoryUtil.stripColor;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.data.ItemStat;
import info.faceland.loot.events.LootCraftEvent;
import info.faceland.loot.items.LootItemBuilder;
import info.faceland.loot.listeners.DeconstructListener;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.recipe.EquipmentRecipeBuilder;
import info.faceland.loot.tier.Tier;
import info.faceland.loot.utils.MaterialUtil;
import io.pixeloutlaw.minecraft.spigot.garbage.ListExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.strife.data.champion.LifeSkillType;
import land.face.strife.util.PlayerDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CraftingListener implements Listener {

  private final LootPlugin plugin;

  public static String ESSENCE_SLOT_TEXT;

  private final String BAD_INFUSE_NAME;
  private final double CRAFT_EXP;
  private final double CRAFT_LEVEL_MULT;
  private final double CRAFT_QUALITY_MULT;
  private final double CRAFT_MASTER_MULT;
  private final double MAX_QUALITY;
  private final double MAX_SLOTS;
  private final double MAX_SOCKETS;

  private final ItemMeta dupeStatMeta;
  private final ItemMeta powerfulEssenceMeta;
  private final ItemMeta lowLevelMeta;
  private final ItemMeta wrongTypeMeta;
  private final ItemMeta noSlotsMeta;

  private final LootRandom random;

  public CraftingListener(LootPlugin plugin) {
    this.plugin = plugin;
    this.random = new LootRandom();

    CRAFT_EXP = plugin.getSettings().getDouble("config.crafting.base-craft-exp", 1);
    CRAFT_LEVEL_MULT = plugin.getSettings().getDouble("config.crafting.craft-item-level-mult", 0.01);
    CRAFT_QUALITY_MULT = plugin.getSettings().getDouble("config.crafting.craft-quality-mult", 0.1);
    CRAFT_MASTER_MULT = plugin.getSettings().getDouble("config.crafting.craft-master-mult", 2.5);
    MAX_QUALITY = plugin.getSettings().getDouble("config.crafting.craft-max-quality", 5);
    MAX_SLOTS = plugin.getSettings().getDouble("config.crafting.craft-max-craft-slots", 5);
    MAX_SOCKETS = plugin.getSettings().getDouble("config.crafting.craft-max-sockets", 3);
    ESSENCE_SLOT_TEXT = StringExtensionsKt
        .chatColorize(plugin.getSettings().getString("config.crafting.essence-text", "&b(Essence Slot)"));

    BAD_INFUSE_NAME = StringExtensionsKt
        .chatColorize(plugin.getSettings().getString("language.essence.invalid-title", "&cCannot Use Essence!"));

    ItemStack failStack = new ItemStack(Material.BARRIER);
    ItemStackExtensionsKt.setDisplayName(failStack, StringExtensionsKt.chatColorize(BAD_INFUSE_NAME));

    dupeStatMeta = failStack.getItemMeta().clone();
    dupeStatMeta.setLore(ListExtensionsKt.chatColorize(
        plugin.getSettings().getStringList("language.essence.duplicate-stats")));
    powerfulEssenceMeta = failStack.getItemMeta().clone();
    powerfulEssenceMeta.setLore(ListExtensionsKt.chatColorize(
        plugin.getSettings().getStringList("language.essence.essence-strength")));
    lowLevelMeta = failStack.getItemMeta().clone();
    lowLevelMeta.setLore(ListExtensionsKt.chatColorize(
        plugin.getSettings().getStringList("language.essence.low-craft-level")));
    wrongTypeMeta = failStack.getItemMeta().clone();
    wrongTypeMeta.setLore(ListExtensionsKt.chatColorize(
        plugin.getSettings().getStringList("language.essence.wrong-type")));
    noSlotsMeta = failStack.getItemMeta().clone();
    noSlotsMeta.setLore(ListExtensionsKt.chatColorize(
        plugin.getSettings().getStringList("language.essence.no-slots")));
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCraftItemEvent(CraftItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    for (ItemStack is : event.getInventory().getMatrix()) {
      if (is == null || is.getType() == Material.AIR) {
        continue;
      }
      ItemStack materialStack = new ItemStack(is);
      Material material = event.getInventory().getResult().getType();
      // TODO: Configurable material type restriction
      if (material == Material.DIAMOND_BLOCK || material == Material.IRON_BLOCK ||
          material == Material.GOLD_BLOCK || material == Material.EMERALD_BLOCK) {
        for (String str : ItemStackExtensionsKt.getLore(materialStack)) {
          if (ChatColor.stripColor(str).equals("[ Crafting Component ]")) {
            sendMessage(event.getWhoClicked(),
                plugin.getSettings().getString("language.craft.nope", ""));
            event.setCancelled(true);
            return;
          }
        }
      }
      String name = ItemStackExtensionsKt.getDisplayName(materialStack);
      if (isUncraftableByName(name)) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onSpecialCraftEquipment(CraftItemEvent event) {
    ItemStack resultStack = event.getCurrentItem();
    if (!plugin.getCraftBaseManager().getCraftBases().containsKey(resultStack.getType())) {
      return;
    }

    Player player = (Player) event.getWhoClicked();

    if (event.getAction() == InventoryAction.NOTHING) {
      sendMessage(player, plugin.getSettings().getString("language.craft.no-weird", ""));
      event.setCancelled(true);
    } else if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT
        || event.getClick() == ClickType.NUMBER_KEY) {
      sendMessage(player, plugin.getSettings().getString("language.craft.no-shift", ""));
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onCraftEquipment(InventoryClickEvent event) {
    if (event.getRawSlot() == 0 && event.getSlot() == 0 && event.getSlotType() == SlotType.RESULT) {
      if (event.getClick() == ClickType.CONTROL_DROP) {
        MessageUtils.sendMessage(event.getWhoClicked(),
            "&e&oSorry, this crafting operation is blocked due to bugs! No items have been consumed, even if your game client may say they were until you close the crafting grid...");
        event.setCancelled(true);
        return;
      }
      if (event.getAction() == InventoryAction.DROP_ONE_SLOT && (event.getCursor() != null
          && event.getCursor().getType() != Material.AIR)) {
        MessageUtils.sendMessage(event.getWhoClicked(),
            "&e&oSorry, this crafting operation is blocked due to bugs! No items have been consumed, even if your game client may say they were until you close the crafting grid...");
        event.setCancelled(true);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onCraftEquipment(CraftItemEvent event) {
    if (event.isCancelled()) {
      return;
    }

    ItemStack resultStack = event.getCurrentItem();

    if (!plugin.getCraftBaseManager().getCraftBases().containsKey(resultStack.getType())) {
      return;
    }
    for (ItemStack is : event.getInventory().getMatrix()) {
      if (is == null) {
        continue;
      }
      if (is.getType() == Material.PRISMARINE_SHARD || isDyeEvent(is.getType(), resultStack.getType())) {
        return;
      }
    }

    event.setCancelled(true);

    Player player = (Player) event.getWhoClicked();

    Tier tier = MaterialUtil.getTierFromStack(resultStack);

    if (tier == null) {
      Bukkit.getLogger().warning("Attempted to craft item with unknown tier... " + resultStack.getType());
      return;
    }

    int craftingLevel = PlayerDataUtil.getLifeSkillLevel(player, LifeSkillType.CRAFTING);
    double effectiveCraftLevel = PlayerDataUtil.getEffectiveLifeSkill(player, LifeSkillType.CRAFTING, true);

    int numMaterials = 0;
    double totalQuality = 0;
    double totalItemLevel = 0;

    for (ItemStack is : event.getInventory().getMatrix()) {
      if (is == null || is.getType() == Material.AIR || is.getType() == resultStack.getType()) {
        continue;
      }
      ItemStack loopItem = new ItemStack(is);
      if (hasItemLevel(loopItem)) {
        int iLevel = NumberUtils.toInt(CharMatcher.digit().or(CharMatcher.is('-')).negate()
            .collapseFrom(ChatColor.stripColor(ItemStackExtensionsKt.getLore(loopItem).get(0)), ' ').trim());
        totalItemLevel += iLevel;
      } else {
        totalItemLevel += 0.5;
      }
      numMaterials++;
      if (hasQuality(loopItem)) {
        long count = ItemStackExtensionsKt.getLore(loopItem).get(1).chars().filter(ch -> ch == '✪').count();
        totalQuality += count;
      }
    }

    double rawItemLevel = totalItemLevel / numMaterials;
    double levelAdvantage = DeconstructListener.getLevelAdvantage(craftingLevel, (int) rawItemLevel);

    if (levelAdvantage < 0) {
      sendMessage(player, plugin.getSettings().getString("language.craft.low-level-craft", ""));
      player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 0.7F, 0.5F);
      return;
    }

    double effiLevelAdvantage = DeconstructListener.getLevelAdvantage((int) effectiveCraftLevel, (int) rawItemLevel);
    double skillMultiplier = 1 + Math.min(1, effiLevelAdvantage / 25);
    double quality = Math.max(1, Math.min(totalQuality / numMaterials, MAX_QUALITY) - (0.5 * random.nextDouble()));

    double slotLuckBonus = (MAX_SLOTS - quality) * Math.pow(random.nextDouble(), 6);
    double craftedSlotScore = Math.max(1, quality + slotLuckBonus);

    double skillSocketRoll = skillMultiplier * 0.5 * Math.pow(random.nextDouble(), 2);
    double luckSocketRoll = (1 - skillMultiplier) * Math.pow(random.nextDouble(), 6);
    double craftedSocketScore = MAX_SOCKETS * (skillSocketRoll + luckSocketRoll);

    if (skillMultiplier > 1.75) {
      craftedSocketScore = Math.max(2, craftedSocketScore);
    } else if (skillMultiplier > 1.5) {
      craftedSocketScore = Math.max(1, craftedSocketScore);
    }

    int itemLevel = (int) Math.max(1, Math.min(100, rawItemLevel - random.nextInt(4)));

    ItemStack newResult = new ItemStack(event.getCurrentItem().getType());
    ItemStackExtensionsKt.setDisplayName(newResult, ChatColor.AQUA +
        plugin.getNameManager().getRandomPrefix() + " " + plugin.getNameManager()
        .getRandomSuffix());
    List<String> lore = new ArrayList<>();

    lore.add(ChatColor.WHITE + "Level Requirement: " + itemLevel);
    lore.add(ChatColor.WHITE + "Tier: " + ChatColor.AQUA + "Crafted " + tier.getName());

    lore.add(plugin.getStatManager().getFinalStat(tier.getPrimaryStat(), itemLevel, quality, false).getStatString());
    lore.add(plugin.getStatManager().getFinalStat(
        tier.getSecondaryStats().get(random.nextInt(tier.getSecondaryStats().size())), itemLevel, quality, false)
        .getStatString());

    List<ItemStat> stats = new ArrayList<>(tier.getBonusStats());

    boolean masterwork = craftedSlotScore / MAX_SLOTS > 0.85 && craftedSocketScore / MAX_SOCKETS > 0.85;

    if (masterwork) {
      craftedSlotScore = Math.ceil(craftedSlotScore);
      craftedSocketScore = Math.ceil(craftedSocketScore);
    }

    double openSlotChance = (skillMultiplier - 1) * 0.7;
    while (craftedSlotScore >= 1) {
      if (random.nextDouble() < openSlotChance) {
        lore.add(ESSENCE_SLOT_TEXT);
      } else {
        ItemStat stat = stats.get(random.nextInt(stats.size()));
        lore.add(ChatColor.AQUA + ChatColor
            .stripColor(plugin.getStatManager().getFinalStat(stat, itemLevel, quality, false).getStatString()));
        stats.remove(stat);
      }
      craftedSlotScore--;
    }

    lore.add(ChatColor.BLUE + "(Enchantable)");

    while (craftedSocketScore >= 1) {
      lore.add(ChatColor.GOLD + "(Socket)");
      craftedSocketScore--;
    }
    if (masterwork) {
      lore.add(TextUtils.color("&8&oCrafted By: " + player.getName()));
      lore.add(TextUtils.color("&8&o[ Flavor Text Slot ]"));
    }

    ItemStackExtensionsKt.setLore(newResult, lore);
    ItemStackExtensionsKt.addItemFlags(newResult, ItemFlag.HIDE_ATTRIBUTES);
    switch (newResult.getType()) {
      case NETHERITE_HELMET:
      case NETHERITE_CHESTPLATE:
      case NETHERITE_LEGGINGS:
      case NETHERITE_BOOTS:
        ItemMeta iMeta = newResult.getItemMeta();
        iMeta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, LootItemBuilder.MINUS_ONE_KB_RESIST);
        newResult.setItemMeta(iMeta);
    }
    MaterialUtil.applyTierLevelData(newResult, tier, itemLevel);

    event.setCurrentItem(newResult);
    event.setCancelled(false);

    double exp = CRAFT_EXP * (numMaterials * 0.25);
    exp *= 1 + (itemLevel * CRAFT_LEVEL_MULT);
    exp *= 1 + (quality * CRAFT_QUALITY_MULT);
    if (masterwork) {
      exp *= CRAFT_MASTER_MULT;
    }
    if (craftingLevel - 8 > rawItemLevel) {
      exp *= rawItemLevel / craftingLevel;
    }

    LootCraftEvent craftEvent = new LootCraftEvent(player, newResult);
    Bukkit.getPluginManager().callEvent(craftEvent);

    plugin.getStrifePlugin().getSkillExperienceManager().addExperience(player,
        LifeSkillType.CRAFTING, exp, false, false);

    player.playSound(player.getEyeLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1F, 1F);
    player.playSound(player.getEyeLocation(), Sound.BLOCK_ANVIL_FALL, 0.5F, 1F);
  }

  @EventHandler
  public void onEssenceInfuse(PrepareItemCraftEvent event) {

    if (event.getRecipe() == null) {
      return;
    }

    ItemStack resultStack = event.getRecipe().getResult();
    ItemStack equipmentItem = null;
    ItemStack essenceStack = null;

    for (ItemStack is : event.getInventory().getMatrix()) {
      if (!(essenceStack == null || equipmentItem == null)) {
        break;
      }
      if (is == null || is.getType() == Material.AIR) {
        continue;
      }
      ItemStack loopItem = new ItemStack(is);
      if (is.getType() == resultStack.getType()) {
        equipmentItem = loopItem;
        continue;
      }
      if (isEssence(loopItem)) {
        essenceStack = loopItem;
      }
    }

    if (essenceStack == null || equipmentItem == null) {
      return;
    }

    if (getEssenceTier(essenceStack) != MaterialUtil.getTierFromStack(equipmentItem)) {
      event.getInventory().getResult().setType(Material.BARRIER);
      event.getInventory().getResult().setItemMeta(wrongTypeMeta);
      return;
    }

    List<String> lore = ItemStackExtensionsKt.getLore(equipmentItem);
    List<String> strippedLore = stripColor(lore);

    if (!lore.contains(CraftingListener.ESSENCE_SLOT_TEXT)) {
      event.getInventory().getResult().setType(Material.BARRIER);
      event.getInventory().getResult().setItemMeta(noSlotsMeta);
      return;
    }

    int itemLevel = NumberUtils.toInt(CharMatcher.digit().or(CharMatcher.is('-')).negate()
        .collapseFrom(ChatColor.stripColor(strippedLore.get(0)), ' ').trim());

    int essenceLevel = getEssenceLevel(essenceStack);

    Player player = (Player) event.getViewers().get(0);
    int craftingLevel = PlayerDataUtil.getLifeSkillLevel(player, LifeSkillType.CRAFTING);
    double levelAdvantage = DeconstructListener.getLevelAdvantage(craftingLevel, itemLevel);
    if (levelAdvantage < 0) {
      event.getInventory().getResult().setType(Material.BARRIER);
      event.getInventory().getResult().setItemMeta(lowLevelMeta);
      return;
    }

    if (essenceLevel > itemLevel) {
      event.getInventory().getResult().setType(Material.BARRIER);
      event.getInventory().getResult().setItemMeta(powerfulEssenceMeta);
      return;
    }

    List<String> existingCraftStatStrings = new ArrayList<>();
    for (String str : lore) {
      if (!str.startsWith(ChatColor.AQUA + "+")) {
        continue;
      }
      str = CharMatcher.javaLetter().or(CharMatcher.is(' ')).retainFrom(ChatColor.stripColor(str).trim());
      existingCraftStatStrings.add(str);
    }

    String essenceStat = getEssenceStat(essenceStack);
    String strippedStat = CharMatcher.javaLetter().or(CharMatcher.is(' '))
        .retainFrom(ChatColor.stripColor(essenceStat).trim());

    if (existingCraftStatStrings.contains(strippedStat)) {
      event.getInventory().getResult().setType(Material.BARRIER);
      event.getInventory().getResult().setItemMeta(dupeStatMeta);
      return;
    }

    int slotIndex = lore.indexOf(CraftingListener.ESSENCE_SLOT_TEXT);
    lore.set(slotIndex, ChatColor.AQUA + ChatColor.stripColor(essenceStat));
    ItemStackExtensionsKt.setLore(equipmentItem, lore);

    event.getInventory().getResult().setType(equipmentItem.getType());
    event.getInventory().getResult().setItemMeta(equipmentItem.getItemMeta());
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onCraftBarrier(CraftItemEvent event) {
    if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BARRIER
        || event.getCursor() != null && event.getCursor().getType() == Material.BARRIER) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEssenceInfuse(CraftItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (!ItemStackExtensionsKt.getDisplayName(event.getRecipe().getResult())
        .equals(EquipmentRecipeBuilder.INFUSE_NAME)) {
      return;
    }
    plugin.getStrifePlugin().getSkillExperienceManager().addExperience((Player) event.getWhoClicked(),
        LifeSkillType.CRAFTING, 25, false, false);
  }

  private boolean isUncraftableByName(String name) {
    if (StringUtils.isBlank(name)) {
      return false;
    }
    return name.equals(ChatColor.DARK_AQUA + "Socket Extender") ||
        name.startsWith(ChatColor.BLUE + "Enchantment Tome - ") ||
        name.startsWith(ChatColor.GOLD + "Socket Gem -") ||
        name.startsWith(ChatColor.DARK_AQUA + "Scroll Augment -");
  }

  private boolean isEssence(ItemStack itemStack) {
    if (itemStack.getType() != Material.PRISMARINE_SHARD || StringUtils
        .isBlank(ItemStackExtensionsKt.getDisplayName(itemStack))) {
      return false;
    }
    if (!ChatColor.stripColor(ItemStackExtensionsKt.getDisplayName(itemStack)).equals("Item Essence")) {
      return false;
    }
    List<String> lore = ItemStackExtensionsKt.getLore(itemStack);
    List<String> strippedLore = stripColor(lore);
    if (strippedLore.get(0) == null || !strippedLore.get(0).startsWith("Item Level Requirement")) {
      return false;
    }
    if (strippedLore.get(1) == null || !strippedLore.get(1).startsWith("Item Type")) {
      return false;
    }
    if (strippedLore.get(2) == null) {
      return false;
    }
    return true;
  }

  private Tier getEssenceTier(ItemStack itemStack) {
    String str = ChatColor.stripColor(ItemStackExtensionsKt.getLore(itemStack).get(1)).replace("Item Type: ", "");
    return plugin.getTierManager().getTier(str);
  }

  private String getEssenceStat(ItemStack itemStack) {
    return ItemStackExtensionsKt.getLore(itemStack).get(2);
  }

  private boolean hasQuality(ItemStack h) {
    return !StringUtils.isBlank(ItemStackExtensionsKt.getDisplayName(h)) && h.hasItemMeta()
        && ItemStackExtensionsKt.getLore(h).get(1) != null &&
        ChatColor.stripColor(ItemStackExtensionsKt.getLore(h).get(1)).startsWith("Quality: ");
  }

  private boolean hasItemLevel(ItemStack h) {
    return !StringUtils.isBlank(ItemStackExtensionsKt.getDisplayName(h)) && h.hasItemMeta()
        && ItemStackExtensionsKt.getLore(h).get(0) != null &&
        ChatColor.stripColor(ItemStackExtensionsKt.getLore(h).get(0)).startsWith("Item Level: ");
  }

  private int getEssenceLevel(ItemStack h) {
    return NumberUtils.toInt(CharMatcher.digit().or(CharMatcher.is('-')).negate().collapseFrom(
        ChatColor.stripColor(ItemStackExtensionsKt.getLore(h).get(0)), ' ').trim());
  }

  private boolean isDyeEvent(Material ingredient, Material result) {
    return ingredient == result && (ingredient == Material.LEATHER_HELMET
        || ingredient == Material.LEATHER_CHESTPLATE || ingredient == Material.LEATHER_LEGGINGS
        || ingredient == Material.LEATHER_BOOTS);
  }
}
