package io.drahlek.smartfeedingtroughs.datagen;

import io.drahlek.dirigo.datagen.DirigoFabricRecipeProvider;
import io.drahlek.smartfeedingtroughs.Constants;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class FabricRecipeProvider extends DirigoFabricRecipeProvider {
    public FabricRecipeProvider(FabricDataOutput output) {
        super(Constants.GROUP, output);
    }
}
