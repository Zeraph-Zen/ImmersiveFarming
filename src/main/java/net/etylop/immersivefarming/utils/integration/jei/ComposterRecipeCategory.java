package net.etylop.immersivefarming.utils.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.api.crafting.ComposterRecipe;
import net.etylop.immersivefarming.block.IFBlocks;
import net.minecraft.world.item.ItemStack;

public class ComposterRecipeCategory extends IFRecipeCategory<ComposterRecipe>
{
	public static final RecipeType<ComposterRecipe> TYPE = RecipeType.create(ImmersiveFarming.MOD_ID, "composter", ComposterRecipe.class);


	public ComposterRecipeCategory(IGuiHelper helper)
	{
		super(TYPE, helper, "block.immersivefarming.composter");
		setBackground(helper.createBlankDrawable(155, 60));
		setIcon(new ItemStack(IFBlocks.COMPOSTER.get()));

	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, ComposterRecipe recipe, IFocusGroup focuses)
	{
		if (recipe.fluidProduct)
			return;

		builder.addSlot(RecipeIngredientRole.OUTPUT, 92, 20)
				.addItemStack(recipe.itemOutput.getMatchingStacks()[0])
				.setBackground(JEIHelper.slotDrawable, -1, -1);

	}

	@Override
	public void draw(ComposterRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack transform, double mouseX, double mouseY)
	{
		transform.pushPose();
		transform.scale(3f, 3f, 1);
		this.getIcon().draw(transform, 10, 2);
		transform.popPose();
	}
}