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
package info.faceland.loot.api.creatures;

import com.tealcube.minecraft.bukkit.shade.objecthunter.exp4j.Expression;
import info.faceland.loot.api.enchantments.EnchantmentTome;
import info.faceland.loot.api.items.CustomItem;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.data.JunkItemData;
import org.bukkit.entity.EntityType;

import java.util.Map;

public interface CreatureMod {

  EntityType getEntityType();

  Map<CustomItem, Double> getCustomItemMults();

  Map<SocketGem, Double> getSocketGemMults();

  Map<Tier, Double> getTierMults();

  Map<String, Map<JunkItemData, Double>> getJunkMaps();

  double getCustomItemMult(CustomItem ci);

  double getSocketGemMult(SocketGem sg);

  double getTierMult(Tier t);

  Expression getExperienceExpression();

  Map<EnchantmentTome, Double> getEnchantmentStoneMults();

}
