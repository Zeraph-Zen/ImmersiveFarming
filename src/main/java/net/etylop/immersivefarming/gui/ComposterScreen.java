package net.etylop.immersivefarming.gui;

import blusunrize.immersiveengineering.client.gui.IEContainerScreen;
import blusunrize.immersiveengineering.client.gui.info.EnergyInfoArea;
import blusunrize.immersiveengineering.client.gui.info.FluidInfoArea;
import blusunrize.immersiveengineering.client.gui.info.InfoArea;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.api.crafting.ComposterRecipe;
import net.etylop.immersivefarming.block.multiblocks.composter.ComposterBlockEntity;
import net.etylop.immersivefarming.gui.container.ComposterContainer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;
import java.util.List;

public class ComposterScreen extends IEContainerScreen<ComposterContainer>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(ImmersiveFarming.MOD_ID, "textures/gui/composter.png");

	private final ComposterBlockEntity tile;

	public ComposterScreen(ComposterContainer container, Inventory inventoryPlayer, Component component)
	{
		super(container, inventoryPlayer, component, TEXTURE);
		this.tile = container.tile;
	}

	@Nonnull
	@Override
	protected List<InfoArea> makeInfoAreas()
	{
		return ImmutableList.of(
				new EnergyInfoArea(leftPos+157, topPos+21, tile.energyStorage),
				new FluidInfoArea(tile.tanks[0], new Rect2i(leftPos+109, topPos+20, 16, 47), 177, 31, 20, 51, TEXTURE),
				new FluidInfoArea(tile.tanks[1], new Rect2i(leftPos+82, topPos+20, 16, 47), 177, 31, 20, 51, TEXTURE),
				new FluidInfoArea(tile.tanks[2], new Rect2i(leftPos+55, topPos+20, 16, 47), 177, 31, 20, 51, TEXTURE)
		);
	}


	@Override
	protected void drawContainerBackgroundPre(@Nonnull PoseStack transform, float f, int mx, int my)
	{
		transform.pushPose();
		MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

		for(MultiblockProcess<ComposterRecipe> process : tile.processQueue)
			if(process instanceof MultiblockProcessInMachine<?> inMachine)
			{
				float mod = 1-(process.processTick/(float)process.getMaxTicks(tile.getLevel()));
				for(int slot : inMachine.getInputSlots())
				{
					int h = (int)Math.max(1, mod*16);
					this.blit(transform, leftPos+24+slot%2*21, topPos+7+slot/2*18+(16-h), 176, 16-h, 2, h);
				}
			}

		buffers.endBatch();
	}
}
