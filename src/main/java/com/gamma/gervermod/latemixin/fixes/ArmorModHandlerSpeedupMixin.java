package com.gamma.gervermod.latemixin.fixes;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.hbm.handler.ArmorModHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

@Mixin(ArmorModHandler.class)
public abstract class ArmorModHandlerSpeedupMixin {

    @Unique
    private static final Object2ObjectMap<NBTTagCompound, ItemStack[]> gervermod$cache = new Object2ObjectOpenHashMap<>();

    @Unique
    private static final int EMPTY_ARMOR = 0x3124d489;

    @WrapMethod(method = "pryMods", remap = false)
    private static ItemStack[] wrapped(ItemStack armor, Operation<ItemStack[]> original) {
        if (armor == null) {
            gervermod$cache.clear();
            return new ItemStack[9];
        }

        if (!ArmorModHandler.hasMods(armor)) return new ItemStack[9];

        NBTTagCompound tag = armor.getTagCompound();

        if (tag.hashCode() == EMPTY_ARMOR) return new ItemStack[9];

        ItemStack[] stacks;
        if ((stacks = gervermod$cache.get(tag)) != null) return stacks;

        stacks = original.call(armor);
        gervermod$cache.put(tag, stacks);
        return stacks;
    }
}
