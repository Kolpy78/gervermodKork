package com.gamma.gervermod.dim.struct;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;

import java.util.Random;

public class StructWorldDesert extends WorldProvider {

    public StructWorldDesert() {

    }

    private static final Random rand = new Random();
    private long seed = rand.nextLong();

    void nextSeed() {
        seed = rand.nextLong();
    }

    @Override
    protected void registerWorldChunkManager() {
        super.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.desert, 0.0F);
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        Block sand = Block.getBlockById(12);
        Block sandstone = Block.getBlockById(24);
        Block stone = Block.getBlockById(1);
        return new FastChunkProviderFlat(this.worldObj, sand, sandstone, stone);
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public String getDimensionName() {
        return "Desert Structure World";
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

