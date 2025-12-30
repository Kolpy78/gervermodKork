package com.gamma.gervermod.dim.struct;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.Stack;

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
import com.gamma.gervermod.dim.struct.providers.AbstractStructWorldProvider;
import com.gamma.gervermod.dim.struct.providers.StructWorldDesertProvider;
import com.gamma.gervermod.dim.struct.providers.StructWorldPlainsProvider;

import cpw.mods.fml.common.FMLLog;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class StructDimHandler {

    public static final int structDim = 400;
    public static final int desertStructDim = 401;
    public static final Int2ObjectMap<AbstractStructWorldProvider> allDims = new Int2ObjectLinkedOpenHashMap<>();

    static {
        allDims.put(structDim, new StructWorldPlainsProvider());
        allDims.put(desertStructDim, new StructWorldDesertProvider());
    }

    public static long nextClearMillis;
    private static int stage = 0;
    private static boolean override = false;

    private static final Object2IntMap<EntityPlayer> playersQueued = new Object2IntLinkedOpenHashMap<>();

    public static void onTick() {
        long currentTimeMillis = System.currentTimeMillis();
        for (Object2IntMap.Entry<EntityPlayer> moveEntry : playersQueued.object2IntEntrySet()) {

            int value = moveEntry.getIntValue();
            EntityPlayer player = moveEntry.getKey();

            if (value != 0 && joinsAllowed()) {
                MinecraftServer.getServer()
                    .getConfigurationManager()
                    .transferPlayerToDimension(
                        (EntityPlayerMP) player,
                        value,
                        new StructDimTeleporter(
                            MinecraftServer.getServer()
                                .worldServerForDimension(value)));
                playersQueued.removeInt(player);
            } else if (value == 0) {
                MinecraftServer.getServer()
                    .getConfigurationManager()
                    .transferPlayerToDimension(
                        (EntityPlayerMP) player,
                        0,
                        new StructDimTeleporter(
                            MinecraftServer.getServer()
                                .worldServerForDimension(0)));
                playersQueued.removeInt(player);
            }
        }

        if (stage == 0) {
            if (currentTimeMillis <= nextClearMillis - 120000) return;
            sendMessageToAllPlayers(
                new ChatComponentText(
                    EnumChatFormatting.RED + "All structure dimensions clearing in 2 minutes."
                        + EnumChatFormatting.RESET));
            stage = 1;
        } else if (stage == 1) {
            if (currentTimeMillis <= nextClearMillis - 30000) return;
            sendMessageToAllPlayers(
                new ChatComponentText(
                    EnumChatFormatting.RED
                        + "All structure dimensions clearing in 30 seconds. Entering dimensions is now disabled."
                        + EnumChatFormatting.RESET));
            stage = 2;
        } else if (stage == 2) {
            if (currentTimeMillis <= nextClearMillis - 5000) return;
            sendMessageToAllPlayers(
                new ChatComponentText(
                    EnumChatFormatting.RED + "All structure dimensions clearing in 5 seconds."
                        + EnumChatFormatting.RESET));
            stage = 3;
        } else if (stage == 3) {
            if (currentTimeMillis < nextClearMillis) return;
            stage = 4;
        } else if (stage == 4) {
            sendMessageToAllPlayers(
                new ChatComponentText(
                    EnumChatFormatting.GREEN + "All structure dimensions clearing now..." + EnumChatFormatting.RESET));

            for (int dimID : allDims.keySet()) {
                WorldServer world = MinecraftServer.getServer()
                    .worldServerForDimension(dimID);
                kick(dimID);

                // Now we have everyone out of the world, still loaded.
                world.flush();
                DimensionManager.unloadWorld(dimID);
                stage = 5;
            }
        } else if (stage == 5) {
            for (Int2ObjectMap.Entry<AbstractStructWorldProvider> dimEntry : allDims.int2ObjectEntrySet()) {

                int dimID = dimEntry.getIntKey();
                AbstractStructWorldProvider provider = dimEntry.getValue();

                if (DimensionManager.getWorld(dimID) != null) {
                    return; // Wait until it's unloaded...
                }

                // absolutely make sure it's unloaded.
                DimensionManager.setWorld(dimID, null);

                // World is unloaded and we have full access over the directories.
                // It's time to do the thing.
                File rootDirectory = DimensionManager.getCurrentSaveRootDirectory();
                File worldDirectory = new File(rootDirectory, "DIM" + dimID);

                GerverMod.LOG.info("Deleting world directory: {}{}DIM{}", rootDirectory, File.separator, dimID);
                recursivelyDeleteDirectory(worldDirectory);
                GerverMod.LOG.info("Deleted world directory: {}{}DIM{}", rootDirectory, File.separator, dimID);

                // Re-init dimension.
                DimensionManager.initDimension(dimID);
                WorldServer world = DimensionManager.getWorld(dimID);
                world.rand = new Random();
                provider.nextSeed();
            }

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
        return stage < 2 && !override;
    }

    public static boolean isQueued(EntityPlayer player) {
        return playersQueued.containsKey(player);
    }

    public static void cancelQueue(EntityPlayer player) {
        playersQueued.removeInt(player);
    }

    public static void queueForEnter(EntityPlayer player, int targetDim) {
        playersQueued.put(player, targetDim);
    }

    public static void queueForLeave(EntityPlayer player) {
        playersQueued.put(player, 0);
    }

    public static boolean isPlayerInDim(EntityPlayer player) {
        for (int dimID : allDims.keySet()) {
            if (player.dimension == dimID) return true;
        }
        return false;
    }

    public static void kick(int dimID) {
        World world = MinecraftServer.getServer()
            .worldServerForDimension(dimID);
        List<EntityPlayer> players;
        if (world.playerEntities != null) {
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
        } else {
            FMLLog.severe("Player list was null! How did this happen?!");
        }
    }

    public static void disallow() {
        override = true;
    }

    public static void allow() {
        override = false;
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
