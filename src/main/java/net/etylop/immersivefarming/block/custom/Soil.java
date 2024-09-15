package net.etylop.immersivefarming.block.custom;

import net.etylop.immersivefarming.block.entity.SprinklerBlockEntity;
import net.etylop.immersivefarming.particle.IFParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;

import java.util.Random;
import java.util.Set;

import static net.etylop.immersivefarming.utils.IFFunctions.getHoeSpeed;

public class Soil extends FarmBlock {
    public static final int TILL_MAX = 7;
    public static final IntegerProperty TILL = IntegerProperty.create("till",0,TILL_MAX);
    public static final int FERTILITY_MAX = 2;
    public static final IntegerProperty FERTILITY = IntegerProperty.create("fertility", 0, FERTILITY_MAX);

    // Probability for a crop to become sick during a random tick
    public static final float START_CONTAMINATION = 0.0001f;
    // Probability for a crop to contaminate an adjacent crop during a random tick
    public static final float PROXIMITY_CONTAMINATION = 0.1f;
    // Probability for a crop to die when contaminated during a random tick
    public static final float LETHALITY_CONTAMINATION = 0.03f;
    public static final BooleanProperty CONTAMINATED = BooleanProperty.create("contaminated");


    public Soil(Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(TILL, 0)
                .setValue(FERTILITY, 0)
                .setValue(CONTAMINATED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(TILL);
        pBuilder.add(FERTILITY);
        pBuilder.add(CONTAMINATED);
    }


    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable) {
        if (state.hasProperty(Soil.TILL) && state.getValue(Soil.TILL) == Soil.TILL_MAX) {
            BlockState farmlandState = Blocks.FARMLAND.defaultBlockState();
            return farmlandState.canSustainPlant(level, pos, facing, plantable);
        }
        return false;
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
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {

        // Update moisture level
        int i = pState.getValue(MOISTURE);
        int waterNearby = isNearWater(pLevel, pPos);
        if (waterNearby==0 && !pLevel.isRainingAt(pPos.above())) {
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

        // Update contaminated level
        boolean contaminated = pState.getValue(CONTAMINATED);
        boolean isUnderCrops = isUnderCrops(pLevel, pPos);
        if (waterNearby==2) {
            if (contaminated) {
                pLevel.setBlock(pPos, pState.setValue(CONTAMINATED, false), 2);
            }
        }
        else if (isUnderCrops && !contaminated && Math.random()<START_CONTAMINATION) {
            pLevel.setBlock(pPos, pState.setValue(CONTAMINATED, true), 2);
        }
        else if (!isUnderCrops && contaminated) {
            pLevel.setBlock(pPos, pState.setValue(CONTAMINATED, false), 2);
        }
        else if (isUnderCrops && contaminated && Math.random()<LETHALITY_CONTAMINATION) {
            pLevel.setBlock(pPos.above(), Blocks.DEAD_BUSH.defaultBlockState(), 3);
            pLevel.setBlock(pPos, Blocks.DIRT.defaultBlockState(), 2);
        }
        else if (isUnderCrops && contaminated) {
            for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-1, 0, -1), pPos.offset(1, 1, 1))) {
                if (pLevel.getBlockState(blockpos).getBlock() instanceof Soil && isUnderCrops(pLevel, blockpos) && Math.random()<PROXIMITY_CONTAMINATION) {
                    BlockState newSoil = pLevel.getBlockState(blockpos).setValue(CONTAMINATED, true);
                    pLevel.setBlock(blockpos, newSoil, 2);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
        if (pState.getValue(CONTAMINATED)) {
            spawnContaminatedParticles(pLevel, pPos);
        }
    }

    private static void spawnContaminatedParticles(Level level, BlockPos pos) {
        for(int i = 0; i < 10; i++) {
            level.addParticle(IFParticles.CONTAMINATION_PARTICLES.get(),
                    pos.getX() + Math.random(), pos.getY() + 1 + Math.random(), pos.getZ() + Math.random(),
                    0,0,0);
        }
    }

    private static int isNearWater(LevelReader pLevel, BlockPos pPos) {
        /**
         * Checks nearby blocks for sources of water
         * @return 0: no water found, 1: water found, 2: treated water found
         */

        ChunkAccess chunk = pLevel.getChunk(pPos);
        boolean found_sprinkler = false;
        int chunkPosX = chunk.getPos().x;
        int chunkPosZ = chunk.getPos().z;

        for (int i=chunkPosX-1; i<chunkPosX+2; i++) {
            for (int j=chunkPosZ-1; j<chunkPosZ+2; j++) {
                Set<BlockPos> entitiesPos = pLevel.getChunk(i,j).getBlockEntitiesPos();
                for (BlockPos pos : entitiesPos) {
                    if (pLevel.getBlockState(pos).getBlock() instanceof SprinklerBlock && pLevel.getBlockState(pos).getValue(SprinklerBlockEntity.ACTIVE) && distanceWater(pPos, pos)<7) {
                        found_sprinkler = true;
                        if (pLevel.getBlockState(pos).getValue(SprinklerBlockEntity.USING_PESTICIDE)) {
                            return 2;
                        }
                    }
                    else if (pLevel.getBlockState(pos).getBlock() instanceof SprinklerExtendedBlock &&  pLevel.getBlockState(pos).getValue(SprinklerBlockEntity.ACTIVE) && distanceWater(pPos, pos)<13) {
                        found_sprinkler = true;
                        if (pLevel.getBlockState(pos).getValue(SprinklerBlockEntity.USING_PESTICIDE)) {
                            return 2;
                        }
                    }
                }
            }
        }

        if (found_sprinkler)
            return 1;
        for (BlockPos blockpos : BlockPos.betweenClosed(pPos.offset(-1, 0, -1), pPos.offset(1, 1, 1))) {
            if (pLevel.getFluidState(blockpos).is(FluidTags.WATER)) {
                return 1;
            }
        }
        return 0;
    }

    private static boolean isUnderCrops(BlockGetter pLevel, BlockPos pPos) {
        BlockState plant = pLevel.getBlockState(pPos.above());
        BlockState state = pLevel.getBlockState(pPos);
        return plant.getBlock() instanceof net.minecraftforge.common.IPlantable && state.canSustainPlant(pLevel, pPos, Direction.UP, (net.minecraftforge.common.IPlantable)plant.getBlock());
    }

    private static int distanceWater(BlockPos pos1, BlockPos pos2) {
        if (Math.abs(pos1.getY()-pos2.getY())>3) return 1000000;
        int res = Math.abs(pos1.getX()-pos2.getX());
        res = Math.max(res,Math.abs(pos1.getZ()-pos2.getZ()));
        return res;
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(MOISTURE)>0;
    }
}
