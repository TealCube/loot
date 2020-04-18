/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.data;

import java.util.List;

public final class UpgradeScroll {

    private String id;
    private String prefix;
    private List<String> lore;
    private double baseSuccess;
    private double flatDecay;
    private double percentDecay;
    private double exponent;
    private double weight;
    private double itemDamageMultiplier;
    private int minLevel;
    private int maxLevel;
    private int customData;
    private boolean broadcast;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public double getBaseSuccess() {
        return baseSuccess;
    }

    public void setBaseSuccess(double baseSuccess) {
        this.baseSuccess = baseSuccess;
    }

    public double getFlatDecay() {
        return flatDecay;
    }

    public void setFlatDecay(double flatDecay) {
        this.flatDecay = flatDecay;
    }

    public double getPercentDecay() {
        return percentDecay;
    }

    public void setPercentDecay(double percentDecay) {
        this.percentDecay = percentDecay;
    }

    public double getExponent() {
        return exponent;
    }

    public void setExponent(double exponent) {
        this.exponent = exponent;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getItemDamageMultiplier() {
        return itemDamageMultiplier;
    }

    public void setItemDamageMultiplier(double itemDamageMultiplier) {
        this.itemDamageMultiplier = itemDamageMultiplier;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getCustomData() {
        return customData;
    }

    public void setCustomData(int customData) {
        this.customData = customData;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

}
