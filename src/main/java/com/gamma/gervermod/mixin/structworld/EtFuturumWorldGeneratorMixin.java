package com.gamma.gervermod.mixin.structworld;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gamma.gervermod.dim.struct.StructDimHandler;

import ganymedes01.etfuturum.world.EtFuturumWorldGenerator;

@Mixin(EtFuturumWorldGenerator.class)
public abstract class EtFuturumWorldGeneratorMixin {

    @Inject(method = "generate", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void injected(Random rand, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider, CallbackInfo ci) {
        if (world.provider.dimensionId == StructDimHandler.structDim) ci.cancel();
    }
}
