package net.etylop.immersivefarming.client;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IFSounds {

    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ImmersiveFarming.MOD_ID);

    public static final RegistryObject<SoundEvent> CART_ATTACHED = REGISTER.register("entity.cart.attach",
            () -> new SoundEvent(new ResourceLocation(ImmersiveFarming.MOD_ID, "entity.cart.attach")));
    public static final RegistryObject<SoundEvent> CART_DETACHED = REGISTER.register("entity.cart.detach",
            () -> new SoundEvent(new ResourceLocation(ImmersiveFarming.MOD_ID, "entity.cart.detach")));
    public static final RegistryObject<SoundEvent> CART_PLACED = REGISTER.register("entity.cart.place",
            () -> new SoundEvent(new ResourceLocation(ImmersiveFarming.MOD_ID, "entity.cart.place")));

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }
}
