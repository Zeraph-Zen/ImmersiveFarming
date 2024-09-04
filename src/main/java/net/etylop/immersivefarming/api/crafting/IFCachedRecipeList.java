package net.etylop.immersivefarming.api.crafting;

import blusunrize.immersiveengineering.api.crafting.cache.IListRecipe;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EventBusSubscriber(modid = ImmersiveFarming.MOD_ID)
public class IFCachedRecipeList<R extends Recipe<?>>
{
    public static final int INVALID_RELOAD_COUNT = -1;
    private static int reloadCount = 0;

    private final Supplier<RecipeType<R>> type;
    private final Class<R> recipeClass;
    private Map<ResourceLocation, R> recipes;
    private boolean cachedDataIsClient;
    private int cachedAtReloadCount = INVALID_RELOAD_COUNT;

    public IFCachedRecipeList(Supplier<RecipeType<R>> type, Class<R> recipeClass)
    {
        this.type = type;
        this.recipeClass = recipeClass;
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent ev)
    {
        ++reloadCount;
    }

    @SubscribeEvent
    public static void onRecipeUpdatedClient(RecipesUpdatedEvent ev)
    {
        ++reloadCount;
    }

    public static int getReloadCount()
    {
        return reloadCount;
    }

    public Collection<R> getRecipes(@Nonnull Level level)
    {
        updateCache(level.getRecipeManager(), level.isClientSide());
        return Objects.requireNonNull(recipes).values();
    }

    public Collection<ResourceLocation> getRecipeNames(@Nonnull Level level)
    {
        updateCache(level.getRecipeManager(), level.isClientSide());
        return Objects.requireNonNull(recipes).keySet();
    }

    public R getById(@Nonnull Level level, ResourceLocation name)
    {
        updateCache(level.getRecipeManager(), level.isClientSide());
        return recipes.get(name);
    }

    private void updateCache(RecipeManager manager, boolean isClient)
    {
        if(recipes!=null&&cachedAtReloadCount==reloadCount&&(!cachedDataIsClient||isClient))
            return;
        this.recipes = manager.getRecipes().stream()
                .filter(iRecipe -> iRecipe.getClass()==recipeClass)
                .flatMap(r -> {
                    if(r instanceof IListRecipe listRecipe)
                        return listRecipe.getSubRecipes().stream();
                    else
                        return Stream.of(r);
                })
                .map(recipeClass::cast)
                .collect(Collectors.toMap(R::getId, Function.identity()));
        this.cachedDataIsClient = isClient;
        this.cachedAtReloadCount = reloadCount;
    }
}
