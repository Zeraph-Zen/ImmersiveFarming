package net.etylop.immersivefarming.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class IFCreativeTab {
    public static final CreativeModeTab TAB = new CreativeModeTab("immersivefarmingtab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Blocks.FARMLAND.asItem());
        }
    };
}
