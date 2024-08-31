package net.etylop.immersivefarming.block;

import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import blusunrize.immersiveengineering.common.blocks.metal.MetalMultiblockBlock;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.block.custom.Soil;
import net.etylop.immersivefarming.block.custom.SprinklerBlock;
import net.etylop.immersivefarming.block.custom.SprinklerExtendedBlock;
import net.etylop.immersivefarming.block.multiblocks.composter.ComposterBlockEntity;
import net.etylop.immersivefarming.item.IFCreativeTab;
import net.etylop.immersivefarming.item.IFItems;
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

public class IFBlocks {

    public static final BlockBehaviour.Properties METAL_NO_OCCLUSION = Block.Properties.of(Material.METAL)
            .sound(SoundType.METAL)
            .strength(3, 15)
            .requiresCorrectToolForDrops()
            .isViewBlocking((state, blockReader, pos) -> false)
            .noOcclusion();

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ImmersiveFarming.MOD_ID);

    public static final RegistryObject<Soil> SOIL = registerBlock(
            "soil",
            () -> new Soil(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(Blocks.FARMLAND))
    );

    public static final RegistryObject<SprinklerBlock> SPRINKLER = registerBlock(
            "sprinkler",
            () -> new SprinklerBlock(METAL_NO_OCCLUSION)
    );

    public static final RegistryObject<SprinklerExtendedBlock> SPRINKLER_EXTENDED = registerBlock(
            "sprinkler_extended",
            () -> new SprinklerExtendedBlock(METAL_NO_OCCLUSION)
    );

    public static final RegistryObject<MetalMultiblockBlock<ComposterBlockEntity>> COMPOSTER = registerMultiblock(
            "composter",
            () -> new MetalMultiblockBlock<>(IFBlockEntities.COMPOSTER, METAL_NO_OCCLUSION)
    );


    private static <T extends Block> RegistryObject<T> registerMultiblock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return IFItems.ITEMS.register(name, () -> new BlockItemIE(block.get(),
                new Item.Properties().tab(IFCreativeTab.TAB)));
    }
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}

