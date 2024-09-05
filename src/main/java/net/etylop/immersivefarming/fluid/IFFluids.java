package net.etylop.immersivefarming.fluid;

import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.block.IFBlocks;
import net.etylop.immersivefarming.item.IFItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IFFluids {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY_RL = new ResourceLocation("block/water_overlay");

    public static final DeferredRegister<Fluid> FLUIDS
            = DeferredRegister.create(ForgeRegistries.FLUIDS, ImmersiveFarming.MOD_ID);


    public static final RegistryObject<FlowingFluid> TREATED_WATER_FLUID
            = FLUIDS.register("treated_water_fluid", () -> new ForgeFlowingFluid.Source(IFFluids.TREATED_WATER_PROPERTIES));

    public static final RegistryObject<FlowingFluid> TREATED_WATER_FLOWING
            = FLUIDS.register("treated_water_flowing", () -> new ForgeFlowingFluid.Flowing(IFFluids.TREATED_WATER_PROPERTIES));

    public static final RegistryObject<FlowingFluid> DRY_MATTER_FLUID
            = FLUIDS.register("dry_matter_fluid", () -> new ForgeFlowingFluid.Source(IFFluids.TREATED_WATER_PROPERTIES));

    public static final RegistryObject<FlowingFluid> WET_MATTER_FLUID
            = FLUIDS.register("wet_matter_fluid", () -> new ForgeFlowingFluid.Source(IFFluids.TREATED_WATER_PROPERTIES));


    public static final ForgeFlowingFluid.Properties TREATED_WATER_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> TREATED_WATER_FLUID.get(), () -> TREATED_WATER_FLOWING.get(), FluidAttributes.builder(WATER_STILL_RL, WATER_FLOWING_RL)
            .overlay(WATER_OVERLAY_RL)
            .color(0xbf0f0fff))
            .block(() -> IFFluids.TREATED_WATER_BLOCK.get()).bucket(() -> IFItems.TREATED_WATER_BUCKET.get());

    public static final RegistryObject<LiquidBlock> TREATED_WATER_BLOCK = IFBlocks.BLOCKS.register("treated_water",
            () -> new LiquidBlock(() -> IFFluids.TREATED_WATER_FLUID.get(), BlockBehaviour.Properties.of(Material.WATER)
                    .noCollission().strength(100f).noDrops()));

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
