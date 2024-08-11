package net.etylop.immersivefarming.event;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.FarmlandTrampleEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModEvents {
    @Mod.EventBusSubscriber(modid = ImmersiveFarming.MOD_ID)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onBonemeal(BonemealEvent event) {
            if (event.getBlock().getBlock() instanceof GrassBlock) {
                return;
            }
            event.setCanceled(true);

        }

        @SubscribeEvent
        public static void onTrampleFarmland(FarmlandTrampleEvent event) {
            event.setCanceled(true);
        }



        @SubscribeEvent
        public static void onCropGrowth(BlockEvent.CropGrowEvent.Pre event) {
            if (isSunVisible(event.getWorld(), event.getPos())) {
                event.setResult(Event.Result.DEFAULT);
            }
            else {
                event.setResult(Event.Result.DENY);
            }
        }

        private static boolean isSunVisible(LevelAccessor world, BlockPos pos)
        {
            return (world.dayTime()%24000 <= 12000) && (world.canSeeSky(pos));
        }
    }
}
