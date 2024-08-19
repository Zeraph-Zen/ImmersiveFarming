package net.etylop.immersivefarming.block;

import blusunrize.immersiveengineering.common.blocks.metal.FluidPumpBlock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.item.ModCreativeTab;
import net.etylop.immersivefarming.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    private static final Supplier<BlockBehaviour.Properties> METAL_PROPERTIES_NO_OVERLAY =
            () -> Block.Properties.of(Material.METAL)
                    .sound(SoundType.METAL)
                    .strength(3, 15)
                    .requiresCorrectToolForDrops()
                    .isViewBlocking((state, blockReader, pos) -> false);
    private static final Supplier<BlockBehaviour.Properties> METAL_PROPERTIES_NO_OCCLUSION = () -> METAL_PROPERTIES_NO_OVERLAY.get().noOcclusion();


    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ImmersiveFarming.MOD_ID);

    public static final RegistryObject<Soil> SOIL = registerBlock(
            "soil",
            () -> new Soil(BlockBehaviour.Properties.copy(Blocks.FARMLAND))
    );
    public static final RegistryObject<SprinklerBlock> SPRINKLER = registerBlock(
            "sprinkler",
            () -> new SprinklerBlock(Block.Properties.of(Material.METAL)
                    .sound(SoundType.METAL)
                    .strength(3, 15)
                    .requiresCorrectToolForDrops()
                    .isViewBlocking((state, blockReader, pos) -> false))
    );


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(ModCreativeTab.MOD_TAB)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}

