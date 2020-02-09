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
package info.faceland.loot.menu.upgrade;

import com.tealcube.minecraft.bukkit.TextUtils;
import info.faceland.loot.LootPlugin;
import info.faceland.loot.data.ItemStat;
import info.faceland.loot.data.UpgradeScroll;
import info.faceland.loot.enchantments.EnchantmentTome;
import info.faceland.loot.menu.BlankIcon;
import info.faceland.loot.utils.inventory.MaterialUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import land.face.strife.data.champion.LifeSkillType;
import land.face.strife.util.PlayerDataUtil;
import ninja.amp.ampmenus.menus.ItemMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantMenu extends ItemMenu {

  private LootPlugin plugin;

  private ItemStack selectedEquipment;
  private ItemStack selectedUpgradeItem;

  private ConfirmIcon confirmIcon;
  private EquipmentIcon equipmentIcon;
  private UpgradeItemIcon upgradeItemIcon;

  private String validEnchant;
  private String invalidEnchant;
  private String validUpgrade;
  private String invalidUpgrade;
  private String validExtend;
  private String invalidExtend;
  private List<String> noEquipmentLore;
  private List<String> noUpgradeItemLore;
  private List<String> validEnchantLore;
  private List<String> noEnchantTagLore;
  private List<String> badTomeTypeLore;
  private List<String> invalidUpgradeLore;
  private List<String> badScrollRangeLore;
  private List<String> validUpgradeLore;
  private List<String> validExtendLore;
  private List<String> invalidExtendLore;

  private ItemStack blankItem;

  private static final DecimalFormat DF = new DecimalFormat("#.#");

  public EnchantMenu(LootPlugin plugin) {
    super(TextUtils.color(plugin.getSettings().getString("language.menu.menu-name",
        "&0&lUpgrade Items!")), Size.fit(27), plugin);

    this.plugin = plugin;

    validEnchant = TextUtils.color(plugin.getSettings()
        .getString("language.menu.valid-enchant-name", "aaaa"));
    validUpgrade = TextUtils.color(plugin.getSettings()
        .getString("language.menu.valid-upgrade-name", "aaaa"));
    validExtend = TextUtils.color(plugin.getSettings()
        .getString("language.menu.valid-extend-name", "aaaa"));
    invalidEnchant = TextUtils.color(plugin.getSettings()
        .getString("language.menu.invalid-enchant-name", "aaaa"));
    invalidUpgrade = TextUtils.color(plugin.getSettings()
        .getString("language.menu.invalid-upgrade-name", "aaaa"));
    invalidExtend = TextUtils.color(plugin.getSettings()
        .getString("language.menu.invalid-extend-name", "aaaa"));

    noEquipmentLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.no-equipment"));
    noUpgradeItemLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.no-upgrade"));
    validEnchantLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.valid-enchant-lore"));
    noEnchantTagLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.no-tag-lore"));
    badTomeTypeLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.type-mismatch-lore"));
    invalidUpgradeLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.invalid-upgrade-lore"));
    badScrollRangeLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.invalid-plus-range-lore"));
    validUpgradeLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.valid-upgrade-lore"));
    validExtendLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.valid-extend-lore"));
    invalidExtendLore = TextUtils.color(plugin.getSettings()
        .getStringList("language.menu.invalid-extend-lore"));

    blankItem = new ItemStack(Material.AIR);

    confirmIcon = new ConfirmIcon(this);
    equipmentIcon = new EquipmentIcon(this);
    upgradeItemIcon = new UpgradeItemIcon(this);

    fillEmptySlots(new BlankIcon());
    setItem(10, equipmentIcon);
    setItem(12, upgradeItemIcon);
    setItem(16, confirmIcon);
  }

  public void setSelectedEquipment(Player player, ItemStack selectedEquipment) {
    this.selectedEquipment = selectedEquipment;
    equipmentIcon.getIcon().setAmount(selectedEquipment.getAmount());
    equipmentIcon.getIcon().setType(selectedEquipment.getType());
    equipmentIcon.getIcon().setItemMeta(selectedEquipment.getItemMeta());
    equipmentIcon.setDisplayName(ItemStackExtensionsKt.getDisplayName(selectedEquipment));
    updateConfirmIcon(player);
  }

  public void setSelectedUpgradeItem(Player player, ItemStack selectedUpgradeItem) {
    this.selectedUpgradeItem = selectedUpgradeItem;
    upgradeItemIcon.getIcon().setAmount(selectedUpgradeItem.getAmount());
    upgradeItemIcon.getIcon().setType(selectedUpgradeItem.getType());
    upgradeItemIcon.getIcon().setItemMeta(selectedUpgradeItem.getItemMeta());
    upgradeItemIcon.setDisplayName(ItemStackExtensionsKt.getDisplayName(selectedUpgradeItem));
    updateConfirmIcon(player);
  }

  private void updateConfirmIcon(Player player) {
    confirmIcon.getIcon().setType(Material.BARRIER);
    List<String> lore = new ArrayList<>();
    if (selectedEquipment == null || selectedEquipment.getType() == Material.AIR) {
      confirmIcon.setDisplayName(TextUtils.color("&eNo Equipment Item..."));
      lore.addAll(noEquipmentLore);
      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    if (selectedUpgradeItem == null || selectedUpgradeItem.getType() == Material.AIR) {
      confirmIcon.setDisplayName(TextUtils.color("&eNo Upgrade Item..."));
      lore.addAll(noUpgradeItemLore);
      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    if (MaterialUtil.isEnchantmentItem(selectedUpgradeItem)) {
      if (!MaterialUtil.hasEnchantmentTag(selectedEquipment)) {
        confirmIcon.setDisplayName(invalidEnchant);
        lore.addAll(noEnchantTagLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      if (!MaterialUtil.isMatchingGroup(MaterialUtil.getEnchantmentItem(selectedUpgradeItem),
          selectedEquipment.getType())) {
        confirmIcon.setDisplayName(invalidEnchant);
        lore.addAll(badTomeTypeLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      confirmIcon.setDisplayName(validEnchant);
      confirmIcon.getIcon().setType(Material.NETHER_STAR);

      EnchantmentTome tome = MaterialUtil.getEnchantmentItem(selectedUpgradeItem);
      int itemLevel = MaterialUtil.getItemLevel(selectedEquipment);
      double enchantLevel = PlayerDataUtil
          .getEffectiveLifeSkill(player, LifeSkillType.ENCHANTING, true);
      double effectiveLevel = Math.max(1, Math.min(enchantLevel, itemLevel));
      ItemStat stat = LootPlugin.getInstance().getStatManager().getStat(tome.getStat());
      double rarityBonus = MaterialUtil.getBaseEnchantBonus(enchantLevel);

      String minStat = ChatColor.stripColor(
          TextUtils.color((plugin.getStatManager().getMinStat(stat, effectiveLevel, rarityBonus))));
      String maxStat = ChatColor.stripColor(TextUtils
          .color(plugin.getStatManager().getMaxStat(stat, effectiveLevel, 1 + rarityBonus)));
      for (String s : validEnchantLore) {
        lore.add(s.replace("{min}", minStat).replace("{max}", maxStat));
      }

      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    if (MaterialUtil.isExtender(selectedUpgradeItem)) {
      if (!MaterialUtil.canBeExtended(new ArrayList<>(ItemStackExtensionsKt.getLore(selectedEquipment)))) {
        confirmIcon.setDisplayName(invalidExtend);
        lore.addAll(invalidExtendLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      confirmIcon.setDisplayName(validExtend);
      confirmIcon.getIcon().setType(Material.NETHER_STAR);
      lore.addAll(validExtendLore);

      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
      return;
    }
    UpgradeScroll scroll = plugin.getScrollManager().getScroll(selectedUpgradeItem);
    if (scroll != null) {
      if (!MaterialUtil.isUpgradePossible(selectedEquipment)) {
        confirmIcon.setDisplayName(invalidUpgrade);
        lore.addAll(invalidUpgradeLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      String equipName = ItemStackExtensionsKt.getDisplayName(selectedEquipment);
      int itemPlus = MaterialUtil.getUpgradeLevel(equipName);
      if (!MaterialUtil.meetsUpgradeRange(scroll, itemPlus)) {
        confirmIcon.setDisplayName(invalidUpgrade);
        lore.addAll(badScrollRangeLore);
        ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
        return;
      }
      confirmIcon.setDisplayName(validUpgrade);
      confirmIcon.getIcon().setType(Material.NETHER_STAR);
      if (selectedEquipment.getType() == Material.BOOK
          || selectedEquipment.getType() == Material.ARROW) {
        itemPlus += 3;
      } else {
        itemPlus += 1;
      }
      itemPlus = Math.min(itemPlus, 15);
      double successChance = Math.min(100, 100 * MaterialUtil.getSuccessChance(player, itemPlus,
          selectedUpgradeItem, scroll));
      double maxDura = selectedEquipment.getType().getMaxDurability();
      double maxDamage = maxDura * MaterialUtil.getMaxFailureDamagePercent(scroll, itemPlus);
      double damage = selectedEquipment.getDurability();
      double killChance = 0;
      double damageChance = 0;
      if (successChance < 99.9) {
        double failChance = 100 - successChance;
        if (maxDamage == 0) {
          killChance = failChance;
        } else {
          killChance = failChance * Math.max(0, ((damage + maxDamage) - maxDura) / maxDamage);
        }
        damageChance = failChance - killChance;
      }

      for (String s : validUpgradeLore) {
        lore.add(s.replace("{succ}", DF.format(successChance))
            .replace("{dam}", DF.format(damageChance))
            .replace("{ded}", DF.format(killChance)));
      }

      ItemStackExtensionsKt.setLore(confirmIcon.getIcon(), lore);
    }
  }

  boolean doUpgrade(Player player) {
    if (selectedEquipment == null || selectedEquipment.getType() == Material.AIR) {
      player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_HURT, 1, 0.8f);
      return false;
    }
    if (MaterialUtil.isEnchantmentItem(selectedUpgradeItem)) {
      MaterialUtil.enchantItem(player, selectedUpgradeItem, selectedEquipment);
    } else if (plugin.getScrollManager().getScroll(selectedUpgradeItem) != null) {
      MaterialUtil.upgradeItem(player, selectedUpgradeItem, selectedEquipment);
    } else if (MaterialUtil.isExtender(selectedUpgradeItem)) {
      MaterialUtil.extendItem(player, selectedEquipment, selectedUpgradeItem);
    } else {
      player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_HURT, 1, 0.8f);
      return false;
    }
    setSelectedUpgradeItem(player, selectedUpgradeItem);
    setSelectedEquipment(player, selectedEquipment);
    return true;
  }

  ItemStack getBlankItem() {
    return blankItem;
  }

}

/*
00 01 02 03 04 05 06 07 08
09 10 11 12 13 14 15 16 17
18 19 20 21 22 23 24 25 26
27 28 29 30 31 32 33 34 35
36 37 38 39 40 41 42 43 44
45 46 47 48 49 50 51 52 53
*/
