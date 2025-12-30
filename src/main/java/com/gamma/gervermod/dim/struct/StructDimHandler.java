package com.gamma.gervermod.dim.struct;

import java.io.File;
import java.util.*;

import cpw.mods.fml.common.FMLLog;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import scala.Int;

public class StructDimHandler {

    public static final int structDim = 400;
    public static final int desertStructDim = 401;
    public static final int[] allDims = {structDim, desertStructDim};

    public static long nextClearMillis;
    private static int stage = 0;
    private static boolean override = false;

    private static final HashMap<EntityPlayer, Integer> playersQueued = new HashMap<>();

    public static void onTick() {
        long currentTimeMillis = System.currentTimeMillis();
//Object2BooleanMap.Entry<EntityPlayer> returnEntry : playersQueued.entrySet()
        for (Map.Entry<EntityPlayer, Integer> returnEntry : playersQueued.entrySet()) {
            boolean satisfied = false;
            //note to future: 400 is for regular structureworld, 401 is for the desert "flavor" remember to use parenthesis so the && evaluates the two of them!
            if (returnEntry.getValue() == 400 && joinsAllowed()){
                MinecraftServer.getServer()
                    .getConfigurationManager()
                    .transferPlayerToDimension(
                        (EntityPlayerMP) returnEntry.getKey(),
                        structDim,
                        new StructDimTeleporter(
                            MinecraftServer.getServer()
                                .worldServerForDimension(structDim)));
                satisfied = true;
            } else if (returnEntry.getValue() == 401 && joinsAllowed()){
                MinecraftServer.getServer()
                    .getConfigurationManager()
                    .transferPlayerToDimension(
                        (EntityPlayerMP) returnEntry.getKey(),
                        desertStructDim,
                        new StructDimTeleporter(
                            MinecraftServer.getServer()
                                .worldServerForDimension(desertStructDim)));
                satisfied = true;
            } else if (returnEntry.getValue() != 400 && returnEntry.getValue() != 401){
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
            if (satisfied){
                playersQueued.remove(returnEntry.getKey());
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

            for (int dimID : allDims){
                WorldServer world = MinecraftServer.getServer()
                    .worldServerForDimension(structDim);
                WorldServer world2 = MinecraftServer.getServer().worldServerForDimension(desertStructDim);
                kick(dimID);

                // Now we have everyone out of the world, still loaded.
                world.flush();
                world2.flush();
                DimensionManager.unloadWorld(structDim);
                DimensionManager.unloadWorld(desertStructDim);
                stage = 5;
            }
        } else if (stage == 5) {
            for (int dimID : allDims){
                if (DimensionManager.getWorld(dimID) != null) {
                    return; // Wait until it's unloaded...
                }
            }

            for (int dimID : allDims){
                DimensionManager.setWorld(dimID, null);// absolutely make sure it's unloaded.
            }

            // World is unloaded and we have full access over the directories.
            // It's time to do the thing.
           for (int dimID : allDims){
               File rootDirectory = DimensionManager.getCurrentSaveRootDirectory();
               File worldDirectory = new File(rootDirectory, "DIM" + dimID);

               GerverMod.LOG.info("Deleting world directory: {}{}DIM{}", rootDirectory, File.separatorChar, dimID);
               recursivelyDeleteDirectory(worldDirectory);
               GerverMod.LOG.info("Deleted world directory: {}{}DIM{}", rootDirectory, File.separatorChar, dimID);

               // Re-init dimension.
               DimensionManager.initDimension(dimID);
               WorldServer world = DimensionManager.getWorld(dimID);
               world.rand = new Random();
               if (world.provider instanceof StructWorldProvider){
                   ((StructWorldProvider) world.provider).nextSeed();
               }
               else if (world.provider instanceof StructWorldDesert){
                   ((StructWorldDesert) world.provider).nextSeed();
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
    }

    public static boolean joinsAllowed() {
        return stage < 2 && !override;
    }

    public static boolean isQueued(EntityPlayer player) {
        return playersQueued.containsKey(player);
    }

    public static void cancelQueue(EntityPlayer player) {
        playersQueued.remove(player);
    }

    public static void queueForEnter(EntityPlayer player, Integer targetDim) {
        playersQueued.put(player, targetDim);
    }

    public static void queueForLeave(EntityPlayer player) {
        playersQueued.put(player, 0);
    }

    public static void kick(int dimID) {
        World world = MinecraftServer.getServer()
            .worldServerForDimension(dimID);
        List<EntityPlayer> players;
        if (world.playerEntities != null) {
            synchronized (world.playerEntities) {
                players = new ObjectArrayList<>(world.playerEntities);
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
            }
        } else {
            FMLLog.severe("Player list was null! how did this happen?!");
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
