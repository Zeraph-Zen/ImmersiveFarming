package net.etylop.immersivefarming.block;

import blusunrize.immersiveengineering.api.IEEnums;
import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.fluid.IFluidPipe;
import blusunrize.immersiveengineering.api.utils.DirectionUtils;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlock;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlockEntity;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IHasDummyBlocks;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IConfigurableSides;
import blusunrize.immersiveengineering.common.blocks.metal.FluidPumpBlockEntity;
import blusunrize.immersiveengineering.common.blocks.ticking.IEClientTickableBE;
import blusunrize.immersiveengineering.common.blocks.ticking.IEServerTickableBE;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.util.ChatUtils;
import blusunrize.immersiveengineering.common.util.ResettableCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SprinklerBlockEntity extends IEBaseBlockEntity implements IEServerTickableBE, IFluidPipe,
        IEClientTickableBE, IBlockBounds, IHasDummyBlocks, IConfigurableSides {

    public FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME);
    public Map<Direction, IEEnums.IOSideConfig> sideConfig = new EnumMap<>(Direction.class);
    {
        for(Direction d : DirectionUtils.VALUES)
        {
            if(d==Direction.DOWN)
                sideConfig.put(d, IEEnums.IOSideConfig.INPUT);
            else
                sideConfig.put(d, IEEnums.IOSideConfig.NONE);
        }
    }

    private final Map<Direction, ResettableCapability<IFluidHandler>> sidedFluidHandler = new EnumMap<>(Direction.class);

    private boolean checkingArea = false;
    private final List<BlockPos> openList = new ArrayList<>();
    private final List<BlockPos> closedList = new ArrayList<>();
    private final List<BlockPos> checked = new ArrayList<>();


    public SprinklerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket) {
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket) {
    }

    public void prepareAreaCheck()
    {
        openList.clear();
        closedList.clear();
        checked.clear();
        for(Direction f : Direction.values())
            if(sideConfig.get(f)== IEEnums.IOSideConfig.INPUT)
            {
                openList.add(getBlockPos().relative(f));
                checkingArea = true;
            }
    }

    @Override
    public void tickServer() {
        if(tank.getFluidAmount() < tank.getCapacity())
        {
            // int i = inputFluid(tank.getFluid(), IFluidHandler.FluidAction.EXECUTE);
            //TODO
            //tank.fill(, IFluidHandler.FluidAction.EXECUTE);
        }

        boolean hasRSSignal = isRSPowered();

        if(hasRSSignal && this.tank.getFluidAmount() > 0)
        {
            // this.tank.drain(this.tank.getFluid().setAmount(1), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Nullable
    @Override
    public VoxelShape getBlockBounds(@javax.annotation.Nullable CollisionContext ctx)
    {
        if(!isDummy())
            return Shapes.block();
        return Shapes.box(.1875f, 0, .1875f, .8125f, 1, .8125f);
    }

    public void setDummy(boolean dummy)
    {
        BlockState old = getBlockState();
        BlockState newState = old.setValue(IEProperties.MULTIBLOCKSLAVE, dummy);
        setState(newState);
    }

    @Override
    public void placeDummies(BlockPlaceContext ctx, BlockState state)
    {
        BlockPos dummyPos = worldPosition.above();
        getLevelNonnull().setBlockAndUpdate(dummyPos, IEBaseBlock.applyLocationalWaterlogging(
                state.setValue(IEProperties.MULTIBLOCKSLAVE, true), getLevelNonnull(), dummyPos
        ));
    }

    @Override
    public void breakDummies(BlockPos pos, BlockState state)
    {
        for(int i = 0; i <= 1; i++)
            if(Utils.isBlockAt(level, getBlockPos().offset(0, isDummy()?-1: 0, 0).offset(0, i, 0), ModBlocks.SPRINKLER.get()))
                level.removeBlock(getBlockPos().offset(0, isDummy()?-1: 0, 0).offset(0, i, 0), false);
    }

    @Override
    public boolean isDummy()
    {
        return getBlockState().getValue(IEProperties.MULTIBLOCKSLAVE);
    }


    @Nullable
    @Override
    public SprinklerBlockEntity master()
    {
        if(!isDummy())
            return this;
        BlockPos masterPos = getBlockPos().below();
        BlockEntity te = Utils.getExistingTileEntity(level, masterPos);
        return te instanceof SprinklerBlockEntity sprinkler ? sprinkler : null;
    }

    static class SidedFluidHandler implements IFluidHandler {
        SprinklerBlockEntity sprinkler;
        Direction facing;

        SidedFluidHandler(SprinklerBlockEntity sprinkler, Direction facing)
        {
            this.sprinkler = sprinkler;
            this.facing = facing;
        }

        @Override
        public int getTanks()
        {
            return sprinkler.tank.getTanks();
        }

        @Nonnull
        @Override
        public FluidStack getFluidInTank(int tank)
        {
            return sprinkler.tank.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank)
        {
            return sprinkler.tank.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack stack)
        {
            if(sprinkler.sideConfig.get(facing)!= IEEnums.IOSideConfig.INPUT)
                return false;
            return sprinkler.tank.isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            if(resource.isEmpty()||sprinkler.sideConfig.get(facing)!= IEEnums.IOSideConfig.INPUT)
                return 0;
            return sprinkler.tank.fill(resource, action);
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action)
        {
            return this.drain(resource.getAmount(), action);
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            if(sprinkler.sideConfig.get(facing)!= IEEnums.IOSideConfig.OUTPUT)
                return FluidStack.EMPTY;
            return sprinkler.tank.drain(maxDrain, action);
        }
    }

    @Override
    public boolean canOutputPressurized(boolean consumePower)
    {
        if (this.tank.getFluidAmount() > this.tank.getCapacity()/2) {
            return true;
        }
        return false;
    }

    @Override
    public void tickClient()
    {
        // TODO add sound
        //ImmersiveEngineering.proxy.handleTileSound(IESounds.refinery, this, shouldRenderAsActive(), .25f, 1);
    }

    @Override
    public IEEnums.IOSideConfig getSideConfig(Direction side)
    {
        return sideConfig.get(side);
    }

    @Override
    public boolean toggleSide(Direction side, Player p)
    {
        if(side!=Direction.UP&&!isDummy())
        {
            sideConfig.put(side, IEEnums.IOSideConfig.next(sideConfig.get(side)));
            this.setChanged();
            this.markContainingBlockForUpdate(null);
            getLevelNonnull().blockEvent(getBlockPos(), this.getBlockState().getBlock(), 0, 0);
            return true;
        }
        return false;
    }
}
