package net.etylop.immersivefarming.crafting;

import blusunrize.immersiveengineering.api.crafting.IERecipeSerializer;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.api.crafting.ComposterRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class IFRecipeSerializer {

    private static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ImmersiveFarming.MOD_ID);

    public static final RegistryObject<IERecipeSerializer<ComposterRecipe>> COMPOSTER_SERIALIZER = registerSerializer(
            "composter", ComposterRecipeSerializer::new
    );

    public static <T extends RecipeSerializer<?>> RegistryObject<T> registerSerializer(String name, Supplier<T> serializer){
        return REGISTER.register(name, serializer);
    }

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }


}
