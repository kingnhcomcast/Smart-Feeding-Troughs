package io.drahlek.smartfeedingtroughs;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import static io.drahlek.smartfeedingtroughs.Constants.MOD_ID;

@Mod(value = MOD_ID, dist = Dist.DEDICATED_SERVER)
public class SmartFeedingTroughsDedicatedServer {
    public SmartFeedingTroughsDedicatedServer(IEventBus modBus) {
        Constants.LOG.info("{} DedicatedServer Initialize", Constants.MOD_ID);
    }
}