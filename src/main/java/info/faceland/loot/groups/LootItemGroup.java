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

package info.faceland.loot.groups;

import info.faceland.loot.api.groups.ItemGroup;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public final class LootItemGroup implements ItemGroup {

    private final String name;
    private final boolean inverse;
    private final Set<Material> legalMaterials;

    public LootItemGroup(String name, boolean inv) {
        this(name, new HashSet<Material>(), inv);
    }

    public LootItemGroup(String name, Set<Material> materials, boolean inv) {
        this.name = name;
        this.legalMaterials = materials;
        this.inverse = inv;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (inverse ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LootItemGroup that = (LootItemGroup) o;

        return inverse == that.inverse && !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public Set<Material> getMaterials() {
        return new HashSet<>(legalMaterials);
    }

    @Override
    public void addMaterial(Material material) {
        legalMaterials.add(material);
    }

    @Override
    public void removeMaterial(Material material) {
        legalMaterials.remove(material);
    }

    @Override
    public boolean hasMaterial(Material material) {
        return legalMaterials.contains(material);
    }

    @Override
    public boolean isInverse() {
        return inverse;
    }

    @Override
    public ItemGroup getInverse() {
        return new LootItemGroup(name, legalMaterials, !inverse);
    }

}
