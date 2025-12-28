package com.gamma.gervermod.core;

import accessors.RandomTickSpeedAccessor;
import ganymedes01.etfuturum.gamerule.RandomTickSpeed;

public class FixesCore {

    public static void onPreTick() {
        ((RandomTickSpeedAccessor) RandomTickSpeed.INSTANCE).gervermod$resetCache();
    }
}
