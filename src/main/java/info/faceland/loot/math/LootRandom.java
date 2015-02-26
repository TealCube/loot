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
package info.faceland.loot.math;

import java.util.Random;

public final class LootRandom extends Random {

    public LootRandom() {
    }

    public LootRandom(long seed) {
        super(seed);
    }

    @Override
    public double nextDouble() {
        long significand = 0;
        double divisor = 1;

        while (true) {
            int leadingZeros = Long.numberOfLeadingZeros(significand);
            int usefulBits = 64 - leadingZeros;
            int pendingBits = 53 - usefulBits;
            if (pendingBits == 0) {
                break;
            }

            int bits = Math.min(pendingBits, 30);
            significand = (significand << bits) + next(bits);
            divisor = divisor / (1 << bits);
        }

        return significand * divisor;
    }

    public int nextIntRange(int i1, int i2) {
        int min = Math.min(i1, i2);
        int max = Math.max(i1, i2);
        int diff = Math.abs(max - min);
        return min + nextInt(diff);
    }

}
