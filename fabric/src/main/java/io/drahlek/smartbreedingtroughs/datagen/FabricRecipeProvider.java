package io.drahlek.smartbreedingtroughs.datagen;

import io.drahlek.dirigo.datagen.DirigoFabricRecipeProvider;
import io.drahlek.smartbreedingtroughs.Constants;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class FabricRecipeProvider extends DirigoFabricRecipeProvider {
    public FabricRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(Constants.GROUP, output, registriesFuture);
    }
}
