package info.faceland.loot.math;

import java.util.Random;

public final class LootRandom extends Random {

    public LootRandom() {
      super();
    }

    public LootRandom(long seed) {
        super(seed);
    }

    public int nextIntRange(int i1, int i2) {
        int min = Math.min(i1, i2);
        int max = Math.max(i1, i2);
        int diff = Math.abs(max - min);
        return min + nextInt(diff > 0 ? diff : 1);
    }

}
