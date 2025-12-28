package com.gamma.gervermod.mixin.fixes;

import net.minecraft.world.GameRules;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import accessors.RandomTickSpeedAccessor;
import ganymedes01.etfuturum.gamerule.RandomTickSpeed;

@Mixin(RandomTickSpeed.class)
public abstract class RandomTickSpeedMixin implements RandomTickSpeedAccessor {

    @Shadow(remap = false)
    @Final
    public static String GAMERULE_NAME;
    @Shadow(remap = false)
    @Final
    public static String DEFAULT_VALUE;

    @Unique
    private static int gervermod$cachedValue;
    @Unique
    private static boolean gervermod$hasCachedValue = false;

    /**
     * @author BallOfEnergy01
     * @reason Performance fixes.
     */
    @Overwrite(remap = false)
    public int getRandomTickSpeed(GameRules gameRulesInstance) {
        if (gervermod$hasCachedValue) {
            return gervermod$cachedValue;
        } else {
            gervermod$hasCachedValue = true;
            return gervermod$cachedValue = Integer.parseInt(
                StringUtils.defaultIfEmpty(gameRulesInstance.getGameRuleStringValue(GAMERULE_NAME), DEFAULT_VALUE));
        }
    }

    @Override
    public void gervermod$resetCache() {
        gervermod$hasCachedValue = false;
    }
}
