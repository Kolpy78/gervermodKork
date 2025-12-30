package com.gamma.gervermod.latemixin.fixes;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.uninos.NodeNet;
import com.hbm.uninos.UniNodespace;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

@Mixin(UniNodespace.class)
public abstract class UniNodespaceSpeedupMixin {

    @Shadow(remap = false)
    public static Set<NodeNet> activeNodeNets;

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void injected(CallbackInfo ci) {
        activeNodeNets = new ObjectLinkedOpenHashSet<>();
    }
}
