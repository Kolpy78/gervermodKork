package com.gamma.gervermod.core;

import static com.gamma.gervermod.dim.struct.StructDimHandler.structDim;

import com.gamma.gervermod.dim.struct.StructWorldDesert;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gamma.gervermod.Tags;
import com.gamma.gervermod.command.AdminStructWorldCommand;
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

@Mod(
    modid = GerverMod.MODID,
    version = Tags.VERSION,
    name = "GerverMod",
    dependencies = "required-after:hbm",
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*")
@EventBusSubscriber
public class GerverMod {

    public static final String MODID = "gervermod";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static boolean eidLoaded = false;

    @Mod.Instance(MODID)
    public static GerverMod instance;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        DimensionManager.registerProviderType(StructDimHandler.structDim, StructWorldProvider.class, false);
        DimensionManager.registerProviderType(StructDimHandler.desertStructDim, StructWorldDesert.class, false);
        DimensionManager.registerDimension(StructDimHandler.structDim, StructDimHandler.structDim);
        DimensionManager.registerDimension(StructDimHandler.desertStructDim, StructDimHandler.desertStructDim);
    }

    @Mod.EventHandler
    public void Init(final FMLInitializationEvent event){
        ItemStack coal = new ItemStack(Items.coal);
        ItemStack skull = new ItemStack(Items.skull);
        GameRegistry.addRecipe(new ItemStack(Items.skull,1, 1),
            "AAA",
            "ABA",
            "AAA",
            'A', coal,
            'B', skull);
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
        event.registerServerCommand(new AdminStructWorldCommand());

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
