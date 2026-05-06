package io.drahlek.smartbreedingtroughs.datagen;

import io.drahlek.dirigo.datagen.DirigoNeoForgeRecipeProvider;
import io.drahlek.smartbreedingtroughs.Constants;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class DataGenerators {
    private DataGenerators() {
    }

    public static void gatherData(GatherDataEvent.Server event) {
        event.createProvider((output, lookupProvider) ->
                new DirigoNeoForgeRecipeProvider(Constants.GROUP, output, lookupProvider));
    }
}
