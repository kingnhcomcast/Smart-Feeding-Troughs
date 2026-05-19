package io.drahlek.smartfeedingtroughs.blocks;

import io.drahlek.dirigo.annotation.Block;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

@Block(id = EdibleHayBlock.NAME, registerItem = false)
public class EdibleHayBlock extends HayBlock {
    public static final String NAME = "edible_hay_block";

    public EdibleHayBlock(Properties properties) {
        super(properties
                .mapColor(MapColor.COLOR_YELLOW)
                .instrument(NoteBlockInstrument.BANJO)
                .strength(0.5F)
                .sound(SoundType.GRASS)
        );
    }
}
