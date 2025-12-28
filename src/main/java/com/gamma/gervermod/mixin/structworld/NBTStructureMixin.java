package com.gamma.gervermod.mixin.structworld;

import org.spongepowered.asm.mixin.Mixin;

import com.gamma.gervermod.dim.struct.StructDimHandler;
import com.hbm.world.gen.nbt.NBTStructure;
import com.hbm.world.gen.nbt.SpawnCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import it.unimi.dsi.fastutil.ints.IntList;

@Mixin(NBTStructure.class)
public abstract class NBTStructureMixin {

    @WrapMethod(method = "registerStructure(ILcom/hbm/world/gen/nbt/SpawnCondition;)V", remap = false)
    private static void wrapped(int dimID, SpawnCondition spawnCondition, Operation<Void> original) {
        if (dimID == 0) original.call(StructDimHandler.structDim, spawnCondition);
        original.call(dimID, spawnCondition);
    }

    @WrapMethod(method = "registerStructure(Lcom/hbm/world/gen/nbt/SpawnCondition;[I)V", remap = false)
    private static void wrapped(SpawnCondition spawnCondition, int[] dimensionIds, Operation<Void> original) {
        IntList dims = IntList.of(dimensionIds);
        if (dims.contains(0) && !dims.contains(StructDimHandler.structDim)) {
            dims.add(StructDimHandler.structDim);
            original.call(spawnCondition, dims.toArray(new int[0]));
        }
        original.call(spawnCondition, dimensionIds);
    }
}
