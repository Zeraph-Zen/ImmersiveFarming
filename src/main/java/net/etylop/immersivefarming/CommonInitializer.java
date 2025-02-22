package net.etylop.immersivefarming;

import net.etylop.immersivefarming.config.IFConfig;
import net.etylop.immersivefarming.entity.ai.goal.PullCartGoal;
import net.etylop.immersivefarming.utils.cart.GoalAdder;
import net.etylop.immersivefarming.world.IFWorld;
import net.etylop.immersivefarming.world.SimpleIFWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolderRegistry;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CommonInitializer implements Initializer {
    @Override
    public void init(final Context mod) {
        final ModContainer container = mod.context().getActiveContainer();
        ObjectHolderRegistry.addHandler(new Consumer<>() {
            boolean run = true;

            @Override
            public void accept(final Predicate<ResourceLocation> filter) {
                if (this.run && filter.test(ForgeRegistries.ENTITIES.getRegistryName())) {
                    container.addConfig(new ModConfig(ModConfig.Type.COMMON, IFConfig.spec(), container));
                    this.run = false;
                    LogicalSidedProvider.WORKQUEUE.get(EffectiveSide.get())
                        .execute(() -> ObjectHolderRegistry.removeHandler(this));
                }
            }
        });
        mod.bus().<AttachCapabilitiesEvent<Level>, Level>addGenericListener(Level.class, e ->
            e.addCapability(new ResourceLocation(ImmersiveFarming.MOD_ID, "carts"), IFWorld.createProvider(SimpleIFWorld::new))
        );
        GoalAdder.mobGoal(Mob.class)
            .add(1, PullCartGoal::new)
            .build()
            .register(mod.bus());
        mod.bus().<TickEvent.WorldTickEvent>addListener(e -> {
            if (e.phase == TickEvent.Phase.END) {
                IFWorld.get(e.world).ifPresent(IFWorld::tick);
            }
        });
    }
}
