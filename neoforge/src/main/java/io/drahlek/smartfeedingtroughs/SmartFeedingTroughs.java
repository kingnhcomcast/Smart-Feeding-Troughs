package io.drahlek.smartfeedingtroughs;


import io.drahlek.dirigo.services.NeoForgeBlockRegistrar;
import io.drahlek.dirigo.services.NeoForgeBlockEntityTypeRegistrar;
import io.drahlek.dirigo.services.NeoForgeItemRegistrar;
import io.drahlek.dirigo.services.NeoForgeMenuTypeRegistrar;
import io.drahlek.dirigo.services.Services;
import io.drahlek.smartfeedingtroughs.datagen.DataGenerators;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class SmartFeedingTroughs {

    public SmartFeedingTroughs() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Perform logic in that should be executed on both sides

        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        eventBus.addListener(DataGenerators::gatherData);
        if (Services.BLOCK_ENTITY_TYPE_REGISTRAR instanceof NeoForgeBlockEntityTypeRegistrar registrar) {
            registrar.initialize(eventBus, Constants.MOD_ID);
        }
        if (Services.MENU_TYPE_REGISTRAR instanceof NeoForgeMenuTypeRegistrar registrar) {
            registrar.initialize(eventBus, Constants.MOD_ID);
        }
        if (Services.BLOCK_REGISTRAR instanceof NeoForgeBlockRegistrar registrar) {
            registrar.initialize(eventBus, Constants.MOD_ID);
        }
        if (Services.ITEM_REGISTRAR instanceof NeoForgeItemRegistrar registrar) {
            registrar.initialize(eventBus, Constants.MOD_ID);
        }
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> initClient(eventBus));
        SmartFeedingTroughCommon.init();
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private static void initClient(IEventBus eventBus) {
        try {
            Class<?> clientClass = Class.forName("io.drahlek.smartfeedingtroughs.client.SmartFeedingTroughsClient");
            clientClass.getMethod("init", IEventBus.class).invoke(null, eventBus);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to initialize Smart Feeding Troughs NeoForge client hooks", e);
        }
    }

    private void registerCommands(RegisterCommandsEvent event) {
        SmartFeedingTroughCommon.registerCommands(event.getDispatcher());
    }

}
