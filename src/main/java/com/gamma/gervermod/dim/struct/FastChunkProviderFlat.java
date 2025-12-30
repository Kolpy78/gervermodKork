package com.gamma.gervermod.dim.struct;

import static com.gamma.gervermod.core.GerverMod.eidLoaded;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;

import com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook;

import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class FastChunkProviderFlat implements IChunkProvider {

    private final World worldObj;
    private final Random random;
    private final Block[] cachedBlockIDs = new Block[256];
    private final List<MapGenStructure> structureGenerators = new ArrayList<>();


    public FastChunkProviderFlat(World p_i2004_1_, Block topBlock, Block middleBlock, Block bottomBlock) {
        this.worldObj = p_i2004_1_;
        this.random = new Random();
        Map<String, String> map1 = new Object2ObjectOpenHashMap<>();
        map1.put("size", "1");
        this.structureGenerators.add(new MapGenVillage(map1));

        this.structureGenerators.add(new MapGenScatteredFeature(Object2ObjectMaps.emptyMap()));

        this.structureGenerators.add(new MapGenMineshaft(Object2ObjectMaps.emptyMap()));

        // this.worldObj.getWorldInfo()
        // .setTerrainType(WorldType.FLAT);

        this.cachedBlockIDs[0] = Blocks.bedrock;
        Arrays.fill(this.cachedBlockIDs, 1, 68, bottomBlock);
        Arrays.fill(this.cachedBlockIDs, 68, 70, middleBlock);
        this.cachedBlockIDs[70] = topBlock;
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(int p_73158_1_, int p_73158_2_) {
        return this.provideChunk(p_73158_1_, p_73158_2_);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int p_73154_1_, int p_73154_2_) {
        Chunk chunk = new Chunk(this.worldObj, p_73154_1_, p_73154_2_);
        int l;

        for (int k = 0; k < this.cachedBlockIDs.length; ++k) {
            Block block = this.cachedBlockIDs[k];

            if (block != null) {
                l = k >> 4;
                ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[l];

                if (extendedblockstorage == null) {
                    extendedblockstorage = new ExtendedBlockStorage(k, !this.worldObj.provider.hasNoSky);
                    chunk.getBlockStorageArray()[l] = extendedblockstorage;
                }

                for (int i1 = 0; i1 < 16; ++i1) {
                    for (int j1 = 0; j1 < 16; ++j1) {
                        extendedblockstorage.func_150818_a(i1, k & 15, j1, block);
                    }
                }
            }
        }

        chunk.generateSkylightMap();
        BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager()
            .loadBlockGeneratorData(null, p_73154_1_ * 16, p_73154_2_ * 16, 16, 16);

        if (eidLoaded) {
            short[] ashort = ((ChunkBiomeHook) chunk).getBiomeShortArray();
            for (l = 0; l < ashort.length; ++l) {
                ashort[l] = (short) abiomegenbase[l].biomeID;
            }
        } else {
            byte[] abyte = chunk.getBiomeArray();
            for (l = 0; l < abyte.length; ++l) {
                abyte[l] = (byte) abiomegenbase[l].biomeID;
            }
        }

        for (MapGenBase mapgenbase : this.structureGenerators)
            mapgenbase.func_151539_a(this, this.worldObj, p_73154_1_, p_73154_2_, null);

        chunk.generateSkylightMap();
        return chunk;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {
        int k = p_73153_2_ << 4;
        int l = p_73153_3_ << 4;
        this.random.setSeed(this.worldObj.getSeed());
        long i1 = ((this.random.nextLong() >> 1) << 1) + 1;
        long j1 = ((this.random.nextLong() >> 1) << 1) + 1;
        this.random.setSeed((long) p_73153_2_ * i1 + (long) p_73153_3_ * j1 ^ this.worldObj.getSeed());

        for (MapGenStructure structureGenerator : this.structureGenerators) {
            structureGenerator.generateStructuresInChunk(this.worldObj, this.random, p_73153_2_, p_73153_3_);
        }

        for (int l1 = 0; l1 < 8; ++l1) {
            int i2 = k + this.random.nextInt(16) + 8;
            int j2 = this.random.nextInt(256);
            int k1 = l + this.random.nextInt(16) + 8;
            (new WorldGenDungeons()).generate(this.worldObj, this.random, i2, j2, k1);
        }

        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(k + 16, l + 16);
        biomegenbase.decorate(this.worldObj, this.random, k, l);
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go. If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk. Not saved during autosave, only during world unload. Currently
     * unimplemented.
     */
    public void saveExtraData() {}

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks() {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave() {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString() {
        return "FastFlatLevelSource";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List<net.minecraft.world.biome.BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType p_73155_1_,
        int p_73155_2_, int p_73155_3_, int p_73155_4_) {
        return BiomeGenBase.plains.getSpawnableList(p_73155_1_);
    }

    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_,
        int p_147416_5_) {
        return null;
    }

    public int getLoadedChunkCount() {
        return 0;
    }

    public void recreateStructures(int p_82695_1_, int p_82695_2_) {
        for (MapGenStructure mapgenstructure : this.structureGenerators) {
            mapgenstructure.func_151539_a(this, this.worldObj, p_82695_1_, p_82695_2_, null);
        }
    }
}
