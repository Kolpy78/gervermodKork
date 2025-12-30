package com.gamma.gervermod.command;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import com.gamma.gervermod.dim.struct.StructDimHandler;

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
        if (args.length > 2) throw new WrongUsageException("/structworld <enter|leave|cancel>");

        if (args[0].equalsIgnoreCase("enter")) {
            if (args.length < 2){
                throw new WrongUsageException("/structworld enter <plains|desert>");
            }
            if (sender == MinecraftServer.getServer()) {
                sender.addChatMessage(new ChatComponentText("Cannot run this command from console."));
                return;
            }

            EntityPlayer player = (EntityPlayer) sender;

            if (player.dimension == StructDimHandler.structDim) {
                sender.addChatMessage(new ChatComponentText("Already in structure dimension!"));
                return;
            }

            if (player.dimension != 0) {
                sender.addChatMessage(new ChatComponentText("Must be in overworld to enter structure dimension."));
                return;
            }

            if (StructDimHandler.isQueued(player)) {
                sender.addChatMessage(new ChatComponentText("Already queued to enter structure dimension!"));
                return;
            }

            sender.addChatMessage(new ChatComponentText("Queuing for structure dimension..."));

            if (args[1].equalsIgnoreCase("plains")){
                StructDimHandler.queueForEnter(player, 400);
            }
            if (args[1].equalsIgnoreCase("desert")){
                StructDimHandler.queueForEnter(player, 401);
            }


        } else if (args[0].equalsIgnoreCase("leave")) {
            if (sender == MinecraftServer.getServer()) {
                sender.addChatMessage(new ChatComponentText("Cannot run this command from console."));
                return;
            }

            EntityPlayer player = (EntityPlayer) sender;

            if (player.dimension != StructDimHandler.structDim && player.dimension != StructDimHandler.desertStructDim) {
                sender.addChatMessage(new ChatComponentText("Not in structure dimension!"));
                return;
            }

            if (StructDimHandler.isQueued(player)) {
                sender.addChatMessage(new ChatComponentText("Already queued to leave structure dimension!"));
                return;
            }

            sender.addChatMessage(new ChatComponentText("Queuing to leave structure dimension..."));

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
                    "The structure dimension will clear in " + hours
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
        boolean hasEnter = Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase(keyword));
        if (args.length == 2 && hasEnter) {
            return getListOfStringsMatchingLastWord(args, "plains", "desert");
        }
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "enter", "leave", "cancel") : null;
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
