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
package info.faceland.loot.managers;

import info.faceland.loot.api.creatures.CreatureMod;
import info.faceland.loot.api.managers.CreatureModManager;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class LootCreatureModManager implements CreatureModManager {

  private final Map<EntityType, CreatureMod> creatureModMap;

  public LootCreatureModManager() {
    this.creatureModMap = new HashMap<>();
  }

  @Override
  public Set<CreatureMod> getCreatureMods() {
    return new HashSet<>(creatureModMap.values());
  }

  @Override
  public CreatureMod getCreatureMod(EntityType entityType) {
    return creatureModMap.get(entityType);
  }

  @Override
  public void addCreatureMod(CreatureMod mod) {
    if (mod != null) {
      creatureModMap.put(mod.getEntityType(), mod);
    }
  }

  @Override
  public void removeCreatureMod(EntityType entityType) {
    if (entityType != null) {
      creatureModMap.remove(entityType);
    }
  }

}
