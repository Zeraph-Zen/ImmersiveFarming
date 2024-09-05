package net.etylop.immersivefarming.utils.integration.jei;

import blusunrize.immersiveengineering.client.utils.GuiHelper;
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

public class ComposterRecipeCategory extends IFRecipeCategory<ComposterRecipe>
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
		if (recipe.fluidProduct)
			return;

		long tankSize = 2*FluidAttributes.BUCKET_VOLUME;
		builder.addSlot(RecipeIngredientRole.INPUT, 48, 3)
				.setFluidRenderer(tankSize, false, 58, 47)
				.addIngredients(ForgeTypes.FLUID_STACK, recipe.getFluidInput()[0].getMatchingFluidStacks())
				.addTooltipCallback(JEIHelper.fluidTooltipCallback);

		builder.addSlot(RecipeIngredientRole.OUTPUT, 139, 3)
				.addItemStack(recipe.itemOutput.getMatchingStacks()[0])
				.setBackground(JEIHelper.slotDrawable, -1, -1);

	}

	@Override
	public void draw(ComposterRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack transform, double mouseX, double mouseY)
	{
		tankTexture.draw(transform, 40, 0);
		arrowDrawable.draw(transform, 117, 19);
		GuiHelper.drawSlot(139, 18, 16, 47, transform);
	}

}