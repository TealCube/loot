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
package info.faceland.loot.api.managers;

import info.faceland.loot.api.sockets.SocketGem;

import java.util.List;
import java.util.Map;

public interface SocketGemManager {

    List<SocketGem> getSocketGems();

    List<String> getGemNames();

    List<SocketGem> getSortedGems();

    List<String> getSortedGemNames();

    SocketGem getSocketGem(String name);

    void addSocketGem(SocketGem gem);

    void removeSocketGem(String name);

    SocketGem getRandomSocketGem();

    SocketGem getRandomSocketGem(boolean withChance);

    SocketGem getRandomSocketGem(boolean withChance, double distance);

    SocketGem getRandomSocketGem(boolean withChance, double distance, Map<SocketGem, Double> map);

    SocketGem getRandomSocketGemByBonus();

    SocketGem getRandomSocketGemByLevel(int level);

    double getTotalWeight();

    double getTotalWeight(double distance);

    double getTotalWeight(double distance, Map<SocketGem, Double> map);

    double getTotalLevelWeight(int level);

    double getTotalBonusWeight();

}
