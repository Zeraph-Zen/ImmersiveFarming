package net.etylop.immersivefarming.block.multiblocks.composter;

import blusunrize.immersiveengineering.client.render.tile.BERenderUtils;
import blusunrize.immersiveengineering.client.render.tile.DynamicModel;
import blusunrize.immersiveengineering.client.render.tile.IEBlockEntityRenderer;
import blusunrize.immersiveengineering.client.utils.GuiHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.etylop.immersivefarming.block.IFBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fluids.FluidStack;

public class ComposterRenderer extends IEBlockEntityRenderer<ComposterBlockEntity>
{
    public static final String NAME = "mixer_agitator";
    public static DynamicModel AGITATOR;

    @Override
    public void render(ComposterBlockEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        if(!te.formed||te.isDummy()||!te.getLevelNonnull().hasChunkAt(te.getBlockPos()))
            return;

        final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BlockPos blockPos = te.getBlockPos();
        BlockState state = te.getLevel().getBlockState(blockPos);
        if(state.getBlock()!= IFBlocks.COMPOSTER.get())
            return;

        matrixStack.pushPose();
        matrixStack.translate(.5, .5, .5);

        bufferIn = BERenderUtils.mirror(te, matrixStack, bufferIn);
        matrixStack.pushPose();
        matrixStack.translate(te.getFacing()== Direction.SOUTH||te.getFacing()==Direction.WEST?-.5: .5, 0, te.getFacing()==Direction.SOUTH||te.getFacing()==Direction.EAST?.5: -.5);
        float agitator = te.animation_agitator-(!te.shouldRenderAsActive()?0: (1-partialTicks)*9f);
        matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), agitator, true));

        matrixStack.translate(-0.5, -0.5, -0.5);
        blockRenderer.getModelRenderer().renderModel(
                matrixStack.last(), bufferIn.getBuffer(RenderType.solid()), state, AGITATOR.get(),
                1, 1, 1,
                combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE
        );

        matrixStack.popPose();

        matrixStack.translate(te.getFacing()==Direction.SOUTH||te.getFacing()==Direction.WEST?-.5: .5, -.625f, te.getFacing()==Direction.SOUTH||te.getFacing()==Direction.EAST?.5: -.5);
        matrixStack.scale(.0625f, 1, .0625f);
        matrixStack.mulPose(new Quaternion(90, 0, 0, true));

        for(int i = te.tank.getFluidTypes()-1; i >= 0; i--)
        {
            FluidStack fs = te.tank.fluids.get(i);
            if(fs!=null&&fs.getFluid()!=null)
            {
                float yy = fs.getAmount()/(float)te.tank.getCapacity()*1.0625f;
                matrixStack.translate(0, 0, -yy);
                float w = (i < te.tank.getFluidTypes()-1||yy >= .125)?26: 16+yy/.0125f;
                GuiHelper.drawRepeatedFluidSprite(bufferIn.getBuffer(RenderType.translucent()), matrixStack, fs,
                        -w/2, -w/2, w, w);
            }
        }

        matrixStack.popPose();
    }
}