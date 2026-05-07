package io.drahlek.smartbreedingtroughs;


import io.drahlek.dirigo.services.NeoForgeBlockRegistrar;
import io.drahlek.dirigo.services.NeoForgeItemRegistrar;
import io.drahlek.dirigo.services.NeoForgeMenuTypeRegistrar;
import io.drahlek.dirigo.services.Services;
import io.drahlek.smartbreedingtroughs.datagen.DataGenerators;
import io.drahlek.smartbreedingtroughs.platform.NeoForgeBlockEntityTypeRegistrar;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(Constants.MOD_ID)
public class SmartBreedingTroughs {

    public SmartBreedingTroughs(IEventBus eventBus) {
        // Perform logic in that should be executed on both sides

        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        eventBus.addListener(DataGenerators::gatherData);
        if (io.drahlek.smartbreedingtroughs.platform.Services.BLOCK_ENTITY_TYPE_REGISTRAR instanceof NeoForgeBlockEntityTypeRegistrar registrar) {
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
        SmartBreedingTroughCommon.init();
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        SmartBreedingTroughCommon.registerCommands(event.getDispatcher());
    }

}
