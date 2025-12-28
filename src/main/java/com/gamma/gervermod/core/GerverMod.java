package com.gamma.gervermod.core;

import static com.gamma.gervermod.dim.struct.StructDimHandler.structDim;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gamma.gervermod.Tags;
import com.gamma.gervermod.command.StructWorldCommand;
import com.gamma.gervermod.dim.struct.StructDimHandler;
import com.gamma.gervermod.dim.struct.StructDimTeleporter;
import com.gamma.gervermod.dim.struct.StructWorldProvider;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import appeng.api.features.IWorldGen;
import appeng.core.Api;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mod(modid = GerverMod.MODID, version = Tags.VERSION, name = "GerverMod", acceptedMinecraftVersions = "[1.7.10]")
@EventBusSubscriber
public class GerverMod {

    public static final String MODID = "gervermod";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static boolean eidLoaded = false;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        DimensionManager.registerProviderType(
            StructDimHandler.structDim = DimensionManager.getNextFreeDimId(),
            StructWorldProvider.class,
            false);
        DimensionManager.registerDimension(StructDimHandler.structDim, StructDimHandler.structDim);
    }

    @Mod.EventHandler
    public void onPostInit(final FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("appliedenergistics2")) {
            Api.INSTANCE.registries()
                .worldgen()
                .enableWorldGenForDimension(IWorldGen.WorldGenType.Meteorites, structDim);
            Api.INSTANCE.registries()
                .worldgen()
                .disableWorldGenForDimension(IWorldGen.WorldGenType.CertusQuartz, structDim);
            Api.INSTANCE.registries()
                .worldgen()
                .disableWorldGenForDimension(IWorldGen.WorldGenType.ChargedCertusQuartz, structDim);
        }

        eidLoaded = Loader.isModLoaded("endlessids");
    }

    @Mod.EventHandler
    public void onServerStarting(final FMLServerStartingEvent event) {

        event.registerServerCommand(new StructWorldCommand());

        World world = event.getServer()
            .getEntityWorld();
        if (!world.getGameRules()
            .hasRule("structureWorldResetTime"))
            world.getGameRules()
                .addGameRule("structureWorldResetTime", "86400000");
    }

    @SubscribeEvent
    public static void onTick(final TickEvent event) {
        if (event.side.isServer()) {
            if (event.phase == TickEvent.Phase.START) {
                FixesCore.onPreTick();
            } else {
                StructDimHandler.onTick();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player.dimension == structDim && !StructDimHandler.joinsAllowed()) {
            MinecraftServer.getServer()
                .getConfigurationManager()
                .transferPlayerToDimension(
                    (EntityPlayerMP) event.player,
                    0,
                    new StructDimTeleporter(
                        MinecraftServer.getServer()
                            .worldServerForDimension(0)));
        }
    }
}
