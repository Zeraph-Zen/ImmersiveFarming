package net.etylop.immersivefarming.api.crafting;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class IFRecipeTypes{
    private static final DeferredRegister<RecipeType<?>> REGISTER = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, ImmersiveFarming.MOD_ID);

    public static final RegistryObject<RecipeType<ComposterRecipe>> COMPOSTER = makeType("composter");


    public static void register(IEventBus eventBus){
        REGISTER.register(eventBus);
    }

    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> makeType(String name){
        return REGISTER.register(name, () -> new RecipeType<T>(){
            @Override
            public String toString(){
                return ImmersiveFarming.MOD_ID + ":" + name;
            }
        });
    }
}