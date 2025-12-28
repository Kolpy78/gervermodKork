package com.gamma.gervermod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import com.gamma.gervermod.dim.struct.StructDimHandler;

import java.util.List;

public class StructWorldCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "structworld";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/structworld <enter|leave|cancel>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) throw new WrongUsageException("/structworld <enter|leave|cancel>");

        if (args[0].equalsIgnoreCase("enter")) {
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

            StructDimHandler.queueForEnter(player);

        } else if (args[0].equalsIgnoreCase("leave")) {
            if (sender == MinecraftServer.getServer()) {
                sender.addChatMessage(new ChatComponentText("Cannot run this command from console."));
                return;
            }

            EntityPlayer player = (EntityPlayer) sender;

            if (player.dimension != StructDimHandler.structDim) {
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
        } else throw new WrongUsageException("/structworld <enter|leave|cancel>");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "enter", "leave", "cancel"): null;
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
