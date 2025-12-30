package com.gamma.gervermod.latemixin.structworld;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gamma.gervermod.dim.struct.StructDimHandler;
import com.hbm.inventory.FluidStack;
import com.hbm.world.feature.BedrockOre;

@Mixin(value = BedrockOre.class, remap = false)
public abstract class BedrockOreMixin {

    @Inject(
        method = "generate(Lnet/minecraft/world/World;IILnet/minecraft/item/ItemStack;Lcom/hbm/inventory/FluidStack;IILnet/minecraft/block/Block;Lnet/minecraft/block/Block;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false)
    private static void generate(World world, int x, int z, ItemStack stack, FluidStack acid, int color, int tier,
        Block depthRock, Block targetBlock, CallbackInfo ci) {
        if (world.provider.dimensionId == StructDimHandler.structDim) ci.cancel();
    }
}
