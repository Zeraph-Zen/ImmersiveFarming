package net.etylop.immersivefarming.event;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.block.IFBlocks;
import net.etylop.immersivefarming.block.custom.Soil;
import net.etylop.immersivefarming.utils.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IFEvents {
    @Mod.EventBusSubscriber(modid = ImmersiveFarming.MOD_ID)
    public static class ForgeEvents {
        private static final Map<ChunkPos, List<BlockPos>> chunkCrops = new HashMap<>();
        private static final Map<BlockPos, Long> cropDate = new HashMap<>();

        @SubscribeEvent
        public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
            Block target = event.getWorld().getBlockState(event.getPos()).getBlock();
            if (Registry.BLOCK.getHolderOrThrow(Registry.BLOCK.getResourceKey(target).get()).is(ModTags.Blocks.TILLABLE_BLOCK) &&
                !event.getWorld().isClientSide() &&
                event.getPlayer().getMainHandItem().getItem() instanceof HoeItem) {

                event.getWorld().setBlock(event.getPos(), IFBlocks.SOIL.get().defaultBlockState(), 3);
                event.getPlayer().getMainHandItem().hurtAndBreak(1, event.getPlayer(), (val) -> {
                    val.broadcastBreakEvent(event.getPlayer().getUsedItemHand());
                });            }
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
            if (canCropGrow((Level) event.getWorld(), event.getPos()))
                event.setResult(Event.Result.DEFAULT);
            else
                event.setResult(Event.Result.DENY);
        }

        private static boolean canCropGrow(Level level, BlockPos pos) {
            BlockState soil = level.getBlockState(pos.below());
            if ((soil.getBlock() instanceof Soil) && soil.getBlock().isFertile(soil, level, pos) && level.canSeeSky(pos))
                return true;
            else
                return false;
        }

        @SubscribeEvent
        public static void onPlaceCrop(BlockEvent.EntityPlaceEvent event) {
            if (event.getWorld().getBlockState(event.getPos()).getBlock() instanceof CropBlock &&
            !event.getWorld().isClientSide()) {
                cropDate.put(event.getPos(), event.getWorld().dayTime());
                ChunkPos chunkPos = event.getWorld().getChunk(event.getPos()).getPos();
                if (chunkCrops.get(chunkPos) == null) {
                    chunkCrops.put(chunkPos, new ArrayList());
                }
                chunkCrops.get(chunkPos).add(event.getPos());
            }
        }
    }
}
