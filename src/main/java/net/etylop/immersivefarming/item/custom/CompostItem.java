package net.etylop.immersivefarming.item.custom;

import net.etylop.immersivefarming.block.custom.Soil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class CompostItem extends Item {

    private int FERTILITY = 2;

    public CompostItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockState block = pContext.getLevel().getBlockState(pContext.getClickedPos());
        if (!(block.getBlock() instanceof Soil))
            return InteractionResult.FAIL;

        if (block.getValue(Soil.FERTILITY) >= FERTILITY)
            return  InteractionResult.FAIL;

        block.setValue(Soil.FERTILITY, this.FERTILITY);
        pContext.getLevel().setBlock(pContext.getClickedPos(), block.setValue(Soil.FERTILITY, this.FERTILITY), 3);

        //TODO
        pContext.getItemInHand().setCount(pContext.getItemInHand().getCount()-1);
        return InteractionResult.SUCCESS;
    }
}
