package io.drahlek.smartbreedingtroughs.items;

import io.drahlek.dirigo.annotation.Item;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import io.drahlek.smartbreedingtroughs.blocks.SmartBreedingTroughBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

@Item(id = SmartBreedingTroughBlock.NAME, creativeTab = "functional_blocks")
public class SmartBreedingTroughItem extends BlockItem {

    public SmartBreedingTroughItem(Properties properties) {
        super(BlockRegistrar.blocks.get(SmartBreedingTroughBlock.NAME).get(), properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay display, Consumer<Component> textConsumer, TooltipFlag tooltipFlag) {
        textConsumer.accept(Component.translatable("tooltip.smartbreedingtroughs.smart_trough"));
    }
}
