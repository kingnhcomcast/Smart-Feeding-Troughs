package io.drahlek.smartbreedingtroughs;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.api.ModInitializer;

public class SmartBreedingTroughs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        SmartBreedingTroughCommon.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                SmartBreedingTroughCommon.registerCommands(dispatcher));
    }
}
