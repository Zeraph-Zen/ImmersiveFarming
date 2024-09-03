package net.etylop.immersivefarming.gui;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.gui.IEBaseContainer;
import blusunrize.immersiveengineering.common.register.IEContainerTypes;
import com.google.common.base.Preconditions;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

//TODO rewrite this
public interface IFMenuProvider<T extends BlockEntity & IFMenuProvider<T>> extends IEBlockInterfaces.IInteractionObjectIE<T>{


    default IEContainerTypes.BEContainer<? super T, ?> getContainerType(){
        return null;
    }

    @Nonnull
    BEContainerIF<? super T, ?> getContainerTypeIF();

    @Nonnull
    @Override
    default AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity){
        T master = getGuiMaster();
        Preconditions.checkNotNull(master);
        BEContainerIF<? super T, ?> type = getContainerTypeIF();
        return type.create(id, playerInventory, master);
    }

    record BEContainerIF<T extends BlockEntity, C extends IEBaseContainer<? super T>> (RegistryObject<MenuType<C>> type, IEContainerTypes.BEContainerConstructor<T, C> factory){
        public C create(int windowId, Inventory playerInv, T tile){
            return factory.construct(getType(), windowId, playerInv, tile);
        }

        public MenuType<C> getType(){
            return type.get();
        }
    }
}