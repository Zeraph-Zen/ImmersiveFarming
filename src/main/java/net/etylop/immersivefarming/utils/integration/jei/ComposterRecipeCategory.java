/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package net.etylop.immersivefarming.utils.integration.jei;


import blusunrize.immersiveengineering.client.utils.GuiHelper;
import blusunrize.immersiveengineering.common.util.compat.jei.IERecipeCategory;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.api.crafting.ComposterRecipe;
import net.etylop.immersivefarming.block.IFBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidAttributes;

import java.util.Arrays;

public class ComposterRecipeCategory extends IERecipeCategory<ComposterRecipe>
{
	public static final RecipeType<ComposterRecipe> TYPE = RecipeType.create(ImmersiveFarming.MOD_ID, "composter", ComposterRecipe.class);
	private final IDrawableStatic tankTexture;
	private final IDrawableStatic tankOverlay;
	private final IDrawableStatic arrowDrawable;

	public ComposterRecipeCategory(IGuiHelper helper)
	{
		super(TYPE, helper, "block.immersivefarming.composter");
		setBackground(helper.createBlankDrawable(155, 60));
		setIcon(new ItemStack(IFBlocks.COMPOSTER.get()));
		ResourceLocation background = new ResourceLocation(ImmersiveFarming.MOD_ID, "textures/gui/composter.png");
		tankTexture = helper.createDrawable(background, 68, 8, 74, 60);
		tankOverlay = helper.drawableBuilder(background, 177, 31, 20, 51).addPadding(-2, 2, -2, 2).build();
		arrowDrawable = helper.createDrawable(background, 178, 17, 18, 13);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ComposterRecipe recipe, IFocusGroup focuses)
	{
		long tankSize = Math.max(2*FluidAttributes.BUCKET_VOLUME,  Math.max(recipe.fluidInput.getAmount(),recipe.fluidOutput.getAmount()));
		builder.addSlot(RecipeIngredientRole.INPUT, 48, 3)
				.setFluidRenderer(tankSize, false, 58, 47)
				.addIngredients(ForgeTypes.FLUID_STACK, recipe.fluidInput.getMatchingFluidStacks())
				.addTooltipCallback(JEIHelper.fluidTooltipCallback);

		builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 3)
				.setFluidRenderer(tankSize, false, 16, 47)
				.setOverlay(tankOverlay, 0, 0)
				.addIngredient(ForgeTypes.FLUID_STACK, recipe.fluidOutput)
				.addTooltipCallback(JEIHelper.fluidTooltipCallback);

		for(int i = 0; i < recipe.itemInputs.length; i++)
		{
			int x = (i%2)*18+1;
			int y = i/2*18+1;
			builder.addSlot(RecipeIngredientRole.INPUT, x, y)
					.addItemStacks(Arrays.asList(recipe.itemInputs[i].getMatchingStacks()))
					.setBackground(JEIHelper.slotDrawable, -1, -1);
		}
	}

	@Override
	public void draw(ComposterRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack transform, double mouseX, double mouseY)
	{
		tankTexture.draw(transform, 40, 0);
		arrowDrawable.draw(transform, 117, 19);
		GuiHelper.drawSlot(139, 18, 16, 47, transform);
	}

}