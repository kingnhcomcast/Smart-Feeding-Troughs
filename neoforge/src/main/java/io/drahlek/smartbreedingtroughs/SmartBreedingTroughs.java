package io.drahlek.smartbreedingtroughs;


import io.drahlek.dirigo.services.NeoForgeBlockRegistrar;
import io.drahlek.dirigo.services.Services;
import io.drahlek.smartbreedingtroughs.platform.NeoForgeBlockEntityTypeRegistrar;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class SmartBreedingTroughs {

    public SmartBreedingTroughs(IEventBus eventBus) {
        // Perform logic in that should be executed on both sides

        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        if (io.drahlek.smartbreedingtroughs.platform.Services.BLOCK_ENTITY_TYPE_REGISTRAR instanceof NeoForgeBlockEntityTypeRegistrar registrar) {
            registrar.initialize(eventBus, Constants.MOD_ID);
        }
        if (Services.BLOCK_REGISTRAR instanceof NeoForgeBlockRegistrar registrar) {
            registrar.initialize(eventBus, Constants.MOD_ID);
        }
        SmartBreedingTroughCommon.init();
    }
}
