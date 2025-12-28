/*
 * This file is part of Applied Energistics 2. Copyright (c) 2013 - 2015, AlgorithmX2, All rights reserved. Applied
 * Energistics 2 is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Applied Energistics 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details. You should have received a copy of the GNU Lesser General Public License along with
 * Applied Energistics 2. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package com.gamma.gervermod.dim.struct;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.client.IRenderHandler;

public class StructWorldProvider extends WorldProvider {

    public StructWorldProvider() {

    }

    @Override
    protected void registerWorldChunkManager() {
        super.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.plains, 0.0F);
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
        flatgeneratorinfo.setBiome(BiomeGenBase.plains.biomeID);
        flatgeneratorinfo.getFlatLayers()
            .add(new FlatLayerInfo(1, Blocks.bedrock));
        flatgeneratorinfo.getFlatLayers()
            .add(new FlatLayerInfo(67, Blocks.stone));
        flatgeneratorinfo.getFlatLayers()
            .add(new FlatLayerInfo(2, Blocks.dirt));
        flatgeneratorinfo.getFlatLayers()
            .add(new FlatLayerInfo(1, Blocks.grass));
        flatgeneratorinfo.func_82645_d();
        flatgeneratorinfo.getWorldFeatures()
            .put("village", new HashMap<>());
        flatgeneratorinfo.getWorldFeatures()
            .put("biome_1", new HashMap<>());
        flatgeneratorinfo.getWorldFeatures()
            .put("mineshaft", new HashMap<>());
        flatgeneratorinfo.getWorldFeatures()
            .put("decoration", new HashMap<>());
        flatgeneratorinfo.getWorldFeatures()
            .put("lake", new HashMap<>());
        flatgeneratorinfo.getWorldFeatures()
            .put("lava_lake", new HashMap<>());
        flatgeneratorinfo.getWorldFeatures()
            .put("dungeon", new HashMap<>());
        return new ChunkProviderFlat(this.worldObj, (long) (Math.random() * Long.MAX_VALUE), true, flatgeneratorinfo.toString());
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public String getDimensionName() {
        return "Structure World";
    }

    @Override
    public IRenderHandler getSkyRenderer() {
        return WorldProviderSurface.getProviderForDimension(0)
            .getSkyRenderer();
    }

    @Override
    public boolean canSnowAt(final int x, final int y, final int z, final boolean checkLight) {
        return false;
    }

    @Override
    public ChunkCoordinates getSpawnPoint() {
        return new ChunkCoordinates(0, 71, 0);
    }

    @Override
    public boolean isBlockHighHumidity(final int x, final int y, final int z) {
        return false;
    }

    @Override
    public boolean canDoLightning(final Chunk chunk) {
        return false;
    }
}
