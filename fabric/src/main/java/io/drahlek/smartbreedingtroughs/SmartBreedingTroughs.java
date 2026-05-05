package io.drahlek.smartbreedingtroughs;

import net.fabricmc.api.ModInitializer;

public class SmartBreedingTroughs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        CommonClass.init();
    }
}
