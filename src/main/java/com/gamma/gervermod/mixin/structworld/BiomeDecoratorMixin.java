package com.gamma.gervermod.mixin.structworld;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gamma.gervermod.dim.struct.StructDimHandler;

@Mixin(BiomeDecorator.class)
public abstract class BiomeDecoratorMixin {

    @Shadow
    public World currentWorld;

    @Inject(method = "generateOres", at = @At("HEAD"), cancellable = true)
    private void generateOres(CallbackInfo ci) {
        if (currentWorld.provider.dimensionId == StructDimHandler.structDim) ci.cancel();
    }
}
