package io.drahlek.smartfeedingtroughs.client.renderer;

import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughBlock;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class SmartFeedingTroughRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState[] itemStates = new ItemStackRenderState[SmartFeedingTroughBlock.SLOT_COUNT];
    public Direction facing = Direction.NORTH;
    public float fillLevel;

    public SmartFeedingTroughRenderState() {
        for (int slot = 0; slot < this.itemStates.length; slot++) {
            this.itemStates[slot] = new ItemStackRenderState();
        }
    }
}
