package com.gamma.gervermod.mixin.fixes;

import java.util.Collection;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

// This mixin occurs only on the server-side, fully because the fillChunk method
// is taken up (overwritten) by chunkapi.
@Mixin(Chunk.class)
public abstract class ChunkSpeedupMixin {

    @Shadow
    @Final
    public int xPosition;
    @Shadow
    @Final
    public int zPosition;

    @Shadow
    public abstract Block getBlock(int p_150810_1_, int p_150810_2_, int p_150810_3_);

    @Shadow
    public abstract int getBlockMetadata(int p_76628_1_, int p_76628_2_, int p_76628_3_);

    @Shadow
    public World worldObj;
    @Shadow
    public boolean isChunkLoaded;

    @Unique
    public Short2ObjectMap<TileEntity> gervermod$tileEntityMap = new Short2ObjectOpenHashMap<>();

    @Unique
    private short gervermod$packCoords(int x, int y, int z) {
        return (short) (((x & 0xF) << 12) | ((y & 0xFF) << 4) | (z & 0xF));
    }

    /**
     * @author BallOfEnergy01
     * @reason Performance fixes.
     */
    @Overwrite
    public TileEntity func_150806_e(int x, int y, int z) {
        short packed = gervermod$packCoords(x, y, z);
        TileEntity tileentity = this.gervermod$tileEntityMap.get(packed);

        if (tileentity != null && tileentity.isInvalid()) {
            gervermod$tileEntityMap.remove(packed);
            tileentity = null;
        }

        if (tileentity == null) {
            Block block = this.getBlock(x, y, z);
            int meta = this.getBlockMetadata(x, y, z);

            if (!block.hasTileEntity(meta)) {
                return null;
            }

            tileentity = block.createTileEntity(worldObj, meta);
            this.worldObj.setTileEntity(this.xPosition * 16 + x, y, this.zPosition * 16 + z, tileentity);
        }

        return tileentity;
    }

    /**
     * @author BallOfEnergy01
     * @reason Performance fixes.
     */
    @Overwrite
    public void func_150812_a(int x, int y, int z, TileEntity newTileEntity) {
        newTileEntity.setWorldObj(this.worldObj);
        newTileEntity.xCoord = this.xPosition * 16 + x;
        newTileEntity.yCoord = y;
        newTileEntity.zCoord = this.zPosition * 16 + z;

        int metadata = getBlockMetadata(x, y, z);
        if (this.getBlock(x, y, z)
            .hasTileEntity(metadata)) {
            short packed = gervermod$packCoords(x, y, z);
            TileEntity oldTE = this.gervermod$tileEntityMap.put(packed, newTileEntity);
            oldTE.invalidate();
            newTileEntity.validate();
        }
    }

    /**
     * @author BallOfEnergy01
     * @reason Performance fixes.
     */
    @Overwrite
    public void removeTileEntity(int x, int y, int z) {
        short packed = gervermod$packCoords(x, y, z);

        if (this.isChunkLoaded) {
            TileEntity tileentity = this.gervermod$tileEntityMap.remove(packed);

            if (tileentity != null) {
                tileentity.invalidate();
            }
        }
    }

    @Redirect(
        method = { "onChunkLoad", "onChunkUnload" },
        at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<?> redirect(Map instance) {
        return this.gervermod$tileEntityMap.values();
    }
}
