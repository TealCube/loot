/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package info.faceland.loot.managers;

import info.faceland.loot.anticheat.AnticheatTag;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.entity.LivingEntity;

public final class AnticheatManager {

  private final Map<LivingEntity, AnticheatTag> anticheatTagMap;

  public AnticheatManager() {
    this.anticheatTagMap = new WeakHashMap<>();
  }

  public boolean isTagged(LivingEntity entity) {
    return entity != null && anticheatTagMap.containsKey(entity);
  }

  public boolean addTag(LivingEntity entity) {
    return entity != null && anticheatTagMap.put(entity, new AnticheatTag(entity)) != null;
  }

  public void removeTag(LivingEntity entity) {
    anticheatTagMap.remove(entity);
  }

  public boolean pushTag(LivingEntity entity, AnticheatTag tag) {
    return entity != null && tag != null && anticheatTagMap.put(entity, tag) != null;
  }

  public AnticheatTag getTag(LivingEntity entity) {
    if (entity == null) {
      throw new IllegalArgumentException("entity cannot be null");
    }
    if (!isTagged(entity)) {
      throw new IllegalArgumentException("entity does not have tag");
    }
    return anticheatTagMap.get(entity);
  }

}
