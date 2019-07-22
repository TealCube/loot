/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.managers;

import com.tealcube.minecraft.bukkit.TextUtils;
import info.faceland.loot.api.managers.SocketGemManager;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.math.LootRandom;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class LootSocketGemManager implements SocketGemManager {

    private static final double DISTANCE = 1000;
    private static final double DISTANCE_SQUARED = Math.pow(DISTANCE, 2);
    private final Map<String, SocketGem> gemMap;
    private final LootRandom random;

    public LootSocketGemManager() {
        this.gemMap = new HashMap<>();
        this.random = new LootRandom();
    }

    @Override
    public List<SocketGem> getSocketGems() {
        return new ArrayList<>(gemMap.values());
    }

    @Override
    public List<String> getGemNames() {
        return new ArrayList<>(gemMap.keySet());
    }

    @Override
    public List<SocketGem> getSortedGems() {
        List<SocketGem> gems = getSocketGems();
        Collections.sort(gems);
        return gems;
    }

    @Override
    public List<String> getSortedGemNames() {
        List<String> l = getGemNames();
        Collections.sort(l);
        return l;
    }

    @Override
    public SocketGem getSocketGem(String name) {
        if (gemMap.containsKey(name.toLowerCase())) {
            return gemMap.get(name.toLowerCase());
        }
        if (gemMap.containsKey(name.toLowerCase().replace(" ", "_"))) {
            return gemMap.get(name.toLowerCase().replace(" ", "_"));
        }
        if (gemMap.containsKey(name.toLowerCase().replace("_", " "))) {
            return gemMap.get(name.toLowerCase().replace("_", " "));
        }
        return null;
    }

    @Override
    public void addSocketGem(SocketGem gem) {
        if (gem != null) {
            gemMap.put(gem.getName().toLowerCase(), gem);
        }
    }

    @Override
    public void removeSocketGem(String name) {
        if (name != null) {
            gemMap.remove(name.toLowerCase());
        }
    }

    @Override
    public SocketGem getRandomSocketGem() {
        return getRandomSocketGem(false);
    }

    @Override
    public SocketGem getRandomSocketGem(boolean withChance) {
        return getRandomSocketGem(withChance, 0D);
    }

    @Override
    public SocketGem getRandomSocketGem(boolean withChance, double distance) {
        return getRandomSocketGem(withChance, distance, new HashMap<SocketGem, Double>());
    }

    @Override
    public SocketGem getRandomSocketGem(boolean withChance, double distance, Map<SocketGem, Double> map) {
        if (!withChance) {
            List<SocketGem> gems = getSocketGems();
            SocketGem[] array = gems.toArray(new SocketGem[gems.size()]);
            return array[random.nextInt(array.length)];
        }
        double selectedWeight = random.nextDouble() * getTotalWeight(distance, map);
        double currentWeight = 0D;
        List<SocketGem> gems = getSocketGems();
        for (SocketGem sg : gems) {
            double calcWeight = sg.getWeight() + ((distance / DISTANCE_SQUARED) * sg.getDistanceWeight());
            if (map.containsKey(sg)) {
                calcWeight *= map.get(sg);
            }
            currentWeight += calcWeight;
            if (currentWeight >= selectedWeight) {
                return sg;
            }
        }
        return null;
    }

    @Override
    public SocketGem getRandomSocketGemByBonus() {
        double totalWeight = getTotalBonusWeight();
        double chosenWeight = random.nextDouble() * totalWeight;
        double currentWeight = 0;
        for (SocketGem sg : getSocketGems()) {
            currentWeight += sg.getBonusWeight();
            if (currentWeight >= chosenWeight) {
                return sg;
            }
        }
        return null;
    }

    @Override
    public SocketGem getRandomSocketGemByLevel(int level) {
        double totalWeight = getTotalLevelWeight(level);
        double chosenWeight = random.nextDouble() * totalWeight;
        double currentWeight = 0;
        for (SocketGem sg : getSocketGems()) {
            currentWeight += sg.getWeight() + sg.getWeightPerLevel() * level;
            if (currentWeight >= chosenWeight) {
                return sg;
            }
        }
        return null;
    }

    @Override
    public double getTotalWeight() {
        return getTotalWeight(0);
    }

    @Override
    public double getTotalWeight(double distance) {
        return getTotalWeight(distance, new HashMap<SocketGem, Double>());
    }

    @Override
    public double getTotalWeight(double distance, Map<SocketGem, Double> map) {
        double totalWeight = 0;
        List<SocketGem> gems = getSocketGems();
        for (SocketGem sg : gems) {
            double calcWeight = sg.getWeight() + ((distance / DISTANCE_SQUARED) * sg.getDistanceWeight());
            if (map.containsKey(sg)) {
                calcWeight *= map.get(sg);
            }
            totalWeight += calcWeight;
        }
        return totalWeight;
    }

    @Override
    public double getTotalLevelWeight(int level) {
        double totalWeight = 0;
        List<SocketGem> gems = getSocketGems();
        for (SocketGem sg : gems) {
            double calcWeight = sg.getWeight() + sg.getWeightPerLevel() * level;
            totalWeight += calcWeight;
        }
        return totalWeight;
    }

    @Override
    public double getTotalBonusWeight() {
        double totalWeight = 0;
        List<SocketGem> gems = getSocketGems();
        for (SocketGem sg : gems) {
            totalWeight += sg.getBonusWeight();
        }
        return totalWeight;
    }

    @Override
    public Set<SocketGem> getGems(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return new HashSet<>();
        }
        Set<SocketGem> gems = new HashSet<>();
        List<String> lore = ItemStackExtensionsKt.getLore(itemStack);
        List<String> strippedLore = new ArrayList<>();
        for (String s : lore) {
            strippedLore.add(ChatColor.stripColor(s));
        }
        for (String key : strippedLore) {
            SocketGem gem = getSocketGem(key);
            if (gem == null) {
                for (SocketGem g : getSocketGems()) {
                    if (!g.isTriggerable()) {
                        continue;
                    }
                    if (key.equals(ChatColor.stripColor(TextUtils.color(
                            g.getTriggerText() != null ? g.getTriggerText() : "")))) {
                        gem = g;
                        break;
                    }
                }
                if (gem == null) {
                    continue;
                }
            }
            gems.add(gem);
        }
        return gems;
    }


}
