package io.drahlek.smartbreedingtroughs;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class SmartBreedingTroughs {

    public SmartBreedingTroughs(IEventBus eventBus) {
        // Perform logic in that should be executed on both sides

        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        CommonClass.init();
    }
}