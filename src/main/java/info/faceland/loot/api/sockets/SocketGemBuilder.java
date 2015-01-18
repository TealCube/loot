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
package info.faceland.loot.api.sockets;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.sockets.effects.SocketEffect;

import java.util.List;

public interface SocketGemBuilder {

    boolean isBuilt();

    SocketGem build();

    SocketGemBuilder withWeight(double d);

    SocketGemBuilder withPrefix(String s);

    SocketGemBuilder withSuffix(String s);

    SocketGemBuilder withLore(List<String> l);

    SocketGemBuilder withSocketEffects(List<SocketEffect> effects);

    SocketGemBuilder withItemGroups(List<ItemGroup> itemGroups);

    SocketGemBuilder withDistanceWeight(double d);

    SocketGemBuilder withBroadcast(boolean b);

    SocketGemBuilder withTriggerable(boolean b);

    SocketGemBuilder withTriggerText(String s);
}
