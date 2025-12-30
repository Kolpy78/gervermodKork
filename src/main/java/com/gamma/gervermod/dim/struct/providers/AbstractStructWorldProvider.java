package com.gamma.gervermod.dim.struct.providers;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;

import com.gamma.gervermod.dim.struct.FastChunkProviderFlat;
import com.hbm.dim.WorldProviderCelestial;

public abstract class AbstractStructWorldProvider extends WorldProviderCelestial {

    private static final Random rand = new Random();
    private long seed = rand.nextLong();

    public void nextSeed() {
        seed = rand.nextLong();
    }

    @Override
    public long getSeed() {
        return seed;
    }

    public abstract BiomeGenBase getBiome();

    public abstract Block[] getBlocks();

    public abstract String getWorldType();

    @Override
    public void registerWorldChunkManager() {
        this.worldChunkMgr = new WorldChunkManagerHell(getBiome(), 0.0F);
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        Block grass = getBlocks()[0];
        Block dirt = getBlocks()[1];
        Block stone = getBlocks()[2];
        return new FastChunkProviderFlat(this.worldObj, getBiome(), grass, dirt, stone);
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public String getDimensionName() {
        return "Structure World (" + getWorldType() + ")";
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
}
