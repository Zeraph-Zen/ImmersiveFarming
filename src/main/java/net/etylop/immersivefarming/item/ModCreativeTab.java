package net.etylop.immersivefarming.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

public class ModCreativeTab {
    public static final CreativeModeTab MOD_TAB = new CreativeModeTab("immersivefarmingtab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.FARMLAND.asItem());
        }
    };
}
