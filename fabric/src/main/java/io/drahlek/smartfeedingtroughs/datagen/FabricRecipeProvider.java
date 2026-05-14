package io.drahlek.smartfeedingtroughs.datagen;

import io.drahlek.dirigo.datagen.DirigoFabricRecipeProvider;
import io.drahlek.smartfeedingtroughs.Constants;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class FabricRecipeProvider extends DirigoFabricRecipeProvider {
    public FabricRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(Constants.GROUP, output, registriesFuture);
    }
}
