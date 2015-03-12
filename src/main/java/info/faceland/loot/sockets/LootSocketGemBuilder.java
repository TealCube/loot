/*
 * This file is part of Loot, licensed under the ISC License.
 *
 * Copyright (c) 2014 Richard Harrah
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */
package info.faceland.loot.sockets;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.sockets.SocketGem;
import info.faceland.loot.api.sockets.SocketGemBuilder;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import java.util.List;

public final class LootSocketGemBuilder implements SocketGemBuilder {

    private boolean built = false;
    private LootSocketGem gem;

    public LootSocketGemBuilder(String name) {
        this.gem = new LootSocketGem(name);
    }

    @Override
    public boolean isBuilt() {
        return built;
    }

    @Override
    public SocketGem build() {
        if (isBuilt()) {
            throw new IllegalStateException("already built");
        }
        built = true;
        return gem;
    }

    @Override
    public SocketGemBuilder withWeight(double d) {
        gem.setWeight(d);
        return this;
    }

    @Override
    public SocketGemBuilder withPrefix(String s) {
        gem.setPrefix(s);
        return this;
    }

    @Override
    public SocketGemBuilder withSuffix(String s) {
        gem.setSuffix(s);
        return this;
    }

    @Override
    public SocketGemBuilder withLore(List<String> l) {
        gem.setLore(l);
        return this;
    }

    @Override
    public SocketGemBuilder withSocketEffects(List<SocketEffect> effects) {
        gem.setSocketEffects(effects);
        return this;
    }

    @Override
    public SocketGemBuilder withItemGroups(List<ItemGroup> itemGroups) {
        gem.setItemGroups(itemGroups);
        return this;
    }

    @Override
    public SocketGemBuilder withDistanceWeight(double d) {
        gem.setDistanceWeight(d);
        return this;
    }

    @Override
    public SocketGemBuilder withBroadcast(boolean b) {
        gem.setBroadcast(b);
        return this;
    }

    @Override
    public SocketGemBuilder withTriggerable(boolean b) {
        gem.setTriggerable(b);
        return this;
    }

    @Override
    public SocketGemBuilder withTriggerText(String s) {
        gem.setTriggerText(s);
        return this;
    }

    @Override
    public SocketGemBuilder withBonusWeight(double d) {
        gem.setBonusWeight(d);
        return this;
    }

}
