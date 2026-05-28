package io.drahlek.smartfeedingtroughs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughBlock;
import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SmartFeedingTroughRenderer implements BlockEntityRenderer<SmartFeedingTroughBlockEntity, SmartFeedingTroughRenderState> {
    private static final int MAX_CAPACITY = SmartFeedingTroughBlock.SLOT_COUNT * 64;
    private static final Identifier HAY_TEXTURE = Identifier.withDefaultNamespace("textures/block/hay_block_top.png");
    private static final RenderType HAY_RENDER_TYPE = RenderTypes.entityCutout(HAY_TEXTURE);
    private static final float[][] SLOT_OFFSETS = {
            {-0.19F, -0.10F},
            {0.19F, -0.10F},
            {-0.19F, 0.10F},
            {0.19F, 0.10F}
    };
    private static final float INTERIOR_MIN_X = 2.0F / 16.0F;
    private static final float INTERIOR_MIN_Z = 5.0F / 16.0F;
    private static final float INTERIOR_WIDTH = 12.0F / 16.0F;
    private static final float INTERIOR_DEPTH = 6.0F / 16.0F;
    private static final float INTERIOR_BOTTOM_Y = 2.0F / 16.0F;
    private static final float INTERIOR_HEIGHT = 6.0F / 16.0F;
    private static final float ITEM_SURFACE_OFFSET = 0.025F;
    private static final float ITEM_SCALE = 0.31F;

    private final ItemModelResolver itemModelResolver;

    public SmartFeedingTroughRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public SmartFeedingTroughRenderState createRenderState() {
        return new SmartFeedingTroughRenderState();
    }

    @Override
    public void extractRenderState(
            SmartFeedingTroughBlockEntity blockEntity,
            SmartFeedingTroughRenderState state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.facing = blockEntity.getBlockState().getValue(SmartFeedingTroughBlock.FACING);

        int itemCount = 0;
        for (int slot = 0; slot < state.itemStates.length; slot++) {
            ItemStack stack = blockEntity.getItem(slot);
            itemCount += stack.getCount();
            this.itemModelResolver.updateForTopItem(state.itemStates[slot], stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, slot);
        }

        state.fillLevel = Math.min(itemCount, MAX_CAPACITY) / (float) MAX_CAPACITY;
    }

    @Override
    public void submit(SmartFeedingTroughRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (state.fillLevel > 0.0F) {
            this.submitHayLayer(state, poseStack, submitNodeCollector);
        }

        for (int slot = 0; slot < state.itemStates.length; slot++) {
            if (!state.itemStates[slot].isEmpty()) {
                this.submitSlotItem(state, poseStack, submitNodeCollector, slot);
            }
        }
    }

    private void submitHayLayer(SmartFeedingTroughRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        float y = INTERIOR_BOTTOM_Y + state.fillLevel * INTERIOR_HEIGHT;
        float halfWidth = INTERIOR_WIDTH / 2.0F;
        float halfDepth = INTERIOR_DEPTH / 2.0F;

        poseStack.pushPose();
        poseStack.translate(0.5F, y, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.facing.toYRot()));
        submitNodeCollector.submitCustomGeometry(poseStack, HAY_RENDER_TYPE, (pose, consumer) -> {
            consumer.addVertex(pose, -halfWidth, 0.0F, halfDepth)
                    .setColor(255, 255, 255, 255)
                    .setUv(0.0F, 1.0F)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(state.lightCoords)
                    .setNormal(pose, 0.0F, 1.0F, 0.0F);
            consumer.addVertex(pose, halfWidth, 0.0F, halfDepth)
                    .setColor(255, 255, 255, 255)
                    .setUv(1.0F, 1.0F)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(state.lightCoords)
                    .setNormal(pose, 0.0F, 1.0F, 0.0F);
            consumer.addVertex(pose, halfWidth, 0.0F, -halfDepth)
                    .setColor(255, 255, 255, 255)
                    .setUv(1.0F, 0.0F)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(state.lightCoords)
                    .setNormal(pose, 0.0F, 1.0F, 0.0F);
            consumer.addVertex(pose, -halfWidth, 0.0F, -halfDepth)
                    .setColor(255, 255, 255, 255)
                    .setUv(0.0F, 0.0F)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(state.lightCoords)
                    .setNormal(pose, 0.0F, 1.0F, 0.0F);
        });
        poseStack.popPose();
    }

    private void submitSlotItem(SmartFeedingTroughRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int slot) {
        float[] offset = SLOT_OFFSETS[slot];
        float hayTopY = INTERIOR_BOTTOM_Y + state.fillLevel * INTERIOR_HEIGHT;

        poseStack.pushPose();
        poseStack.translate(0.5F, hayTopY + ITEM_SURFACE_OFFSET + slot * 0.001F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.facing.toYRot()));
        poseStack.translate(offset[0], 0.0F, offset[1]);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        state.itemStates[slot].submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }
}
