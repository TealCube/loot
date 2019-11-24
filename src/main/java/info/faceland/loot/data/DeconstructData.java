package info.faceland.loot.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class DeconstructData {

  private Material material;
  private int minCustomData;
  private int maxCustomData;
  private List<Material> results = new ArrayList<>();

  private static Random random = new Random();

  public Material getMaterial() {
    return material;
  }

  public void setMaterial(Material material) {
    this.material = material;
  }

  public int getMinCustomData() {
    return minCustomData;
  }

  public void setMinCustomData(int minCustomData) {
    this.minCustomData = minCustomData;
  }

  public int getMaxCustomData() {
    return maxCustomData;
  }

  public void setMaxCustomData(int maxCustomData) {
    this.maxCustomData = maxCustomData;
  }

  public static void addResult(DeconstructData data, Material material) {
    data.results.add(material);
  }

  public static Material getResultMaterial(DeconstructData data, Set<Material> validMats) {
    Material material = data.results.get(random.nextInt(data.results.size()));
    if (validMats.contains(material)) {
      return material;
    }
    Bukkit.getLogger().warning(
        "Deconstruct data for " + data.getMaterial() + " has invalid material" + material);
    return null;
  }
}
