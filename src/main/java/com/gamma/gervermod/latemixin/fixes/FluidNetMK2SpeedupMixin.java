package com.gamma.gervermod.latemixin.fixes;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.util.Tuple;

import api.hbm.energymk2.IEnergyReceiverMK2;
import api.hbm.fluidmk2.FluidNetMK2;
import api.hbm.fluidmk2.IFluidProviderMK2;
import api.hbm.fluidmk2.IFluidReceiverMK2;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

@Mixin(FluidNetMK2.class)
public abstract class FluidNetMK2SpeedupMixin {

    @Shadow(remap = false)
    public List<Tuple.Pair<IFluidProviderMK2, Long>>[] providers;

    @Shadow(remap = false)
    public List<Tuple.Pair<IFluidReceiverMK2, Long>>[][] receivers;

    @Inject(method = "<init>", at = @At(value = "TAIL"), remap = false)
    private void injected(FluidType type, CallbackInfo ci) {
        for (int i = 0; i < 6; ++i) {
            providers[i] = new ObjectArrayList<>();
        }

        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < IEnergyReceiverMK2.ConnectionPriority.values().length; ++j) {
                receivers[i][j] = new ObjectArrayList<>();
            }
        }
    }
}
