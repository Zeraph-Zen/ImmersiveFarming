package net.etylop.immersivefarming.block.multiblocks.composter;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.block.IFBlocks;
import net.etylop.immersivefarming.block.multiblocks.IFTemplateMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class ComposterMultiblock extends IFTemplateMultiblock
{
    public ComposterMultiblock()
    {
        super(new ResourceLocation(ImmersiveFarming.MOD_ID, "multiblocks/composter"),
                new BlockPos(1, 1, 1),
                new BlockPos(1, 1, 1),
                new BlockPos(3, 3, 3),
                IFBlocks.COMPOSTER);
    }

    @Override
    public float getManualScale()
    {
        return 12;
    }
}
