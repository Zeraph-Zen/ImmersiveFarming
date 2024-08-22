package net.etylop.immersivefarming.particle;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegisterParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ImmersiveFarming.MOD_ID);

    public static final RegistryObject<SimpleParticleType> SPRINKLER_PARTICLES =
            PARTICLE_TYPES.register("sprinkler_particles", () -> new SimpleParticleType(true));

    public static final RegistryObject<SimpleParticleType> CONTAMINATION_PARTICLES =
            PARTICLE_TYPES.register("contamination_particles", () -> new SimpleParticleType(true));
    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
