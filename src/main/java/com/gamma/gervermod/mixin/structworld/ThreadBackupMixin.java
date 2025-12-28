package com.gamma.gervermod.mixin.structworld;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gamma.gervermod.dim.struct.StructDimHandler;
import com.llamalad7.mixinextras.sugar.Local;

import serverutils.lib.math.ChunkDimPos;
import serverutils.lib.util.compression.ICompress;
import serverutils.task.backup.ThreadBackup;

@Mixin(ThreadBackup.class)
public abstract class ThreadBackupMixin {

    @Inject(
        method = "doBackup",
        at = @At(
            value = "INVOKE",
            target = "Lserverutils/task/backup/ThreadBackup;addBaseFolderFiles(Ljava/util/List;Ljava/io/File;)V",
            shift = At.Shift.AFTER,
            remap = false),
        remap = false)
    private static void injected(ICompress compressor, File src, String customName, Set<ChunkDimPos> chunks,
        CallbackInfo ci, @Local(name = "files") List<File> files) {
        files.removeIf(
            f -> f.getAbsolutePath()
                .contains("DIM" + StructDimHandler.structDim));
    }
}
