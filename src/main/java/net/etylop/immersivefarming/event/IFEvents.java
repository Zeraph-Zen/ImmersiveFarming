package net.etylop.immersivefarming.event;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.block.IFBlocks;
import net.etylop.immersivefarming.block.custom.Soil;
import net.etylop.immersivefarming.utils.CropSavedData;
import net.etylop.immersivefarming.utils.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.Optional;


public class IFEvents {
    @Mod.EventBusSubscriber(modid = ImmersiveFarming.MOD_ID)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            Block target = event.getWorld().getBlockState(event.getPos()).getBlock();
            if (Registry.BLOCK.getHolderOrThrow(Registry.BLOCK.getResourceKey(target).get()).is(ModTags.Blocks.TILLABLE_BLOCK) &&
                !event.getWorld().isClientSide() &&
                event.getPlayer().getMainHandItem().getItem() instanceof HoeItem) {

                event.getWorld().setBlock(event.getPos(), IFBlocks.SOIL.get().defaultBlockState(), 3);
                event.getPlayer().getMainHandItem().hurtAndBreak(1, event.getPlayer(), (val) -> val.broadcastBreakEvent(event.getPlayer().getUsedItemHand()));            }
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


        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onCropGrowth(BlockEvent.CropGrowEvent.Pre event) {
            if (event.getResult() == Event.Result.DENY) return;

            LevelAccessor level = event.getWorld();
            BlockState cropBlock = level.getBlockState(event.getPos());

            if (level.getBiome(event.getPos()).containsTag(Tags.Biomes.IS_COLD)) {
                event.setResult(Event.Result.DENY);
                return;
            }

            if (!(cropBlock.getBlock() instanceof CropBlock || cropBlock.getBlock() instanceof StemBlock)) {
                return;
            }

            if (canCropGrow((Level) event.getWorld(), event.getPos())) {
                BlockState soilBlock = event.getWorld().getBlockState(event.getPos().below());
                int fertilization = soilBlock.getValue(Soil.FERTILITY);

                if (fertilization >= 1) {
                    event.setResult(Event.Result.DEFAULT);
                } else {
                    CropSavedData cropData = getCropSavedData((Level) event.getWorld());
                    if (Math.random() < 0.5 && cropData != null && cropData.testCrop((Level) level, event.getPos())) {
                        event.setResult(Event.Result.DEFAULT);
                    } else if (Math.random() < 0.25) {
                        event.setResult(Event.Result.DEFAULT);
                    } else {
                        event.setResult(Event.Result.DENY);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onCropGrowthPost(BlockEvent.CropGrowEvent.Post event) {
            BlockState cropBlock = event.getState();
            LevelAccessor level = event.getWorld();
            BlockState soilBlock = event.getWorld().getBlockState(event.getPos().below());

            if (!(cropBlock.getBlock() instanceof CropBlock || cropBlock.getBlock() instanceof StemBlock)) {
                return;
            }

            if (!(soilBlock.getBlock() instanceof Soil)) {
                return;
            }

            Optional<Integer> ageOptional = cropBlock.getValues().entrySet().stream()
                    .filter(entry -> "age".equals(entry.getKey().getName()) && entry.getValue() instanceof Integer)
                    .map(entry -> (Integer) entry.getValue())
                    .findFirst();

            Optional<Integer> maxAgeOptional = cropBlock.getValues().entrySet().stream()
                    .filter(entry -> "age".equals(entry.getKey().getName()) && entry.getKey() instanceof IntegerProperty)
                    .map(entry -> Collections.max(((IntegerProperty) entry.getKey()).getPossibleValues()))
                    .findFirst();

            int age = ageOptional.orElse(0);
            int maxAge= maxAgeOptional.orElse(10);

            if (age == maxAge) {
                CropSavedData cropData = getCropSavedData((Level) event.getWorld());
                if (cropData != null) {
                    cropData.insertCrop((Level) level, event.getPos());
                    cropData.setDirty();
                }
                level.setBlock(event.getPos().below(), soilBlock.setValue(Soil.FERTILITY, 0), 2);
                event.setResult(Event.Result.ALLOW);
            }
        }


            private static boolean canCropGrow(Level level, BlockPos pos) {
            BlockState soil = level.getBlockState(pos.below());
            return (soil.getBlock() instanceof Soil) && soil.getBlock().isFertile(soil, level, pos) && level.canSeeSky(pos);
        }

        public static CropSavedData getCropSavedData(Level w)
        {
            if (w.isClientSide() || !(w instanceof ServerLevel world))
                return null;

            return world.getChunkSource().getDataStorage().computeIfAbsent(CropSavedData::load, CropSavedData::create, CropSavedData.DATA_IDENTIFIER);
        }

    }
}
