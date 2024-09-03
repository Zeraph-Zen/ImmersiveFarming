package net.etylop.immersivefarming.gui;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.register.IEContainerTypes;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.block.multiblocks.composter.ComposterBlockEntity;
import net.etylop.immersivefarming.gui.IFMenuProvider.BEContainerIF;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

public class IFMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTER =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, ImmersiveFarming.MOD_ID);

    public static final BEContainerIF<ComposterBlockEntity, ComposterContainer> COMPOSTER = makeMenu("composter", ComposterContainer::new);


    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return REGISTER.register(name, () -> IForgeMenuType.create(factory));
    }

    public static <T extends BlockEntity, C extends IEBaseContainer<? super T>>
    BEContainerIF<T, C> makeMenu(String name, IEContainerTypes.BEContainerConstructor<T, C> container)
    {
        RegistryObject<MenuType<C>> typeRef = REGISTER.register(
                name, () -> {
                    Mutable<MenuType<C>> typeBox = new MutableObject<>();
                    MenuType<C> type = new MenuType<>((IContainerFactory<C>)(windowId, inv, data) -> {
                        Level world = ImmersiveEngineering.proxy.getClientWorld();
                        BlockPos pos = data.readBlockPos();
                        BlockEntity te = world.getBlockEntity(pos);
                        return container.construct(typeBox.getValue(), windowId, inv, (T)te);
                    });
                    typeBox.setValue(type);
                    return type;
                }
        );
        return new BEContainerIF<>(typeRef, container);
    }
}
