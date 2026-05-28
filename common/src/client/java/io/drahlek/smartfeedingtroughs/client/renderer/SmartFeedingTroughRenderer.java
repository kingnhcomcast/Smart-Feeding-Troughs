package io.drahlek.smartfeedingtroughs.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.drahlek.smartfeedingtroughs.blocks.SmartFeedingTroughBlock;
import io.drahlek.smartfeedingtroughs.blocks.entity.SmartFeedingTroughBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SmartFeedingTroughRenderer implements BlockEntityRenderer<SmartFeedingTroughBlockEntity> {
    private static final int MAX_CAPACITY = SmartFeedingTroughBlock.SLOT_COUNT * 64;
    private static final ResourceLocation HAY_TEXTURE = new ResourceLocation("textures/block/hay_block_top.png");
    private static final RenderType HAY_RENDER_TYPE = RenderType.entityCutout(HAY_TEXTURE);
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

    private final ItemRenderer itemRenderer;

    public SmartFeedingTroughRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(
            SmartFeedingTroughBlockEntity blockEntity,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        int itemCount = 0;
        for (int slot = 0; slot < SmartFeedingTroughBlock.SLOT_COUNT; slot++) {
            itemCount += blockEntity.getItem(slot).getCount();
        }
        float fillLevel = Math.min(itemCount, MAX_CAPACITY) / (float) MAX_CAPACITY;

        if (fillLevel > 0.0F) {
            this.renderHayLayer(blockEntity, fillLevel, poseStack, bufferSource, packedLight);
        }

        for (int slot = 0; slot < SmartFeedingTroughBlock.SLOT_COUNT; slot++) {
            if (!blockEntity.getItem(slot).isEmpty()) {
                this.renderSlotItem(blockEntity, fillLevel, poseStack, bufferSource, packedLight, packedOverlay, slot);
            }
        }
    }

    private void renderHayLayer(
            SmartFeedingTroughBlockEntity blockEntity,
            float fillLevel,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        float y = INTERIOR_BOTTOM_Y + fillLevel * INTERIOR_HEIGHT;
        float halfWidth = INTERIOR_WIDTH / 2.0F;
        float halfDepth = INTERIOR_DEPTH / 2.0F;

        poseStack.pushPose();
        poseStack.translate(0.5F, y, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - blockEntity.getBlockState().getValue(SmartFeedingTroughBlock.FACING).toYRot()));

        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();
        VertexConsumer consumer = bufferSource.getBuffer(HAY_RENDER_TYPE);
        consumer.vertex(poseMatrix, -halfWidth, 0.0F, halfDepth)
                .color(255, 255, 255, 255)
                .uv(0.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(poseMatrix, halfWidth, 0.0F, halfDepth)
                .color(255, 255, 255, 255)
                .uv(1.0F, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(poseMatrix, halfWidth, 0.0F, -halfDepth)
                .color(255, 255, 255, 255)
                .uv(1.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
        consumer.vertex(poseMatrix, -halfWidth, 0.0F, -halfDepth)
                .color(255, 255, 255, 255)
                .uv(0.0F, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
        poseStack.popPose();
    }

    private void renderSlotItem(
            SmartFeedingTroughBlockEntity blockEntity,
            float fillLevel,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay,
            int slot
    ) {
        float[] offset = SLOT_OFFSETS[slot];
        float hayTopY = INTERIOR_BOTTOM_Y + fillLevel * INTERIOR_HEIGHT;
        ItemStack stack = blockEntity.getItem(slot);

        poseStack.pushPose();
        poseStack.translate(0.5F, hayTopY + ITEM_SURFACE_OFFSET + slot * 0.001F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - blockEntity.getBlockState().getValue(SmartFeedingTroughBlock.FACING).toYRot()));
        poseStack.translate(offset[0], 0.0F, offset[1]);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
        BakedModel model = this.itemRenderer.getModel(stack, blockEntity.getLevel(), null, slot);
        this.itemRenderer.render(stack, ItemDisplayContext.FIXED, false, poseStack, bufferSource, packedLight, packedOverlay, model);
        poseStack.popPose();
    }
}
