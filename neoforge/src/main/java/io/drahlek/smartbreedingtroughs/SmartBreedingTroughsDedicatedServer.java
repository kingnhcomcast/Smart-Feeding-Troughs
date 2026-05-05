package io.drahlek.smartbreedingtroughs;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import static io.drahlek.smartbreedingtroughs.Constants.MOD_ID;

@Mod(value = MOD_ID, dist = Dist.DEDICATED_SERVER)
public class SmartBreedingTroughsDedicatedServer {
    public SmartBreedingTroughsDedicatedServer(IEventBus modBus) {
        Constants.LOG.info("{} DedicatedServer Initialize", Constants.MOD_ID);
    }
}