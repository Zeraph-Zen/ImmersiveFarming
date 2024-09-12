package net.etylop.immersivefarming.common.data.generators;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.api.crafting.builders.ComposterRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class IFRecipes extends RecipeProvider{
    private final Map<String, Integer> PATH_COUNT = new HashMap<>();

    protected Consumer<FinishedRecipe> out;
    public IFRecipes(DataGenerator generatorIn){
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> out){
        this.out = out;

        composterRecipes(out);
    }

    private void composterRecipes(Consumer<FinishedRecipe> out) {
        ComposterRecipeBuilder.builder("forge:seeds", 10, 0).build(out, toRL("composter/seed"));
        ComposterRecipeBuilder.builder("forge:crops", 10, 0).build(out, toRL("composter/crops"));
        ComposterRecipeBuilder.builder("forge:fruits", 10, 0).build(out, toRL("composter/fruits"));
        ComposterRecipeBuilder.builder("forge:vegetables", 10, 0).build(out, toRL("composter/vegetables"));
        ComposterRecipeBuilder.builder("forge:fruit", 10, 0).build(out, toRL("composter/fruit"));
        ComposterRecipeBuilder.builder("forge:vegetable", 10, 0).build(out, toRL("composter/vegetable"));

        ComposterRecipeBuilder.builder(Items.MELON, 20, 0).build(out, toRL("composter/melon"));
        ComposterRecipeBuilder.builder(Items.MELON_SLICE, 2, 0).build(out, toRL("composter/melon_slice"));
        ComposterRecipeBuilder.builder(Items.PUMPKIN, 20, 0).build(out, toRL("composter/pumpkin"));
    }

    private ResourceLocation toRL(String loc) {
        return new ResourceLocation(ImmersiveFarming.MOD_ID,"crafting/"+loc);
    }
}