package net.etylop.immersivefarming.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.Tags;

import java.util.Random;

public class Soil extends FarmBlock {
    public static final int TILL_MAX = 7;
    public static final IntegerProperty TILL = IntegerProperty.create("till",0,TILL_MAX);

    public Soil(Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(TILL, 0)
        );
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
        if (state.hasProperty(Soil.TILL) && state.getValue(Soil.TILL) == Soil.TILL_MAX) {
            BlockState farmlandState = Blocks.FARMLAND.defaultBlockState();
            return farmlandState.canSustainPlant(level, pos, facing, plantable);
        }
        return false;
    }

    private static final int getHoeSpeed(Item item) {
        if (!(item instanceof HoeItem)) return 0;
        TagKey<Block> tag = ((HoeItem) item).getTier().getTag();
        if (tag == Tags.Blocks.NEEDS_WOOD_TOOL || tag == Tags.Blocks.NEEDS_GOLD_TOOL) {
            return 1;
        }
        else if (tag == BlockTags.NEEDS_STONE_TOOL) {
            return 1 + (Math.random()>0.5 ? 1 : 0);
        }
        else if (tag == BlockTags.NEEDS_IRON_TOOL) {
            return 2;
        }
        else if (tag == BlockTags.NEEDS_DIAMOND_TOOL) {
            return 2 + (Math.random()>0.5 ? 1 : 0);
        }
        else if (tag == Tags.Blocks.NEEDS_NETHERITE_TOOL) {
            return 3;
        }
        return 1;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand == InteractionHand.MAIN_HAND &&
                pPlayer.getMainHandItem().getItem() instanceof HoeItem &&
                pLevel.isEmptyBlock(pPos.above()) &&
                pState.getValue(TILL) < TILL_MAX) {
            pLevel.playSound(pPlayer, pPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!pLevel.isClientSide()) {
                int speed = getHoeSpeed(pPlayer.getMainHandItem().getItem());
                int newTill = Math.min(TILL_MAX,pState.getValue(TILL)+speed);
                pLevel.setBlock(pPos, pState.setValue(TILL, newTill), 3);
                pPlayer.getMainHandItem().hurtAndBreak(1, pPlayer, (val) -> {
                    val.broadcastBreakEvent(pPlayer.getUsedItemHand());
                });
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TILL);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {
        int i = pState.getValue(MOISTURE);
        if (!isNearWater(pLevel, pPos) && !pLevel.isRainingAt(pPos.above())) {
            if (i > 0) {
                pLevel.setBlock(pPos, pState.setValue(MOISTURE, Integer.valueOf(i - 1)), 2);
            } else if (!isUnderCrops(pLevel, pPos)) {
                turnToDirt(pState, pLevel, pPos);
            } else {
                turnToDirt(pState, pLevel, pPos);
            }
        } else if (i < 7) {
            pLevel.setBlock(pPos, pState.setValue(MOISTURE, Integer.valueOf(7)), 2);
        }

    }


    private static boolean isNearWater(LevelReader pLevel, BlockPos pPos) {
        for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-1, 0, -1), pPos.offset(1, 1, 1))) {
            if (pLevel.getFluidState(blockpos).is(FluidTags.WATER)) {
                return true;
            }
        }
        for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-6, 1, -6), pPos.offset(6, 4, 6))) {
            if (pLevel.getBlockState(blockpos).getBlock() == Blocks.IRON_BLOCK) {
                return true;
            }
        }
        return false;
    }

    private static boolean isUnderCrops(BlockGetter pLevel, BlockPos pPos) {
        BlockState plant = pLevel.getBlockState(pPos.above());
        BlockState state = pLevel.getBlockState(pPos);
        return plant.getBlock() instanceof net.minecraftforge.common.IPlantable && state.canSustainPlant(pLevel, pPos, Direction.UP, (net.minecraftforge.common.IPlantable)plant.getBlock());
    }

    private static int distance(BlockPos pos1, BlockPos pos2) {
        int res = Math.abs(pos1.getX()-pos2.getX());
        res += Math.abs(pos1.getY()-pos2.getY());
        res += Math.abs(pos1.getZ()-pos2.getZ());
        return res;
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return super.isFertile(state, level, pos);
    }
}
