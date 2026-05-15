package io.drahlek.smartfeedingtroughs.datagen;

import io.drahlek.dirigo.datagen.DirigoNeoForgeRecipeProvider;
import io.drahlek.smartfeedingtroughs.Constants;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class DataGenerators {
    private DataGenerators() {
    }

    public static void gatherData(GatherDataEvent event) {
        event.createProvider((output, lookupProvider) ->
                new DirigoNeoForgeRecipeProvider(Constants.GROUP, output, lookupProvider));
    }
}
