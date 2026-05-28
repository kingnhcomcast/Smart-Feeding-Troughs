package io.drahlek.smartfeedingtroughs.datagen;

import io.drahlek.dirigo.datagen.DirigoNeoForgeRecipeProvider;
import io.drahlek.smartfeedingtroughs.Constants;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;

public final class DataGenerators {
    private DataGenerators() {
    }

    public static void gatherData(GatherDataEvent event) {
        PackOutput output = event.getGenerator().getPackOutput();
        event.getGenerator().addProvider(event.includeServer(),
                new DirigoNeoForgeRecipeProvider(Constants.GROUP, output));
    }
}
