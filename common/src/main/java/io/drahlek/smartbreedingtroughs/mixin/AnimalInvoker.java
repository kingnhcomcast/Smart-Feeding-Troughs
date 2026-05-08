package io.drahlek.smartbreedingtroughs.mixin;

import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Animal.class)
public interface AnimalInvoker {
    @Invoker("playEatingSound")
    void smartbreedingtroughs$playEatingSound();
}
