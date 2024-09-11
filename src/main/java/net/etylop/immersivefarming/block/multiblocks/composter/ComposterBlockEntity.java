package net.etylop.immersivefarming.block.multiblocks.composter;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.crafting.FluidTagInput;
import blusunrize.immersiveengineering.api.utils.CapabilityReference;
import blusunrize.immersiveengineering.api.utils.DirectionalBlockPos;
import blusunrize.immersiveengineering.api.utils.shapes.CachedShapesWithTransform;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IBlockBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IInteractionObjectIE;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.ISoundBE;
import blusunrize.immersiveengineering.common.blocks.generic.PoweredMultiblockBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcess;
import blusunrize.immersiveengineering.common.blocks.multiblocks.process.MultiblockProcessInMachine;
import blusunrize.immersiveengineering.common.blocks.ticking.IEClientTickableBE;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.MultiblockCapability;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IEInventoryHandler;
import blusunrize.immersiveengineering.common.util.orientation.RelativeBlockFace;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.etylop.immersivefarming.api.crafting.ComposterRecipe;
import net.etylop.immersivefarming.block.IFMultiblocks;
import net.etylop.immersivefarming.gui.IFMenuProvider;
import net.etylop.immersivefarming.gui.IFMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class ComposterBlockEntity extends PoweredMultiblockBlockEntity<ComposterBlockEntity, ComposterRecipe> implements
        IInteractionObjectIE<ComposterBlockEntity>, IBlockBounds, IEClientTickableBE, ISoundBE, IFMenuProvider<ComposterBlockEntity>
{
    public FluidTank[] tanks = new FluidTank[]{
            new FluidTank(8*FluidAttributes.BUCKET_VOLUME), // water tank
            new FluidTank(8*FluidAttributes.BUCKET_VOLUME), // nitrogen tank
            new FluidTank(8*FluidAttributes.BUCKET_VOLUME)  // carbone tank
    };
    public static final int INPUT_SLOT = 0;
    public final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    public ComposterBlockEntity(BlockEntityType<ComposterBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(IFMultiblocks.COMPOSTER, 16000, true, type, pos, state);
    }

    @Override
    public void readCustomNBT(CompoundTag nbt, boolean descPacket)
    {
        super.readCustomNBT(nbt, descPacket);
        tanks[0].readFromNBT(nbt.getCompound("tankWater"));
        tanks[1].readFromNBT(nbt.getCompound("tankNitrogen"));
        tanks[2].readFromNBT(nbt.getCompound("tankCarbon"));
        if(!descPacket)
            ContainerHelper.loadAllItems(nbt, inventory);
    }

    @Override
    public void writeCustomNBT(CompoundTag nbt, boolean descPacket)
    {
        super.writeCustomNBT(nbt, descPacket);
        CompoundTag tankTag = tanks[0].writeToNBT(new CompoundTag());
        nbt.put("tankWater", tankTag);
        tankTag = tanks[1].writeToNBT(new CompoundTag());
        nbt.put("tankNitrogen", tankTag);
        tankTag = tanks[2].writeToNBT(new CompoundTag());
        nbt.put("tankCarbon", tankTag);
        if(!descPacket)
            ContainerHelper.saveAllItems(nbt, inventory);
    }

    @Override
    public void receiveMessageFromClient(CompoundTag message)
    {
        super.receiveMessageFromClient(message);
    }

    @Override
    public boolean canTickAny()
    {
        return super.canTickAny() && !isRSDisabled();
    }

    @Override
    public void tickClient()
    {
        if(shouldRenderAsActive())
        {
            ImmersiveEngineering.proxy.handleTileSound(IESounds.mixer, this, shouldRenderAsActive(), 0.075f, 1f);
        }
    }

    @Override
    public void tickServer()
    {
        super.tickServer();
        boolean update = false;

        if(energyStorage.getEnergyStored() > 0&& processQueue.isEmpty())
        {
            ComposterRecipe recipe = ComposterRecipe.findRecipe(level, getTankFluids(), this.inventory.get(0));
            if(recipe!=null)
            {
                MultiblockProcessInMachine<ComposterRecipe> process = new MultiblockProcessComposter(recipe, this::getRecipeForId).setInputTanks(0);
                if(this.addProcessToQueue(process, true))
                {
                    this.addProcessToQueue(process, false);
                    update = true;
                }
            }
        }

        if(update)
        {
            this.setChanged();
            this.markContainingBlockForUpdate(null);
        }
    }


    @Override
    protected ComposterRecipe getRecipeForId(Level level, ResourceLocation id)
    {
        return ComposterRecipe.RECIPES.getById(level, id);
    }


    private FluidStack[] getTankFluids() {
        FluidStack[] fss = new FluidStack[3];
        fss[0] = tanks[0].getFluid();
        fss[1] = tanks[1].getFluid();
        fss[2] = tanks[2].getFluid();
        return fss;
    }

    private static final CachedShapesWithTransform<BlockPos, Pair<Direction, Boolean>> SHAPES =
            CachedShapesWithTransform.createForMultiblock(ComposterBlockEntity::getShape);

    @Override
    public VoxelShape getBlockBounds(@Nullable CollisionContext ctx)
    {
        return getShape(SHAPES);
    }

    private static List<AABB> getShape(BlockPos posInMultiblock)
    {

        if(new BlockPos(2, 0, 2).equals(posInMultiblock))
            return ImmutableList.of(
                    new AABB(0, 0, 0, 1, .5f, 1),
                    new AABB(0.125, .5f, 0.625, 0.25, 1, 0.875),
                    new AABB(0.75, .5f, 0.625, 0.875, 1, 0.875)
            );
        else if (posInMultiblock.getY()==0 && posInMultiblock.getZ()<2) {
            List<AABB> list = new ArrayList<>();
            list.add(new AABB(0, 0, 0, 1, .5f, 1));
            if (new BlockPos(0,0,0).equals(posInMultiblock)) {
                list.add(new AABB(4*0.0625, 0.5, 3*0.0625, 8*0.0625, 1, 7*0.0625));
            }
            else if (new BlockPos(0,0,1).equals(posInMultiblock)) {
                list.add(new AABB(4*0.0625, 0.5, 9*0.0625, 8*0.0625, 1, 13*0.0625));
            }
            else if (new BlockPos(2,0,0).equals(posInMultiblock)) {
                list.add(new AABB(8*0.0625, 0.5, 3*0.0625, 12*0.0625, 1, 7*0.0625));
            }
            else if (new BlockPos(2,0,1).equals(posInMultiblock)) {
                list.add(new AABB(8*0.0625, 0.5, 9*0.0625, 12*0.0625, 1, 13*0.0625));
            }
            return list;
        }
        else if (new BlockPos(2,1,2).equals(posInMultiblock)) {
            return ImmutableList.of(new AABB(0, 0, 0.5, 1, 1, 1));
        }
        else if (new BlockPos(1,1,2).equals(posInMultiblock)) {
            return ImmutableList.of(new AABB(4*0.0625, 0, 4*0.0625, 12*0.0625, 1, 12*0.0625));
        }
        else if (new BlockPos(1,2,2).equals(posInMultiblock)) {
            return ImmutableList.of(new AABB(4*0.0625, 0, 4*0.0625, 12*0.0625, 1, 12*0.0625));
        }
        else if (new BlockPos(1,3,2).equals(posInMultiblock)) {
            return ImmutableList.of(new AABB(4*0.0625, 0, 0, 12*0.0625, 12*0.0625, 12*0.0625));
        }
        else if (new BlockPos(1,3,1).equals(posInMultiblock)) {
            return ImmutableList.of(new AABB(4*0.0625, 0, 4*0.0625, 12*0.0625, 12*0.0625, 15*0.0625));
        }
        else
            return ImmutableList.of(new AABB(0, 0, 0, 1, 1, 1));
    }

    @Override
    public Set<MultiblockFace> getEnergyPos()
    {
        return ImmutableSet.of(new MultiblockFace(0, 1, 2, RelativeBlockFace.UP));
    }

    @Override
    public Set<BlockPos> getRedstonePos()
    {
        return ImmutableSet.of(
                new BlockPos(2, 1, 2)
        );
    }

    @Override
    public boolean isInWorldProcessingMachine()
    {
        return false;
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<ComposterRecipe> process)
    {
        return true;
    }

    private DirectionalBlockPos getOutputPos()
    {
        BlockPos pos = worldPosition.relative(getFacing(), 1).relative(getFacing().getClockWise(), 3);
        return new DirectionalBlockPos(pos, getFacing().getClockWise());
    }

    private CapabilityReference<IItemHandler> outputCap = CapabilityReference.forBlockEntityAt(
            this, this::getOutputPos, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    );

    @Override
    public void doProcessOutput(ItemStack output)
    {
        output = Utils.insertStackIntoInventory(outputCap, output, false);
        if(!output.isEmpty())
            Utils.dropStackAtPos(level, getOutputPos(), output);
    }

    @Override
    public void doProcessFluidOutput(FluidStack output)
    {
    }

    @Override
    public void onProcessFinish(MultiblockProcess<ComposterRecipe> process)
    {
    }

    @Override
    public int getMaxProcessPerTick()
    {
        return 1;
    }

    @Override
    public int getProcessQueueMaxLength()
    {
        return 1;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<ComposterRecipe> process)
    {
        return 0;
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getInventory()
    {
        return inventory;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public int[] getOutputSlots()
    {
        return new int[0];
    }

    @Override
    public int[] getOutputTanks()
    {
        return new int[]{0};
    }

    @Override
    @Nonnull
    public IFluidTank[] getInternalTanks()
    {
        return new IFluidTank[]{tanks[0]};
    }

    @Override
    public void doGraphicalUpdates()
    {
        this.setChanged();
        this.markContainingBlockForUpdate(null);
    }

    private final MultiblockCapability<IItemHandler> insertionHandler = MultiblockCapability.make(
            this, be -> be.insertionHandler, ComposterBlockEntity::master,
            registerCapability(new IEInventoryHandler(8, this, 0, new boolean[]{true, true, true, true, true, true, true, true}, new boolean[8]))
    );
    private final MultiblockCapability<IFluidHandler> fluidInputCap = MultiblockCapability.make(
            this, be -> be.fluidInputCap, ComposterBlockEntity::master, registerFluidInput(tanks[0])
    );
    private final MultiblockCapability<IFluidHandler> fluidOutputCap = MultiblockCapability.make(
            this, be -> be.fluidOutputCap, ComposterBlockEntity::master, registerFluidOutput(tanks[0])
    );


    private static final MultiblockFace FLUID_INPUT = new MultiblockFace(1, 0, 2, RelativeBlockFace.FRONT);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
    {
        if(capability==CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if(facing==null)
                return fluidInputCap.getAndCast();
            MultiblockFace relativeFace = asRelativeFace(facing);
            if(FLUID_INPUT.equals(relativeFace))
                return fluidInputCap.getAndCast();
        }
        if((facing==null||new BlockPos(1, 1, 0).equals(posInMultiblock))&&capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return insertionHandler.getAndCast();
        return super.getCapability(capability, facing);
    }

    @Override
    public ComposterRecipe findRecipeForInsertion(ItemStack inserting)
    {
        return null;
    }


    @Nullable
    @Override
    protected MultiblockProcess<ComposterRecipe> loadProcessFromNBT(CompoundTag tag)
    {
        ResourceLocation id = new ResourceLocation(tag.getString("recipe"));
        return new MultiblockProcessComposter(id, this::getRecipeForId);
    }

    public static class MultiblockProcessComposter extends MultiblockProcessInMachine<ComposterRecipe>
    {
        public MultiblockProcessComposter(ComposterRecipe recipe, BiFunction<Level, ResourceLocation, ComposterRecipe> getRecipe)
        {
            super(recipe, getRecipe, 0);
        }

        public MultiblockProcessComposter(ResourceLocation recipeId, BiFunction<Level, ResourceLocation, ComposterRecipe> getRecipe)
        {
            super(recipeId, getRecipe, 0);
        }

        @Override
        protected List<FluidStack> getRecipeFluidOutputs(PoweredMultiblockBlockEntity<?, ComposterRecipe> multiblock)
        {
            return Collections.emptyList();
        }

        @Override
        protected List<FluidTagInput> getRecipeFluidInputs(PoweredMultiblockBlockEntity<?, ComposterRecipe> multiblock)
        {
            return Collections.emptyList();
        }

        @Override
        public boolean canProcess(PoweredMultiblockBlockEntity<?, ComposterRecipe> multiblock)
        {
            LevelDependentData<ComposterRecipe> levelData = getLevelData(multiblock.getLevel());
            if (levelData.recipe() == null)
                return true;
            if(!(multiblock instanceof ComposterBlockEntity composter))
                return false;
            // we don't need to check filling since after draining 1 mB of input fluid there will be space for 1 mB of output fluid
            return composter.energyStorage.extractEnergy(levelData.energyPerTick(), true)==levelData.energyPerTick()&&
                    !composter.tanks[0].drain(1, FluidAction.SIMULATE).isEmpty(); // TODO remove
        }

        @Override
        public void doProcessTick(PoweredMultiblockBlockEntity<?, ComposterRecipe> multiblock)
        {
            LevelDependentData<ComposterRecipe> levelData = getLevelData(multiblock.getLevel());
            if (levelData.recipe() == null)
            {
                this.clearProcess = true;
                return;
            }
            ComposterBlockEntity composter = (ComposterBlockEntity)multiblock;
            ComposterRecipe recipe = levelData.recipe();
            if(this.processTick == recipe.getTotalProcessTime()-1)
            {
                if (recipe.fluidProduct) {
                    FluidStack[] fluidOuputs = recipe.getFluidOutput();
                    if (fluidOuputs[0] != null)
                        composter.tanks[1].fill(fluidOuputs[0], FluidAction.EXECUTE);
                    if (fluidOuputs[1] != null)
                        composter.tanks[2].fill(fluidOuputs[1], FluidAction.EXECUTE);
                }
                else {
                    FluidTagInput[] fluidInputs = recipe.getFluidInput();
                    for (int i=0; i<3; i++) {
                        composter.tanks[i].drain(fluidInputs[i].getAmount(), FluidAction.EXECUTE);
                    }
                    ItemStack outputItems = recipe.itemOutput.getMatchingStacks()[0];
                    composter.doProcessOutput(outputItems);

                }
            }
            super.doProcessTick(multiblock);
        }
    }

    @Override
    public boolean canUseGui(Player player)
    {
        return formed;
    }

    @Override
    public ComposterBlockEntity getGuiMaster()
    {
        return master();
    }

    @Override
    public BEContainerIF<ComposterBlockEntity, ?> getContainerTypeIF()
    {
        return IFMenuTypes.COMPOSTER;
    }

    @Override
    public boolean shouldPlaySound(String sound)
    {
        return shouldRenderAsActive();
    }
}
