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
        int diff = max - min;
        if (diff <= 0) {
            return min;
        }
        return min + nextInt(diff);
    }

}
