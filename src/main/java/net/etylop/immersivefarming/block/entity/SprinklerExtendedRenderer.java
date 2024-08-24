/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package net.etylop.immersivefarming.block.entity;

import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.etylop.immersivefarming.block.utils.IFDynamicModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.List;

public class SprinklerExtendedRenderer extends IEBlockEntityRenderer<SprinklerExtendedBlockEntity>
{
	public static String NAME_SPRINKLER = "sprinkler_extended_top";
	public static IFDynamicModel SPRINKLER_TOP;

	@Override
	public void render(SprinklerExtendedBlockEntity sprinkler, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
	{
		if(sprinkler.isDummy() || !sprinkler.getLevelNonnull().hasChunkAt(sprinkler.getBlockPos().above()))
			return;

		boolean active = sprinkler.getBlockState().getValue(SprinklerBlockEntity.ACTIVE);
		float angle = sprinkler.sprinklerRotation;

		matrixStack.pushPose();

		matrixStack.translate(.5, 1.5, .5);

		matrixStack.pushPose();
		matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), angle, true));
		renderBarrel(SPRINKLER_TOP, matrixStack, bufferIn, combinedLightIn	, combinedOverlayIn);
		matrixStack.popPose();
		matrixStack.popPose();
	}

	private void renderBarrel(IFDynamicModel sprinklerModel, PoseStack matrix, MultiBufferSource buffer, int light, int overlay)
	{
		matrix.pushPose();
		matrix.translate(-.5, -.5, -.5);
		List<BakedQuad> quads = sprinklerModel.get().getQuads(null, null, Utils.RAND, EmptyModelData.INSTANCE);
		RenderUtils.renderModelTESRFast(quads, buffer.getBuffer(RenderType.translucentMovingBlock()), matrix, light, overlay);
		matrix.popPose();
	}

}