package io.drahlek.smartfeedingtroughs.items;

import io.drahlek.dirigo.annotation.Item;
import io.drahlek.dirigo.registrars.BlockRegistrar;
import io.drahlek.smartfeedingtroughs.blocks.EdibleHayBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Item(id = EdibleHayBlock.NAME, creativeTab = "functional_blocks")
public class EdibleHayBlockItem extends BlockItem {
    public EdibleHayBlockItem(Properties properties) {
        super(BlockRegistrar.blocks.get(EdibleHayBlock.NAME).get(), properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.smartfeedingtroughs.edible_hay_block"));
    }
}
