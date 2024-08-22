package net.etylop.immersivefarming;

import com.mojang.logging.LogUtils;
import net.etylop.immersivefarming.block.ModBlockEntities;
import net.etylop.immersivefarming.block.ModBlocks;
import net.etylop.immersivefarming.fluid.IFFluids;
import net.etylop.immersivefarming.item.ModItems;
import net.etylop.immersivefarming.particle.RegisterParticles;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ImmersiveFarming.MOD_ID)
public class ImmersiveFarming {
    public static final String MOD_ID = "immersivefarming";


    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public ImmersiveFarming() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModBlockEntities.register(eventBus);
        RegisterParticles.register(eventBus);
        IFFluids.register(eventBus);

        // Register the setup method for modloading
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(IFFluids.TREATED_WATER_BLOCK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(IFFluids.TREATED_WATER_FLUID.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(IFFluids.TREATED_WATER_FLOWING.get(), RenderType.translucent());
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
    }
}
