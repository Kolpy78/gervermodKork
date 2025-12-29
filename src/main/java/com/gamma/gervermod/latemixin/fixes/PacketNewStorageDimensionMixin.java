package com.gamma.gervermod.latemixin.fixes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketNewStorageDimension;

@Mixin(PacketNewStorageDimension.class)
public abstract class PacketNewStorageDimensionMixin {

    @Shadow(remap = false)
    @Final
    private int newDim;

    @Inject(method = "clientPacketData", at = @At("HEAD"), cancellable = true, remap = false)
    private void injected(INetworkInfo network, AppEngPacket packet, EntityPlayer player, CallbackInfo ci) {
        if (DimensionManager.isDimensionRegistered(newDim)) ci.cancel();
    }
}
