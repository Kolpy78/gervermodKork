package com.gamma.gervermod.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import com.gamma.gervermod.dim.struct.StructDimHandler;

public class AdminStructWorldCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "adminstructworld";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/adminstructworld <kick|disallow|allow|timer>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("kick")) {
            for (int dimID : StructDimHandler.allDims){
                StructDimHandler.kick(dimID);
            }
        } else if (args[0].equalsIgnoreCase("disallow")) {
            StructDimHandler.disallow();
        } else if (args[0].equalsIgnoreCase("allow")) {
            StructDimHandler.allow();
        } else if (args[0].equalsIgnoreCase("timer")) {
            StructDimHandler.nextClearMillis = System.currentTimeMillis() + Integer.parseInt(args[1]);
        } else throw new WrongUsageException("/adminstructworld <kick|disallow|timer>");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "kick", "disallow", "allow", "timer") : null;
    }
}
