package net.etylop.immersivefarming.block.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.ClientMultiblocks;
import blusunrize.immersiveengineering.client.utils.BasicClientProperties;
import blusunrize.immersiveengineering.common.blocks.generic.MultiblockPartBlockEntity;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IETemplateMultiblock;
import blusunrize.immersiveengineering.common.register.IEBlocks;
import blusunrize.immersiveengineering.common.util.IELogger;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class IFTemplateMultiblock extends IETemplateMultiblock
{
    private final RegistryObject<? extends Block> baseState;

    public IFTemplateMultiblock(
            ResourceLocation loc, BlockPos masterFromOrigin, BlockPos triggerFromOrigin, BlockPos size,
            RegistryObject<? extends Block> baseState
    )
    {
        super(loc, masterFromOrigin, triggerFromOrigin, size, new IEBlocks.BlockEntry<>(Blocks.AIR));
        this.baseState = baseState;
    }

    @Override
    protected void replaceStructureBlock(StructureTemplate.StructureBlockInfo info, Level world, BlockPos actualPos, boolean mirrored, Direction clickDirection, Vec3i offsetFromMaster)
    {
        BlockState state = baseState.get().defaultBlockState();
        if(!offsetFromMaster.equals(Vec3i.ZERO))
            state = state.setValue(IEProperties.MULTIBLOCKSLAVE, true);
        world.setBlockAndUpdate(actualPos, state);
        BlockEntity curr = world.getBlockEntity(actualPos);
        if(curr instanceof MultiblockPartBlockEntity<?> tile)
        {
            tile.formed = true;
            tile.offsetToMaster = new BlockPos(offsetFromMaster);
            tile.posInMultiblock = info.pos;
            if(state.hasProperty(IEProperties.MIRRORED))
                tile.setMirrored(mirrored);
            tile.setFacing(transformDirection(clickDirection.getOpposite()));
            tile.setChanged();
            world.blockEvent(actualPos, world.getBlockState(actualPos).getBlock(), 255, 0);
        }
        else
            IELogger.logger.error("Expected MB TE at {} during placement", actualPos);
    }

    public Direction transformDirection(Direction original)
    {
        return original;
    }

    public Direction untransformDirection(Direction transformed)
    {
        return transformed;
    }

    public BlockPos multiblockToModelPos(BlockPos posInMultiblock)
    {
        return posInMultiblock.subtract(masterFromOrigin);
    }

    @Override
    public Vec3i getSize(@Nullable Level world)
    {
        return size;
    }

    @Nonnull
    @Override
    protected StructureTemplate getTemplate(@Nullable Level world)
    {
        StructureTemplate result = super.getTemplate(world);
        Preconditions.checkState(
                result.getSize().equals(size),
                "Wrong template size for multiblock %s, template size: %s",
                getTemplateLocation(), result.getSize()
        );
        return result;
    }

    @Override
    protected void prepareBlockForDisassembly(Level world, BlockPos pos)
    {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof MultiblockPartBlockEntity<?> multiblockBE)
            multiblockBE.formed = false;
        else if(be!=null)
            IELogger.logger.error("Expected multiblock TE at {}, got {}", pos, be);
    }

    @Override
    public void initializeClient(Consumer<ClientMultiblocks.MultiblockManualData> consumer)
    {
        consumer.accept(new BasicClientProperties(this));
    }

    public ResourceLocation getBlockName() { return baseState.getId(); }

    @Override
    public Component getDisplayName()
    {
        return baseState.get().getName();
    }

    @Override
    public Block getBlock()
    {
        return baseState.get();
    }
}