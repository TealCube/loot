/******************************************************************************
 * Copyright (c) 2014, Richard Harrah                                         *
 *                                                                            *
 * Permission to use, copy, modify, and/or distribute this software for any   *
 * purpose with or without fee is hereby granted, provided that the above     *
 * copyright notice and this permission notice appear in all copies.          *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES   *
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF           *
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR    *
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES     *
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN      *
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF    *
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.             *
 ******************************************************************************/

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
