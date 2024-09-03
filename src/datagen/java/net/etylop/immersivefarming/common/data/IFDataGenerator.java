package net.etylop.immersivefarming.common.data;


import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.common.data.generators.IFBlockStates;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(
        modid = ImmersiveFarming.MOD_ID,
        bus = Bus.MOD
)
public class IFDataGenerator {
    public IFDataGenerator() {
    }

    @SubscribeEvent
    public static void generate(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper exhelper = event.getExistingFileHelper();
        StaticTemplateManager.EXISTING_HELPER = exhelper;

        if(event.includeServer()){
        }

        if(event.includeClient()){
            generator.addProvider(new IFBlockStates(generator, exhelper));
        }
    }
}

