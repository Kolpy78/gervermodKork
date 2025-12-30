package com.gamma.gervermod.latemixin.fixes;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.cloudflared.forge1710.command.CommandCloudflared;

@Mixin(CommandCloudflared.class)
public abstract class CloudflaredCommandListFixMixin {

    @Inject(method = "canCommandSenderUseCommand", at = @At("HEAD"), cancellable = true)
    public void injectHead(ICommandSender sender, CallbackInfoReturnable<Boolean> cir) {
        MinecraftServer server = MinecraftServer.getServer();
        // If the server is null, we can infer that we're on a client.
        // Clients already shouldn't be able to run this command, so
        // this works perfectly.
        if (server == null) cir.setReturnValue(false);
    }
}
