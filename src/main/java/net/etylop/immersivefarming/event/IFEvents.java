package net.etylop.immersivefarming.event;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.block.IFBlocks;
import net.etylop.immersivefarming.block.custom.Soil;
import net.etylop.immersivefarming.utils.CropSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.etylop.immersivefarming.utils.IFFunctions.isBlockTillable;


public class IFEvents {
    @Mod.EventBusSubscriber(modid = ImmersiveFarming.MOD_ID)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onHoeUse(UseHoeEvent event) {
            UseOnContext context = event.getContext();
            Level level = context.getLevel();
            if (isBlockTillable(level, context.getClickedPos())) {
                level.setBlock(context.getClickedPos(), IFBlocks.SOIL.get().defaultBlockState(), 3);
                event.getPlayer().getMainHandItem().hurtAndBreak(1, event.getPlayer(), (val) -> {
                    val.broadcastBreakEvent(event.getPlayer().getUsedItemHand());
                });
            }
        }

        @SubscribeEvent
        public static void onBonemeal(BonemealEvent event) {
            if (event.getBlock().getBlock() instanceof GrassBlock) {
                return;
            }
            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onTrampleFarmland(BlockEvent.FarmlandTrampleEvent event) {
            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onCropGrowth(BlockEvent.CropGrowEvent.Pre event) {
            LevelAccessor level = event.getWorld();
            BlockState cropBlock =  level.getBlockState(event.getPos());

            if (level.getBiome(event.getPos()).containsTag(Tags.Biomes.IS_COLD)) {
                event.setResult(Event.Result.DENY);
                if (Math.random()<.3) {
                    level.setBlock(event.getPos(),IFBlocks.DEAD_CROP.get().defaultBlockState(), 2);
                }
                return;
            }

            if (canCropGrow((Level) event.getWorld(), event.getPos())) {
                BlockState soilBlock =  event.getWorld().getBlockState(event.getPos().below());
                int fertilization = soilBlock.getValue(Soil.FERTILITY);

                if (fertilization>=1) {
                    event.setResult(Event.Result.DEFAULT);
                }
                else {
                    CropSavedData cropData = getCropSavedData((Level) event.getWorld());
                    if (Math.random()<0.5 && cropData != null && cropData.testCrop((Level)level, event.getPos())) {
                        event.setResult(Event.Result.DEFAULT);
                    }
                    else if (Math.random()<0.25) {
                        event.setResult(Event.Result.DEFAULT);
                    }
                    else {
                        event.setResult(Event.Result.DENY);
                    }
                }
                if (cropBlock.getValue(CropBlock.AGE)==CropBlock.MAX_AGE-1 && !(event.getResult()==Event.Result.DENY)) {
                    level.setBlock(event.getPos().below(), soilBlock.setValue(Soil.FERTILITY, 0), 2);
                    CropSavedData cropData = getCropSavedData((Level) event.getWorld());
                    if (cropData!=null) {
                        cropData.insertCrop((Level) level, event.getPos());
                        cropData.setDirty();
                    }
                    event.setResult(Event.Result.ALLOW);
                }
                return;
            }
            event.setResult(Event.Result.DENY);
        }

        private static boolean canCropGrow(Level level, BlockPos pos) {
            BlockState soil = level.getBlockState(pos.below());
            return (soil.getBlock() instanceof Soil) && soil.getBlock().isFertile(soil, level, pos) && level.canSeeSky(pos);
        }


        @SubscribeEvent
        public static void onHarvestCrop(BlockEvent.BreakEvent event) {
            BlockState block = event.getState();
            if (!(block.getBlock() instanceof CropBlock))
                return;

            BlockState soil = event.getWorld().getBlockState(event.getPos().below());
            if (!(soil.getBlock() instanceof Soil))
                return;

            soil.setValue(Soil.FERTILITY,0);
        }

        public static CropSavedData getCropSavedData(Level w)
        {
            if (w.isClientSide() || !(w instanceof ServerLevel world))
                return null;

            return world.getChunkSource().getDataStorage().computeIfAbsent(CropSavedData::load, CropSavedData::create, CropSavedData.DATA_IDENTIFIER);
        }

    }
}
