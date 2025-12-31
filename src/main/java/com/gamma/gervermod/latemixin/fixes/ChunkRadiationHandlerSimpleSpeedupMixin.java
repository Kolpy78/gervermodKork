package com.gamma.gervermod.latemixin.fixes;

import java.util.Map;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.handler.radiation.ChunkRadiationHandlerSimple;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

@Mixin(ChunkRadiationHandlerSimple.class)
public abstract class ChunkRadiationHandlerSimpleSpeedupMixin {

    @Shadow(remap = false)
    private Map<World, ChunkRadiationHandlerSimple.SimpleRadiationPerWorld> perWorld;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void injected(CallbackInfo ci) {
        perWorld = new Object2ObjectLinkedOpenHashMap<>();
    }

    @Mixin(ChunkRadiationHandlerSimple.SimpleRadiationPerWorld.class)
    public static abstract class SimpleRadiationPerWorldMixin {

        @Shadow(remap = false)
        public Map<ChunkCoordIntPair, Float> radiation;

        @Inject(method = "<init>", at = @At("TAIL"), remap = false)
        private void injected(CallbackInfo ci) {
            radiation = new Object2ObjectLinkedOpenHashMap<>();
        }
    }
}
