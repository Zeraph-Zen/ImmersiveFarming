package net.etylop.immersivefarming.block.custom;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.IEEntityBlock;
import net.etylop.immersivefarming.block.IFBlockEntities;
import net.etylop.immersivefarming.block.entity.SprinklerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class SprinklerBlock extends IEEntityBlock<SprinklerBlockEntity>
{
    public SprinklerBlock(Properties props)
    {
        super(IFBlockEntities.SPRINKLER, props);
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(IEProperties.MULTIBLOCKSLAVE, false)
                .setValue(SprinklerBlockEntity.ACTIVE, false)
                .setValue(SprinklerBlockEntity.USING_PESTICIDE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(IEProperties.FACING_HORIZONTAL, IEProperties.MULTIBLOCKSLAVE, BlockStateProperties.WATERLOGGED);
        builder.add(SprinklerBlockEntity.ACTIVE);
        builder.add(SprinklerBlockEntity.USING_PESTICIDE);
    }

    @Override
    public boolean canIEBlockBePlaced(BlockState newState, BlockPlaceContext context)
    {
        BlockPos start = context.getClickedPos();
        return areAllReplaceable(start, start.above(1), context);
    }
}