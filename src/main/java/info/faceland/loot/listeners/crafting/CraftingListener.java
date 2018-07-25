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
package info.faceland.loot.listeners.crafting;

import static com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils.sendMessage;
import static info.faceland.loot.utils.inventory.InventoryUtil.stripColor;

import com.tealcube.minecraft.bukkit.TextUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.math.NumberUtils;
import com.tealcube.minecraft.bukkit.shade.google.common.base.CharMatcher;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.recipe.EquipmentRecipeBuilder;
import info.faceland.strife.util.PlayerDataUtil;
import info.faceland.strife.util.SkillExperienceUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.HiltItemStack;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CraftingListener implements Listener {

  private final LootPlugin plugin;
  private LootRandom random;

  public CraftingListener(LootPlugin plugin) {
    this.plugin = plugin;
    this.random = new LootRandom();
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
      HiltItemStack his = new HiltItemStack(is);
      if (event.getInventory().getResult().getType() == Material.DIAMOND_BLOCK) {
        for (String str : his.getLore()) {
          if (ChatColor.stripColor(str).equals("[ Crafting Component ]")) {
            sendMessage(event.getWhoClicked(),
                plugin.getSettings().getString("language.craft.nope", ""));
            event.setCancelled(true);
            return;
          }
        }
      }
      if (his.getName().equals(ChatColor.DARK_AQUA + "Socket Extender") ||
          his.getName().startsWith(ChatColor.BLUE + "Enchantment Tome - ") ||
          his.getName().startsWith(ChatColor.GOLD + "Socket Gem -") ||
          his.getName().startsWith(ChatColor.DARK_AQUA + "Scroll Augment -")) {
        event.setCancelled(true);
        return;
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onCraftEquipment(CraftItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (!(event.getCursor() == null || event.getCursor().getType() == Material.AIR)) {
      event.setCancelled(true);
      return;
    }
    ItemStack resultStack = event.getCurrentItem();
    if (resultStack.getItemMeta().hasDisplayName()) {
      return;
    }
    if (!plugin.getCraftBaseManager().getCraftBases().containsKey(resultStack.getType())) {
      return;
    }

    event.setCancelled(true);
    Player player = (Player) event.getWhoClicked();

    if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT
        || event.getClick() == ClickType.NUMBER_KEY) {
      sendMessage(player, plugin.getSettings().getString("language.craft.no-shift", ""));
      return;
    }

    String strTier = plugin.getCraftBaseManager().getCraftBases().get(resultStack.getType());
    Tier tier = plugin.getTierManager().getTier(strTier);

    int craftingLevel = PlayerDataUtil.getCraftLevel(player);

    int numMaterials = 0;
    double totalQuality = 0;
    double totalItemLevel = 0;
    for (ItemStack is : event.getInventory().getMatrix()) {
      if (is == null || is.getType() == Material.AIR || is.getType() == resultStack.getType()) {
        continue;
      }
      HiltItemStack loopItem = new HiltItemStack(is);
      if (hasItemLevel(loopItem)) {
        int iLevel = NumberUtils
            .toInt(CharMatcher.DIGIT.or(CharMatcher.is('-')).negate().collapseFrom(
                ChatColor.stripColor(loopItem.getLore().get(0)), ' ').trim());
        totalItemLevel += iLevel;
        numMaterials++;
      } else {
        totalItemLevel += 0.5;
        numMaterials++;
      }
      if (hasQuality(loopItem)) {
        long count = loopItem.getLore().get(1).chars().filter(ch -> ch == '✪').count();
        totalQuality += count;
      }
    }

    double itemLevel = totalItemLevel / numMaterials;

    if (maxCraftingLevel(craftingLevel) < (int) itemLevel) {
      sendMessage(player,
          plugin.getSettings().getString("language.craft.low-level-craft", ""));
      player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 0.7F, 0.5F);
      return;
    }

    double overLvlMult = Math.max(1, Math.min(2.0, (craftingLevel - itemLevel) / 30));

    double quality = totalQuality / numMaterials;
    double missingQuality = 5 - quality;
    double qualityScore =
        quality * random.nextDouble() + missingQuality * Math.pow(random.nextDouble(), 2);

    double statScore = (1 + (qualityScore / 4)) * overLvlMult;
    double socketScore = 1 + (random.nextDouble() * (qualityScore / 5)) * overLvlMult;

    double moddedItemLevel = itemLevel * (1 + overLvlMult / 10);

    itemLevel = (int) Math.max(1, Math.min(100, itemLevel - 2 + random.nextInt(5)));

    HiltItemStack newResult = new HiltItemStack(event.getCurrentItem().getType());
    newResult.setName(TextUtils.color(
        "&b" + plugin.getNameManager().getRandomPrefix() + " " + plugin.getNameManager()
            .getRandomSuffix()));
    List<String> lore = new ArrayList<>();

    lore.add(TextUtils.color("&fLevel Requirement: " + (int) itemLevel));
    lore.add(TextUtils.color("&fTier: " + "&bCrafted " + tier.getName()));

    lore.add(TextUtils.color(plugin.getStatManager()
        .getFinalStat(tier.getPrimaryStat(), moddedItemLevel, qualityScore)));
    lore.add(TextUtils.color(plugin.getStatManager().getFinalStat(
        tier.getSecondaryStats().get(random.nextInt(tier.getSecondaryStats().size())),
        moddedItemLevel, qualityScore)));

    boolean masterwork = false;
    if (itemLevel >= 1 && random.nextDouble() <= 0.01 + craftingLevel * 0.001) {
      masterwork = true;
    }

    if (statScore < 2 && random.nextDouble() < 0.3) {
      statScore++;
    }
    if (masterwork) {
      statScore++;
    }
    while (statScore >= 1) {
      lore.add(TextUtils.color("&b[ Crafted Stat Slot ]"));
      statScore--;
    }

    lore.add(TextUtils.color("&9(Enchantable)"));

    if (masterwork) {
      socketScore++;
    }
    if (socketScore < 2 && random.nextDouble() < 0.1) {
      socketScore++;
    }
    while (socketScore >= 1) {
      lore.add(TextUtils.color("&6(Socket)"));
      socketScore--;
    }
    if (masterwork || random.nextDouble() < 0.1 * Math.pow(overLvlMult, 3)) {
      lore.add(TextUtils.color("&3(+)"));
    }
    if (masterwork) {
      lore.add(TextUtils.color("&8&o-- " + player.getName() + " --"));
      lore.add(TextUtils.color("&8&o[ Flavor Text Slot ]"));
    }
    double exp =
        (0.25 + qualityScore / 2 + itemLevel * 0.7) * (0.8 + qualityScore / 5) * (numMaterials
            * 0.35) * (masterwork ? 2.5 : 1.0);
    if (craftingLevel > itemLevel + 5) {
      exp = exp / Math.max((craftingLevel - itemLevel), 1);
    }
    newResult.setLore(lore);
    ItemMeta meta = newResult.getItemMeta();
    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    newResult.setItemMeta(meta);
    event.setCurrentItem(newResult);
    event.setCancelled(false);
    SkillExperienceUtil.addCraftExperience(player, exp);
    player.playSound(player.getEyeLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1F, 1F);
    player.playSound(player.getEyeLocation(), Sound.BLOCK_ANVIL_FALL, 0.5F, 1F);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEssenceInfuse(CraftItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (!(event.getCursor() == null || event.getCursor().getType() == Material.AIR)) {
      event.setCancelled(true);
      return;
    }
    ItemStack resultStack = event.getCurrentItem();
    if (!resultStack.hasItemMeta() || !resultStack.getItemMeta().hasDisplayName()) {
      return;
    }
    if (!resultStack.getItemMeta().getDisplayName().equals(EquipmentRecipeBuilder.INFUSE_NAME)) {
      return;
    }
    event.setCancelled(true);
    Player player = (Player) event.getWhoClicked();
    List<String> essenceStats = new ArrayList<>();
    HiltItemStack baseItem = null;
    int highestEssLevel = 0;
    for (ItemStack is : event.getInventory().getMatrix()) {
      if (is == null || is.getType() == Material.AIR) {
        continue;
      }
      HiltItemStack loopItem = new HiltItemStack(is);
      if (isEssence(loopItem)) {
        if (getEssenceMaterial(loopItem) != resultStack.getType()) {
          sendMessage(player, plugin.getSettings().getString("language.craft.wrong-ess-type", ""));
          player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 0.7F, 0.5F);
          return;
        }
        highestEssLevel = Math.max(getEssenceLevel(loopItem), highestEssLevel);
        essenceStats.add(getEssenceStat(loopItem));
        continue;
      }
      if (is.getType() == resultStack.getType()) {
        baseItem = loopItem;
        continue;
      }
      System.out.println("ERROR! SOMETHING IS UP WITH ESSENCE CRAFTING!");
      return;
    }
    if (baseItem == null) {
      return;
    }
    List<String> lore = baseItem.getLore();
    List<String> strippedLore = stripColor(lore);
    int itemLevel = NumberUtils
        .toInt(CharMatcher.DIGIT.or(CharMatcher.is('-')).negate().collapseFrom(
            ChatColor.stripColor(strippedLore.get(0)), ' ').trim());
    if (highestEssLevel > itemLevel) {
      sendMessage(player, plugin.getSettings().getString("language.craft.item-too-low", ""));
      player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 0.7F, 0.5F);
      return;
    }
    if (!strippedLore.contains("[ Crafted Stat Slot ]")) {
      sendMessage(player, plugin.getSettings().getString("language.craft.no-slots", ""));
      player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 0.7F, 0.5F);
      return;
    }
    List<String> craftedStatTypes = new ArrayList<>();
    for (String str : lore) {
      if (str.startsWith(ChatColor.AQUA + "")) {
        str = CharMatcher.JAVA_LETTER.or(CharMatcher.is(' '))
            .retainFrom(ChatColor.stripColor(str).trim());
        craftedStatTypes.add(str);
      }
    }
    for (String str : essenceStats) {
      str = CharMatcher.JAVA_LETTER.or(CharMatcher.is(' '))
          .retainFrom(ChatColor.stripColor(str).trim());
      if (craftedStatTypes.contains(str)) {
        sendMessage(player, plugin.getSettings().getString("language.craft.stat-exists", ""));
        player.playSound(player.getEyeLocation(), Sound.BLOCK_LAVA_POP, 0.7F, 0.5F);
        return;
      }
    }
    int selectedSlot =
        random.nextDouble() > 0.35 ? random.nextInt(essenceStats.size()) : random.nextInt(8);
    if (selectedSlot > essenceStats.size() - 1) {
      event.setCurrentItem(baseItem);
      sendMessage(player, plugin.getSettings().getString("language.craft.ess-failed", ""));
      player.playSound(player.getEyeLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1F, 0.5F);
      event.setCancelled(false);
      return;
    }
    int slotIndex = strippedLore.indexOf("[ Crafted Stat Slot ]");
    lore.remove(slotIndex);
    lore.add(slotIndex, ChatColor.AQUA + ChatColor.stripColor(essenceStats.get(selectedSlot)));
    baseItem.setLore(lore);

    event.setCurrentItem(baseItem);
    SkillExperienceUtil.addCraftExperience(player, 0.5f + essenceStats.size());
    sendMessage(player, plugin.getSettings().getString("language.craft.ess-success", ""));
    player.playSound(player.getEyeLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1.5F);
    player.playSound(player.getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 0.7F);
    event.setCancelled(false);
  }

  private boolean isEssence(HiltItemStack itemStack) {
    if (itemStack.getType() != Material.PRISMARINE_SHARD || StringUtils
        .isBlank(itemStack.getName())) {
      return false;
    }
    if (!ChatColor.stripColor(itemStack.getName()).equals("Item Essence")) {
      return false;
    }
    List<String> lore = itemStack.getLore();
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

  private Material getEssenceMaterial(HiltItemStack itemStack) {
    String str = ChatColor.stripColor(itemStack.getLore().get(1)).replace("Item Type: ", "");
    if (str.equalsIgnoreCase("Wand")) {
      return Material.WOOD_SWORD;
    }
    str = str.replace(" ", "_").toUpperCase();
    Material material;
    try {
      material = Material.getMaterial(str);
    } catch (Exception e) {
      System.out.println("INVALID MATERIAL ON ESSENCE! What tf is " + str + "?");
      return null;
    }
    return material;
  }

  private String getEssenceStat(HiltItemStack itemStack) {
    return itemStack.getLore().get(2);
  }

  private boolean hasQuality(HiltItemStack h) {
    return !StringUtils.isBlank(h.getName()) && h.hasItemMeta() && h.getLore().get(1) != null &&
        ChatColor.stripColor(h.getLore().get(1)).startsWith("Quality: ");
  }

  private boolean hasItemLevel(HiltItemStack h) {
    return !StringUtils.isBlank(h.getName()) && h.hasItemMeta() && h.getLore().get(0) != null &&
        ChatColor.stripColor(h.getLore().get(0)).startsWith("Item Level: ");
  }

  private int getEssenceLevel(HiltItemStack h) {
    return NumberUtils.toInt(CharMatcher.DIGIT.or(CharMatcher.is('-')).negate().collapseFrom(
        ChatColor.stripColor(h.getLore().get(0)), ' ').trim());
  }

  private int maxCraftingLevel(int craftLevel) {
    return 5 + (int) Math.floor((double) craftLevel / 5) * 8;
  }
}
