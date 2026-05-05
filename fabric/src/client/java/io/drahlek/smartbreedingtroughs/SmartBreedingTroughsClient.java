package io.drahlek.smartbreedingtroughs;

import net.fabricmc.api.ClientModInitializer;

public class SmartBreedingTroughsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOG.info("{} Client Initialize", Constants.MOD_NAME);    }
}
