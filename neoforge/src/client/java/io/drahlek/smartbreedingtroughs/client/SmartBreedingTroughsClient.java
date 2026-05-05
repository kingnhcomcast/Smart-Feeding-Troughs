package io.drahlek.smartbreedingtroughs.client;

import io.drahlek.smartbreedingtroughs.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import static io.drahlek.smartbreedingtroughs.Constants.MOD_ID;

@Mod(value = MOD_ID, dist = Dist.CLIENT)
public final class SmartBreedingTroughsClient {
    public SmartBreedingTroughsClient(IEventBus modBus) {
        // Perform logic in that should only be executed on the physical client
        Constants.LOG.info("{} Client Initialize", Constants.MOD_NAME);
    }
}
