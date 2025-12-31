package com.gamma.gervermod.core;

import com.gamma.gervermod.accessors.RandomTickSpeedAccessor;
import com.hbm.handler.ArmorModHandler;

import cpw.mods.fml.common.Loader;
import ganymedes01.etfuturum.gamerule.RandomTickSpeed;

public class FixesCore {

    public static void onPreTick() {
        if (Loader.isModLoaded("etfuturum"))
            ((RandomTickSpeedAccessor) RandomTickSpeed.INSTANCE).gervermod$resetCache();
        // Clear every second to avoid getting too full (while maintaining performance).
        if (Loader.isModLoaded("hbm")) {
            ArmorModHandler.pryMods(null);
        }
    }
}
