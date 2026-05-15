package io.drahlek.smartfeedingtroughs;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.api.ModInitializer;

public class SmartFeedingTroughs implements ModInitializer {
    
    @Override
    public void onInitialize() {
        Constants.LOG.info("{} Main Initialize", Constants.MOD_NAME);
        SmartFeedingTroughCommon.init();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                SmartFeedingTroughCommon.registerCommands(dispatcher));
    }
}
