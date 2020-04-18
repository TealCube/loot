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
package info.faceland.loot.listeners;

import info.faceland.loot.LootPlugin;
import info.faceland.loot.math.LootRandom;
import info.faceland.loot.utils.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public final class EnchantDegradeListener implements Listener {

  private final LootPlugin plugin;
  private LootRandom random;

  public EnchantDegradeListener(LootPlugin plugin) {
    this.plugin = plugin;
    this.random = new LootRandom();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onEntityDeath(EntityDeathEvent event) {
    LivingEntity dyingEntity = event.getEntity();
    Player p = dyingEntity.getKiller();
    if (p == null) {
      return;
    }
    if (random.nextDouble() > plugin.getSettings().getDouble("config.enchantment-degrade", 1.0)) {
      return;
    }
    ItemStack item;
    switch (random.nextInt(6)) {
      case 0:
        item = p.getEquipment().getItemInMainHand();
        break;
      case 1:
        item = p.getEquipment().getItemInOffHand();
        break;
      case 2:
        item = p.getEquipment().getHelmet();
        break;
      case 3:
        item = p.getEquipment().getChestplate();
        break;
      case 4:
        item = p.getEquipment().getLeggings();
        break;
      case 5:
        item = p.getEquipment().getBoots();
        break;
      default:
        item = null;
    }
    if (item == null || item.getType() == Material.AIR) {
      return;
    }
    MaterialUtil.degradeItemEnchantment(item, p);
  }
}