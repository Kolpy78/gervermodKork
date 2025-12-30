package com.gamma.gervermod.command;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import com.gamma.gervermod.dim.struct.StructDimHandler;
import com.gamma.gervermod.dim.struct.providers.AbstractStructWorldProvider;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public class StructWorldCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "structworld";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/structworld <enter|leave|cancel|timer>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) throw new WrongUsageException("/structworld <enter|leave|cancel|timer>");

        if (args[0].equalsIgnoreCase("enter")) {
            if (args.length != 2) {
                throw new WrongUsageException("/structworld enter <dimension-name>");
            }
            if (sender == MinecraftServer.getServer()) {
                sender.addChatMessage(new ChatComponentText("Cannot run this command from console."));
                return;
            }

            EntityPlayer player = (EntityPlayer) sender;

            if (StructDimHandler.isPlayerInDim(player)) {
                sender.addChatMessage(new ChatComponentText("Already in a structure dimension!"));
                return;
            }

            if (player.dimension != 0) {
                sender.addChatMessage(new ChatComponentText("Must be in overworld to enter a structure dimension."));
                return;
            }

            if (StructDimHandler.isQueued(player)) {
                sender.addChatMessage(new ChatComponentText("Already queued to enter a structure dimension!"));
                return;
            }

            boolean satisfied = false;
            for (Int2ObjectMap.Entry<AbstractStructWorldProvider> provider : StructDimHandler.allDims
                .int2ObjectEntrySet()) {
                if (args[1].equalsIgnoreCase(
                    provider.getValue()
                        .getWorldType())) {
                    sender.addChatMessage(new ChatComponentText("Queuing for the structure dimension..."));
                    StructDimHandler.queueForEnter(player, provider.getIntKey());
                    satisfied = true;
                    break;
                }
            }

            if (!satisfied) {
                throw new DimensionNotFoundException();
            }

        } else if (args[0].equalsIgnoreCase("leave")) {
            if (sender == MinecraftServer.getServer()) {
                sender.addChatMessage(new ChatComponentText("Cannot run this command from console."));
                return;
            }

            EntityPlayer player = (EntityPlayer) sender;

            if (!StructDimHandler.isPlayerInDim(player)) {
                sender.addChatMessage(new ChatComponentText("Not in a structure dimension!"));
                return;
            }

            if (StructDimHandler.isQueued(player)) {
                sender.addChatMessage(new ChatComponentText("Already queued to leave a structure dimension!"));
                return;
            }

            sender.addChatMessage(new ChatComponentText("Queuing to leave the structure dimension..."));

            StructDimHandler.queueForLeave(player);
        } else if (args[0].equalsIgnoreCase("cancel")) {
            if (sender == MinecraftServer.getServer()) {
                sender.addChatMessage(new ChatComponentText("Cannot run this command from console."));
                return;
            }

            EntityPlayer player = (EntityPlayer) sender;

            if (!StructDimHandler.isQueued(player)) {
                sender.addChatMessage(new ChatComponentText("Not in structure dimension queue!"));
                return;
            }

            sender.addChatMessage(new ChatComponentText("Leaving queue..."));

            StructDimHandler.cancelQueue(player);
        } else if (args[0].equalsIgnoreCase("timer")) {

            Instant instant = Instant.ofEpochMilli(StructDimHandler.nextClearMillis);
            Instant now = Instant.now();

            long seconds = now.until(instant, ChronoUnit.SECONDS);
            long minutes = seconds / 60;
            long hours = minutes / 60;
            minutes = minutes % 60;
            seconds = seconds % 60;

            sender.addChatMessage(
                new ChatComponentText(
                    "The structure dimensions will clear in " + hours
                        + " hours, "
                        + minutes
                        + " minutes, and "
                        + seconds
                        + " seconds."));
        } else throw new WrongUsageException("/structworld <enter|leave|cancel|timer>");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        String keyword = "enter";
        boolean hasEnter = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase(keyword)) {
                hasEnter = true;
                break;
            }
        }
        if (args.length == 2 && hasEnter) {
            String[] arr = new String[StructDimHandler.allDims.size()];
            int idx = 0;
            for (int dimID : StructDimHandler.allDims.keySet()) {
                arr[idx++] = StructDimHandler.allDims.get(dimID)
                    .getWorldType()
                    .toLowerCase();
            }

            return getListOfStringsMatchingLastWord(args, arr);
        }
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "enter", "leave", "cancel", "timer") : null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
