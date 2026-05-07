package io.drahlek.smartbreedingtroughs.client;

import io.drahlek.smartbreedingtroughs.Constants;
import io.drahlek.smartbreedingtroughs.blocks.SmartBreedingTroughMenu;
import io.drahlek.smartbreedingtroughs.blocks.SmartBreedingTroughScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static io.drahlek.smartbreedingtroughs.Constants.MOD_ID;

@Mod(value = MOD_ID, dist = Dist.CLIENT)
public final class SmartBreedingTroughsClient {
    public SmartBreedingTroughsClient(IEventBus modBus) {
        // Perform logic in that should only be executed on the physical client
        Constants.LOG.info("{} Client Initialize", Constants.MOD_NAME);
        modBus.addListener(this::registerMenuScreens);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(SmartBreedingTroughMenu.SMART_TROUGH.get(), SmartBreedingTroughScreen::new);
    }
}
