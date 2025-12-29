package com.gamma.gervermod.latemixin.fixes;

import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import codechicken.nei.api.ItemInfo;
import codechicken.nei.recipe.StackInfo;

@Mixin(StackInfo.class)
public abstract class NEINPEFixMixin {

    /**
     * @author BallOfEnergy
     * @reason Minor fix for NPE error.
     */
    @Overwrite(remap = false)
    public static ItemStack getItemStackWithMinimumDamage(ItemStack[] stacks) {
        int damage = Short.MAX_VALUE;
        ItemStack result = stacks[0];

        if (stacks.length > 1) {
            for (ItemStack stack : stacks) {
                if (stack != null && stack.getItem() != null
                    && !ItemInfo.isHidden(stack)
                    && stack.getItemDamage() < damage) {
                    damage = stack.getItemDamage();
                    result = stack;
                }
            }
        }

        return result.copy();
    }
}
