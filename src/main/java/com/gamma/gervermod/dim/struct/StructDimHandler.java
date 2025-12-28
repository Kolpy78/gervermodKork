package com.gamma.gervermod.dim.struct;

import java.io.File;
import java.util.List;
import java.util.Stack;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.gamma.gervermod.core.GerverMod;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;

public class StructDimHandler {

    public static int structDim;

    public static long nextClearMillis;
    private static int stage = 0;
    private static int ticksSinceUnload = 0;

    private static final Object2BooleanMap<EntityPlayer> playersQueued = new Object2BooleanOpenHashMap<>();

    public static void onTick() {
        long currentTimeMillis = System.currentTimeMillis();

        for (Object2BooleanMap.Entry<EntityPlayer> returnEntry : playersQueued.object2BooleanEntrySet()) {
            boolean satisfied = false;
            if (returnEntry.getBooleanValue() && joinsAllowed()) {
                MinecraftServer.getServer()
                    .getConfigurationManager()
                    .transferPlayerToDimension(
                        (EntityPlayerMP) returnEntry.getKey(),
                        structDim,
                        new StructDimTeleporter(
                            MinecraftServer.getServer()
                                .worldServerForDimension(structDim)));
                satisfied = true;
            } else if (!returnEntry.getBooleanValue()) {
                MinecraftServer.getServer()
                    .getConfigurationManager()
                    .transferPlayerToDimension(
                        (EntityPlayerMP) returnEntry.getKey(),
                        0,
                        new StructDimTeleporter(
                            MinecraftServer.getServer()
                                .worldServerForDimension(0)));
                satisfied = true;
            }
            if (satisfied) {
                playersQueued.removeBoolean(returnEntry.getKey());
            }
        }

        if (stage == 0) {
            if (currentTimeMillis <= nextClearMillis - 120000) return;
            sendMessageToAllPlayers(
                new ChatComponentText(
                    EnumChatFormatting.RED + "Structure dimension clearing in 2 minutes." + EnumChatFormatting.RESET));
            stage = 1;
        } else if (stage == 1) {
            if (currentTimeMillis <= nextClearMillis - 30000) return;
            sendMessageToAllPlayers(
                new ChatComponentText(
                    EnumChatFormatting.RED
                        + "Structure dimension clearing in 30 seconds. Entering dimension is now disabled."
                        + EnumChatFormatting.RESET));
            stage = 2;
        } else if (stage == 2) {
            if (currentTimeMillis <= nextClearMillis - 5000) return;
            sendMessageToAllPlayers(
                new ChatComponentText(
                    EnumChatFormatting.RED + "Structure dimension clearing in 5 seconds." + EnumChatFormatting.RESET));
            stage = 3;
        } else if (stage == 3) {
            if (currentTimeMillis < nextClearMillis) return;
            stage = 4;
        } else if (stage == 4) {
            sendMessageToAllPlayers(
                new ChatComponentText(
                    EnumChatFormatting.GREEN + "Structure dimension clearing now..." + EnumChatFormatting.RESET));

            WorldServer world = MinecraftServer.getServer()
                .worldServerForDimension(structDim);
            World overworld = MinecraftServer.getServer()
                .getEntityWorld();

            // noinspection SynchronizeOnNonFinalField
            List<EntityPlayer> players;
            synchronized (world.playerEntities) {
                players = new ObjectArrayList<>(world.playerEntities);
            }
            for (EntityPlayer entityPlayer : players) {
                MinecraftServer.getServer()
                    .getConfigurationManager()
                    .transferPlayerToDimension(
                        (EntityPlayerMP) entityPlayer,
                        0,
                        new StructDimTeleporter(
                            MinecraftServer.getServer()
                                .worldServerForDimension(0)));
            }

            world.flush();
            DimensionManager.unloadWorld(structDim);
            stage = 5;
        } else if (stage == 5) {
            if (DimensionManager.getWorld(structDim) != null) {
                ticksSinceUnload++;
                if (ticksSinceUnload >= 10) sendMessageToAllPlayers(
                    new ChatComponentText(
                        EnumChatFormatting.RED + "Structure dimension took too long to unload; unable to clear!"
                            + EnumChatFormatting.RESET));
                return; // Wait until it's unloaded...
            }
            // World is unloaded and we have full access over the directories.
            // It's time to do the thing.
            File rootDirectory = DimensionManager.getCurrentSaveRootDirectory();
            File worldDirectory = new File(rootDirectory, "DIM" + structDim);

            GerverMod.LOG.info("Deleting world directory: {}{}DIM{}", rootDirectory, File.separatorChar, structDim);
            recursivelyDeleteDirectory(worldDirectory);
            GerverMod.LOG.info("Deleted world directory: {}{}DIM{}", rootDirectory, File.separatorChar, structDim);

            sendMessageToAllPlayers(
                new ChatComponentText(
                    EnumChatFormatting.GREEN + "Structure dimension cleared!" + EnumChatFormatting.RESET));

            nextClearMillis = currentTimeMillis + Integer.parseInt(
                MinecraftServer.getServer()
                    .getEntityWorld()
                    .getGameRules()
                    .getGameRuleStringValue("structureWorldResetTime"));
            stage = 0;
        }
    }

    public static boolean joinsAllowed() {
        return stage < 2;
    }

    public static boolean isQueued(EntityPlayer player) {
        return playersQueued.containsKey(player);
    }

    public static void cancelQueue(EntityPlayer player) {
        playersQueued.removeBoolean(player);
    }

    public static void queueForEnter(EntityPlayer player) {
        playersQueued.put(player, true);
    }

    public static void queueForLeave(EntityPlayer player) {
        playersQueued.put(player, false);
    }

    private static final Stack<File> toDelete = new Stack<>();
    private static final Stack<File> directoriesToSearch = new Stack<>();

    public static void recursivelyDeleteDirectory(File directory) {
        directoriesToSearch.push(directory);

        while (!directoriesToSearch.isEmpty()) {
            File file = directoriesToSearch.pop();
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory()) directoriesToSearch.push(f);
                        else toDelete.push(f);
                    }
                }
            }
            toDelete.push(file);
        }

        while (!toDelete.isEmpty()) {
            File file = toDelete.pop();
            file.delete();
        }
    }

    private static void sendMessageToAllPlayers(IChatComponent message) {
        if (MinecraftServer.getServer()
            .getConfigurationManager() == null) return;
        List<EntityPlayerMP> players = MinecraftServer.getServer()
            .getConfigurationManager().playerEntityList;
        synchronized (players) {
            for (EntityPlayerMP player : players) {
                player.addChatMessage(message);
            }
        }
    }
}
