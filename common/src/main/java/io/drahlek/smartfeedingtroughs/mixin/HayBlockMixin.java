package io.drahlek.smartfeedingtroughs.mixin;

import io.drahlek.dirigo.registrars.BlockRegistrar;
import io.drahlek.smartfeedingtroughs.blocks.EdibleHayBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HayBlock.class)
public abstract class HayBlockMixin extends RotatedPillarBlock {
    public HayBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!itemStack.is(Items.SHEARS)) {
            return InteractionResult.PASS;
        }

        if (!state.is(Blocks.HAY_BLOCK)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            Block edibleHay = BlockRegistrar.blocks.get(EdibleHayBlock.NAME).get();
            level.setBlock(pos, edibleHay.defaultBlockState()
                    .setValue(HayBlock.AXIS, state.getValue(HayBlock.AXIS)), 3);

            level.playSound(
                    null,
                    pos,
                    SoundEvents.SHEEP_SHEAR,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );

            itemStack.hurtAndBreak(1, player, owner -> owner.broadcastBreakEvent(hand));
        }
        return InteractionResult.SUCCESS;
    }
}
