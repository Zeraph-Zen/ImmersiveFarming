package net.etylop.immersivefarming.event;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.block.IFBlockEntities;
import net.etylop.immersivefarming.block.entity.SprinklerRenderer;
import net.etylop.immersivefarming.block.utils.IFDynamicModel;
import net.etylop.immersivefarming.particle.IFParticles;
import net.etylop.immersivefarming.particle.custom.ContaminationParticles;
import net.etylop.immersivefarming.particle.custom.SprinklerParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = ImmersiveFarming.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class IFEventBusEvents {
    @SubscribeEvent
    public static void registerParticleFactories(final ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(IFParticles.SPRINKLER_PARTICLES.get(),
                SprinklerParticles.Provider::new);
        Minecraft.getInstance().particleEngine.register(IFParticles.CONTAMINATION_PARTICLES.get(),
                ContaminationParticles.Provider::new);
    }

    @SubscribeEvent
    public static void registerRenders(EntityRenderersEvent.RegisterRenderers event)
    {
        registerBERenders(event);
    }


    public static void registerBERenders(EntityRenderersEvent.RegisterRenderers event)
    {
        registerBERenderNoContext(event, IFBlockEntities.SPRINKLER.master(), SprinklerRenderer::new);
    }

    private static <T extends BlockEntity>
    void registerBERenderNoContext(
            EntityRenderersEvent.RegisterRenderers event, BlockEntityType<? extends T> type, Supplier<BlockEntityRenderer<T>> render
    )
    {
        event.registerBlockEntityRenderer(type, $ -> render.get());
    }

    @SubscribeEvent
    public static void registerModelLoaders(ModelRegistryEvent ev)
    {
        SprinklerRenderer.SPRINKLER_TOP = new IFDynamicModel(SprinklerRenderer.NAME_SPRINKLER);
    }
}