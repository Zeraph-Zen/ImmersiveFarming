package net.etylop.immersivefarming.crafting;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import com.google.gson.JsonObject;
import net.etylop.immersivefarming.api.crafting.ComposterRecipe;
import net.etylop.immersivefarming.block.IFBlocks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class ComposterRecipeSerializer extends IERecipeSerializer<ComposterRecipe>
{
	@Override
	public ItemStack getIcon()
	{
		return new ItemStack(IFBlocks.COMPOSTER.get());
	}

	@Override
	public ComposterRecipe readFromJson(ResourceLocation recipeId, JsonObject json, IContext context)
	{
		boolean fluidProduct = GsonHelper.getAsBoolean(json, "fluidProduct");
		ComposterRecipe recipe;
		if (fluidProduct) {

			FluidStack[] fluidOutputs = new FluidStack[2];
			fluidOutputs[0] = ApiUtils.jsonDeserializeFluidStack(GsonHelper.getAsJsonObject(json, "output0"));
			fluidOutputs[1] = ApiUtils.jsonDeserializeFluidStack(GsonHelper.getAsJsonObject(json, "output1"));
			int energy = GsonHelper.getAsInt(json, "energy");
			if (json.has("input")) {
				IngredientWithSize input = IngredientWithSize.deserialize(json.get("input"));
				recipe = new ComposterRecipe(recipeId, fluidOutputs[0], fluidOutputs[1], input, energy);
			}
			else {
				String inputString = GsonHelper.getAsJsonObject(json, "inputTag").get("tag").getAsString();
				TagKey<Item> input = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(inputString));
				recipe = new ComposterRecipe(recipeId, fluidOutputs[0], fluidOutputs[1], input, energy);
			}

		}
		else {
			FluidTagInput[] fluidInputs = new FluidTagInput[3];
			fluidInputs[0] = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "input0"));
			fluidInputs[1] = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "input1"));
			fluidInputs[2] = FluidTagInput.deserialize(GsonHelper.getAsJsonObject(json, "input2"));
			IngredientWithSize output = IngredientWithSize.deserialize(json.get("result"));
			int energy = GsonHelper.getAsInt(json, "energy");
			recipe = new ComposterRecipe(recipeId, fluidInputs[0], fluidInputs[1], fluidInputs[2], output, energy);
		}
		return recipe;
	}

	@Nullable
	@Override
	public ComposterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
	{
		FluidStack fluidOutput = buffer.readFluidStack();
		FluidTagInput fluidInput = FluidTagInput.read(buffer);
		int ingredientCount = buffer.readInt();
		IngredientWithSize[] itemInputs = new IngredientWithSize[ingredientCount];
		for(int i = 0; i < ingredientCount; i++)
			itemInputs[i] = IngredientWithSize.read(buffer);
		int energy = buffer.readInt();
		return new ComposterRecipe(recipeId, fluidInput, fluidInput,fluidInput, itemInputs[0], energy);
	}

	// TODO
	@Override
	public void toNetwork(FriendlyByteBuf buffer, ComposterRecipe recipe)
	{

	}
}
