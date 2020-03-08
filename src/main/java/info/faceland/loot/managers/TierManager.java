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

import info.faceland.loot.api.tier.Tier;
import info.faceland.loot.math.LootRandom;

import java.util.HashSet;
import java.util.Set;

public final class TierManager {

  private Set<Tier> loadedTiers;
  private LootRandom random;

  public TierManager() {
    loadedTiers = new HashSet<>();
    random = new LootRandom();
  }

  public Tier getTier(String name) {
    for (Tier t : getLoadedTiers()) {
      if (t.getId().replace(" ", "").equalsIgnoreCase(name.replace(" ", ""))) {
        return t;
      }
    }
    return null;
  }

  public void addTier(Tier tier) {
    if (tier != null) {
      loadedTiers.add(tier);
    }
  }

  public void removeTier(String name) {
    Tier t = getTier(name);
    if (t != null) {
      loadedTiers.remove(t);
    }
  }

  public Tier getRandomTier() {
    Set<Tier> allTiers = getLoadedTiers();
    double selectedWeight = random.nextDouble() * getTotalTierWeight();
    double currentWeight = 0;
    for (Tier t : allTiers) {
      double calcWeight = t.getSpawnWeight();
      if (calcWeight >= 0) {
        currentWeight += calcWeight;
      }
      if (currentWeight >= selectedWeight) {
        return t;
      }
    }
    return null;
  }

  public Set<Tier> getLoadedTiers() {
    return new HashSet<>(loadedTiers);
  }

  public double getTotalTierWeight() {
    double weight = 0;
    for (Tier t : getLoadedTiers()) {
      double d = t.getSpawnWeight();
      if (d > 0D) {
        weight += d;
      }
    }
    return weight;
  }
}
