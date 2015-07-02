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

import info.faceland.loot.api.managers.NameManager;
import info.faceland.loot.math.LootRandom;

import java.util.ArrayList;
import java.util.List;

public final class LootNameManager implements NameManager {

    private final List<String> prefixes;
    private final List<String> suffixes;
    private final LootRandom random;

    public LootNameManager() {
        prefixes = new ArrayList<>();
        suffixes = new ArrayList<>();
        random = new LootRandom(System.currentTimeMillis());
    }

    @Override
    public List<String> getPrefixes() {
        return new ArrayList<>(prefixes);
    }

    @Override
    public List<String> getSuffixes() {
        return new ArrayList<>(suffixes);
    }

    @Override
    public String getRandomPrefix() {
        return getPrefixes().get(random.nextInt(getPrefixes().size()));
    }

    @Override
    public String getRandomSuffix() {
        return getSuffixes().get(random.nextInt(getSuffixes().size()));
    }

    @Override
    public void addPrefix(String s) {
        if (s != null) {
            prefixes.add(s);
        }
    }

    @Override
    public void addSuffix(String s) {
        if (s != null) {
            suffixes.add(s);
        }
    }

    @Override
    public void removePrefix(String s) {
        if (s != null) {
            prefixes.remove(s);
        }
    }

    @Override
    public void removeSuffix(String s) {
        if (s != null) {
            suffixes.remove(s);
        }
    }

}
