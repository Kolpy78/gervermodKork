package com.gamma.gervermod.latemixin.fixes;

import static com.gamma.gervermod.core.GerverMod.eidLoaded;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.falsepattern.endlessids.mixin.helpers.ChunkBiomeHook;

import appeng.core.AEConfig;
import appeng.spatial.StorageChunkProvider;

@Mixin(StorageChunkProvider.class)
public abstract class AE2SpatialFixMixin {

    @Shadow(remap = false)
    @Final
    private World world;
    @Shadow(remap = false)
    @Final
    private static Block[] BLOCKS;

    /**
     * @author BallOfEnergy01
     * @reason Fix compat for EndlessIDs.
     */
    @Overwrite
    public Chunk provideChunk(int x, int z) {
        final Chunk chunk = new Chunk(this.world, BLOCKS, x, z);

        final AEConfig config = AEConfig.instance;

        if (eidLoaded) {
            final short[] biomes = ((ChunkBiomeHook) chunk).getBiomeShortArray();
            Arrays.fill(biomes, (short) config.storageBiomeID);
        } else {
            final byte[] biomes = chunk.getBiomeArray();
            Arrays.fill(biomes, (byte) config.storageBiomeID);
        }

        if (!chunk.isTerrainPopulated) {
            chunk.isTerrainPopulated = true;
            chunk.resetRelightChecks();
        }

        return chunk;
    }
}
