package com.gamma.gervermod.latemixin.fixes;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.uninos.NodeNet;

@Mixin(NodeNet.class)
public abstract class NodeNetSpeedupMixin {

    @Shadow(remap = false)
    public HashMap receiverEntries;

    @Shadow(remap = false)
    public HashMap providerEntries;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void injected(CallbackInfo ci) {
        receiverEntries = new LinkedHashMap<>();
        providerEntries = new LinkedHashMap<>();
    }
}
