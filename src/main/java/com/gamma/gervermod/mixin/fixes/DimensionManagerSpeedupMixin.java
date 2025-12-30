package com.gamma.gervermod.mixin.fixes;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

@Mixin(DimensionManager.class)
public abstract class DimensionManagerSpeedupMixin {

    @Unique
    private static WorldServer[] gervermod$worldCache;
    @Unique
    private static Integer[] gervermod$idCache;

    @WrapMethod(method = "getWorlds", remap = false)
    private static WorldServer[] getWorlds(Operation<WorldServer[]> original) {
        if (gervermod$worldCache == null) gervermod$worldCache = original.call();
        return gervermod$worldCache;
    }

    @WrapMethod(method = "getIDs()[Ljava/lang/Integer;", remap = false)
    private static Integer[] getIDs(Operation<Integer[]> original) {
        if (gervermod$idCache == null) gervermod$idCache = original.call();
        return gervermod$idCache;
    }

    @Inject(method = "setWorld", at = @At("HEAD"), remap = false)
    private static void injected(CallbackInfo ci) {
        gervermod$worldCache = null;
        gervermod$idCache = null;
    }
}
