package net.etylop.immersivefarming.api.crafting.builders;

import blusunrize.immersiveengineering.api.crafting.builders.IEFinishedRecipe;
import com.google.gson.JsonPrimitive;
import net.etylop.immersivefarming.api.crafting.ComposterRecipe;
import net.etylop.immersivefarming.fluid.IFFluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

public class ComposterRecipeBuilder extends IEFinishedRecipe<ComposterRecipeBuilder>
{
    private ComposterRecipeBuilder()
    {
        super(ComposterRecipe.SERIALIZER.get());
        this.setUseInputArray(6);
    }

    public static ComposterRecipeBuilder builder(String tag, int wetMatterAmount, int dryMatterAmount)
    {
        ComposterRecipeBuilder builder = new ComposterRecipeBuilder();
        builder.addWriter(jsonObject -> jsonObject.add("fluidProduct", new JsonPrimitive(true)));
        ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();
        TagKey<Item> tagKey = tagManager.createTagKey(new ResourceLocation(tag));
        builder.addIngredient("inputTag", tagKey);
        builder.addFluid("output0", new FluidStack(IFFluids.WET_MATTER_FLUID.get(), wetMatterAmount));
        builder.addFluid("output1", new FluidStack(IFFluids.DRY_MATTER_FLUID.get(), dryMatterAmount));
        builder.addWriter(jsonObject -> jsonObject.add("energy", new JsonPrimitive(10)));
        return builder;
    }

    public static ComposterRecipeBuilder builder(Item item, int wetMatterAmount, int dryMatterAmount)
    {
        ComposterRecipeBuilder builder = new ComposterRecipeBuilder();
        builder.addWriter(jsonObject -> jsonObject.add("fluidProduct", new JsonPrimitive(true)));
        builder.addIngredient("input", item);
        builder.addFluid("output0", new FluidStack(IFFluids.WET_MATTER_FLUID.get(), wetMatterAmount));
        builder.addFluid("output1", new FluidStack(IFFluids.DRY_MATTER_FLUID.get(), dryMatterAmount));
        builder.addWriter(jsonObject -> jsonObject.add("energy", new JsonPrimitive(10)));
        return builder;
    }
}
