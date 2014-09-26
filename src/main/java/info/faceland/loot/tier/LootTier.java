package info.faceland.loot.tier;

import info.faceland.loot.api.groups.ItemGroup;
import info.faceland.loot.api.tier.Tier;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public final class LootTier implements Tier {

    private final String name;
    private String displayName;
    private ChatColor displayColor;
    private ChatColor identificationColor;
    private double spawnWeight;
    private double identifyWeight;
    private int minimumSockets;
    private int maximumSockets;
    private int minimumBonusLore;
    private int maximumBonusLore;
    private List<ItemGroup> itemGroups;
    private List<String> baseLore;
    private List<String> bonusLore;
    private double minimumDurability;
    private double maximumDurability;
    private double optimalSpawnDistance;
    private double maximumRadiusFromOptimalSpawnDistance;

    public LootTier(String name) {
        this.name = name;
        this.itemGroups = new ArrayList<>();
        this.baseLore = new ArrayList<>();
        this.bonusLore = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public ChatColor getDisplayColor() {
        return displayColor;
    }

    void setDisplayColor(ChatColor displayColor) {
        this.displayColor = displayColor;
    }

    @Override
    public ChatColor getIdentificationColor() {
        return identificationColor;
    }

    void setIdentificationColor(ChatColor identificationColor) {
        this.identificationColor = identificationColor;
    }

    @Override
    public double getMinimumDurability() {
        return minimumDurability;
    }

    void setMinimumDurability(double minimumDurability) {
        this.minimumDurability = minimumDurability;
    }

    @Override
    public double getMaximumDurability() {
        return maximumDurability;
    }

    @Override
    public double getSpawnWeight() {
        return spawnWeight;
    }

    void setSpawnWeight(double spawnWeight) {
        this.spawnWeight = spawnWeight;
    }

    @Override
    public double getOptimalSpawnDistance() {
        return optimalSpawnDistance;
    }

    void setOptimalSpawnDistance(double optimalSpawnDistance) {
        this.optimalSpawnDistance = optimalSpawnDistance;
    }

    @Override
    public double getMaximumRadiusFromOptimalSpawnDistance() {
        return maximumRadiusFromOptimalSpawnDistance;
    }

    @Override
    public double getIdentifyWeight() {
        return identifyWeight;
    }

    void setIdentifyWeight(double identifyWeight) {
        this.identifyWeight = identifyWeight;
    }

    @Override
    public List<String> getBaseLore() {
        return new ArrayList<>(baseLore);
    }

    void setBaseLore(List<String> baseLore) {
        if (baseLore == null) {
            this.baseLore.clear();
        } else {
            this.baseLore = baseLore;
        }
    }

    @Override
    public List<String> getBonusLore() {
        return new ArrayList<>(bonusLore);
    }

    @Override
    public List<ItemGroup> getItemGroups() {
        return new ArrayList<>(itemGroups);
    }

    @Override
    public int getMinimumSockets() {
        return minimumSockets;
    }

    void setMinimumSockets(int minimumSockets) {
        this.minimumSockets = minimumSockets;
    }

    @Override
    public int getMaximumSockets() {
        return maximumSockets;
    }

    void setMaximumSockets(int maximumSockets) {
        this.maximumSockets = maximumSockets;
    }

    @Override
    public int getMinimumBonusLore() {
        return minimumBonusLore;
    }

    void setMinimumBonusLore(int minimumBonusLore) {
        this.minimumBonusLore = minimumBonusLore;
    }

    @Override
    public int getMaximumBonusLore() {
        return maximumBonusLore;
    }

    void setMaximumBonusLore(int maximumBonusLore) {
        this.maximumBonusLore = maximumBonusLore;
    }

    void setItemGroups(List<ItemGroup> itemGroups) {
        if (itemGroups == null) {
            this.itemGroups.clear();
        } else {
            this.itemGroups = itemGroups;
        }
    }

    void setBonusLore(List<String> bonusLore) {
        if (bonusLore == null) {
            this.bonusLore.clear();
        } else {
            this.bonusLore = bonusLore;
        }
    }

    void setMaximumRadiusFromOptimalSpawnDistance(double maximumRadiusFromOptimalSpawnDistance) {
        this.maximumRadiusFromOptimalSpawnDistance = maximumRadiusFromOptimalSpawnDistance;
    }

    void setMaximumDurability(double maximumDurability) {
        this.maximumDurability = maximumDurability;
    }

    @Override
    public int compareTo(Tier o) {
        if (o == null) {
            return 1;
        }
        if (this.equals(o)) {
            return 0;
        }
        return Double.compare(getSpawnWeight(), o.getSpawnWeight());
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LootTier lootTier = (LootTier) o;

        return !(name != null ? !name.equals(lootTier.name) : lootTier.name != null);
    }

}
