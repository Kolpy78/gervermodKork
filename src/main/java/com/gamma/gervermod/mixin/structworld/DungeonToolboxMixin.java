package com.gamma.gervermod.mixin.structworld;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gamma.gervermod.dim.struct.StructDimHandler;
import com.hbm.world.generator.DungeonToolbox;

@Mixin(DungeonToolbox.class)
public abstract class DungeonToolboxMixin {

    @Inject(
        method = "generateOre(Lnet/minecraft/world/World;Ljava/util/Random;IIIIIILnet/minecraft/block/Block;ILnet/minecraft/block/Block;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false)
    private static void generateOre(World world, Random rand, int chunkX, int chunkZ, int veinCount, int amount,
        int minHeight, int variance, Block ore, int meta, Block target, CallbackInfo ci) {
        if (world.provider.dimensionId == StructDimHandler.structDim) ci.cancel();
    }
}
