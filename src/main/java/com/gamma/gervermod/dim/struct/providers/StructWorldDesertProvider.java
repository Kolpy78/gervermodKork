package com.gamma.gervermod.dim.struct.providers;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class StructWorldDesertProvider extends AbstractStructWorldProvider {

    @Override
    public BiomeGenBase getBiome() {
        return BiomeGenBase.desert;
    }

    @Override
    public Block[] getBlocks() {
        return new Block[] { Blocks.sand, Blocks.sandstone, Blocks.stone };
    }

    @Override
    public String getWorldType() {
        return "Desert";
    }
}
