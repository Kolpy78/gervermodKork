package com.gamma.gervermod.command;

import net.minecraft.command.CommandException;

public class DimensionNotFoundException extends CommandException {

    public DimensionNotFoundException() {
        this("Invalid dimension name.");
    }

    public DimensionNotFoundException(String p_i1362_1_, Object... p_i1362_2_) {
        super(p_i1362_1_, p_i1362_2_);
    }
}
