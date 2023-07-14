package com.divinity.anythingisay;

import com.divinity.anythingisay.datagen.*;
import com.divinity.anythingisay.init.BlockInit;
import com.divinity.anythingisay.init.EntityInit;
import com.divinity.anythingisay.init.ItemInit;
import com.divinity.anythingisay.cap.PlayerHolderAttacher;
import com.divinity.anythingisay.config.ExampleClientConfig;
import com.divinity.anythingisay.config.ExampleConfig;
import com.divinity.anythingisay.network.NetworkHandler;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AnythingISay.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AnythingISay {
    public static final String MODID = "anythingisay";
    public static final Logger LOGGER = LogManager.getLogger();

    public AnythingISay() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ExampleConfig.CONFIG_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ExampleClientConfig.CLIENT_SPEC);

        ItemInit.ITEMS.register(modBus);
        EntityInit.ENTITIES.register(modBus);
        BlockInit.BLOCKS.register(modBus);
        BlockInit.BLOCK_ENTITIES.register(modBus);

        PlayerHolderAttacher.register();
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        boolean includeServer = event.includeServer();
        boolean includeClient = event.includeClient();

        generator.addProvider(includeServer, new ModRecipeProvider(generator));
        generator.addProvider(includeServer, new ModLootTableProvider(generator));
        generator.addProvider(includeServer, new ModSoundProvider(generator, existingFileHelper));
        generator.addProvider(includeServer, new ModTagProvider.Blocks(generator, existingFileHelper));
        generator.addProvider(includeServer, new ModTagProvider.Items(generator, existingFileHelper));
        generator.addProvider(includeClient, new ModItemModelProvider(generator, existingFileHelper));
        generator.addProvider(includeClient, new ModBlockStateProvider(generator, existingFileHelper));
        generator.addProvider(includeClient, new ModLangProvider(generator));
    }
}
