package net.etylop.immersivefarming.common.data;

import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.common.data.generators.IFBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ImmersiveFarming.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IFDataProvider {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper exHelper = event.getExistingFileHelper();
        StaticTemplateManager.EXISTING_HELPER = exHelper;

        ImmersiveFarming.getNewLogger().info("-============ Immersive Farming Data Generation ============-");

        if(event.includeClient()){
            generator.addProvider(new IFBlockStateProvider(generator, exHelper));
        }
    }
}

