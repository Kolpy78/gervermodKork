package com.gamma.gervermod.mixin.structworld;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gamma.gervermod.dim.struct.StructDimHandler;
import com.hbm.world.feature.DepthDeposit;

@Mixin(DepthDeposit.class)
public abstract class DepthDepositMixin {

    @Inject(method = "generate", at = @At("HEAD"), cancellable = true, remap = false)
    private static void generateOre(World world, int x, int y, int z, int size, double fill, Block block, Random rand,
        Block genTarget, Block filler, CallbackInfo ci) {
        if (world.provider.dimensionId == StructDimHandler.structDim) ci.cancel();
    }
}
