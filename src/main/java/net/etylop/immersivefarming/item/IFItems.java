package net.etylop.immersivefarming.item;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.fluid.IFFluids;
import net.etylop.immersivefarming.item.custom.CompostItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IFItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ImmersiveFarming.MOD_ID);

    public static final RegistryObject<Item> TREATED_WATER_BUCKET = ITEMS.register("treated_water_bucket",
            () -> new BucketItem(IFFluids.TREATED_WATER_FLUID,
                    new Item.Properties().tab(IFCreativeTab.TAB).stacksTo(1)));

    public static final RegistryObject<CompostItem> COMPOST = ITEMS.register("compost",
            () -> new CompostItem(new Item.Properties().tab(IFCreativeTab.TAB)));

    public static final RegistryObject<Item> WHEEL = ITEMS.register("wheel",
            () -> new Item(new Item.Properties().tab(IFCreativeTab.TAB)));

    public static final RegistryObject<Item> PLOW = ITEMS.register("plow",
            () -> new CartItem(new Item.Properties().stacksTo(1).tab(IFCreativeTab.TAB)));

    public static final RegistryObject<Item> SOWER = ITEMS.register("sower",
            () -> new CartItem(new Item.Properties().stacksTo(1).tab(IFCreativeTab.TAB)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
