package com.gamma.gervermod.dim.struct;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class StructDimTeleporter extends Teleporter {

    private final WorldServer world;

    public StructDimTeleporter(final WorldServer world) {
        super(world);
        this.world = world;
    }

    @Override
    public void placeInPortal(final Entity par1Entity, double par2, double par4, double par6, float par8) {
        WorldServer world = this.world;
        Chunk chunk = world.getChunkFromBlockCoords(0, 0); // gen chunk
        par1Entity.setLocationAndAngles(0, chunk.getHeightValue(0, 0), 0, par1Entity.rotationYaw, 0.0F);
        par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0D;
    }

    @Override
    public boolean placeInExistingPortal(Entity par1Entity, double par2, double par4, double par6, float par8) {
        return false;
    }

    @Override
    public boolean makePortal(Entity par1Entity) {
        return false;
    }

    @Override
    public void removeStalePortalLocations(long par1) {

    }
}
