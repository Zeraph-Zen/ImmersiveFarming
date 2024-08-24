package net.etylop.immersivefarming.item;

import net.etylop.immersivefarming.block.IFBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class IFCreativeTab {
    public static final CreativeModeTab TAB = new CreativeModeTab("immersivefarmingtab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(IFBlocks.SPRINKLER.get());
        }
    };
}
