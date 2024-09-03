package net.etylop.immersivefarming.common.data.generators;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.data.models.NongeneratedModels;
import blusunrize.immersiveengineering.data.models.SplitModelBuilder;
import com.google.common.base.Preconditions;
import net.etylop.immersivefarming.ImmersiveFarming;
import net.etylop.immersivefarming.block.IFBlocks;
import net.etylop.immersivefarming.block.IFMultiblocks;
import net.etylop.immersivefarming.block.multiblocks.composter.ComposterMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IFBlockStateProvider extends BlockStateProvider {

    private Logger log = ImmersiveFarming.getNewLogger();
    private final NongeneratedModels nongeneratedModels;
    final ExistingFileHelper exFileHelper;
    private final BlockModelProvider customModels;
    public IFBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper)
    {
        super(gen, ImmersiveFarming.MOD_ID, exFileHelper);
        this.exFileHelper = exFileHelper;
        this.nongeneratedModels = new NongeneratedModels(gen, exFileHelper);
        this.customModels = new BlockModelProvider(gen, ImmersiveFarming.MOD_ID, exFileHelper) {

            protected void registerModels() {

            }
        };
    }
    @Override
    public @NotNull String getName(){
        return "Block Model/States";
    }
    @Override
    protected void registerStatesAndModels() {
        ImmersiveFarming.getNewLogger().info("Registering block states...");
        composter();
    }

    private void composter(){
        ResourceLocation texture = modLoc("block/composter");
        ResourceLocation modelNormal = modLoc("models/block/multiblock/composter/composter.obj");
        ResourceLocation modelMirrored = modLoc("models/block/multiblock/composter/composter.obj");

        BlockModelBuilder normal = multiblockModel(IFBlocks.COMPOSTER.get(), modelNormal, texture, "", ComposterMultiblock.INSTANCE, false);
        BlockModelBuilder mirrored = multiblockModel(IFBlocks.COMPOSTER.get(), modelMirrored, texture, "_mirrored", ComposterMultiblock.INSTANCE, true);

        createMultiblock(IFMultiblocks.COMPOSTER.getBlock(), normal, mirrored, texture);
    }

    private BlockModelBuilder multiblockModel(Block block, ResourceLocation model, ResourceLocation texture, String add, TemplateMultiblock mb, boolean mirror){
        UnaryOperator<BlockPos> transform = UnaryOperator.identity();
        if(mirror){
            Vec3i size = mb.getSize(null);
            transform = p -> new BlockPos(size.getX() - p.getX() - 1, p.getY(), p.getZ());
        }
        final Vec3i offset = mb.getMasterFromOriginOffset();

        Stream<Vec3i> partsStream = mb.getStructure(null).stream()
                .filter(info -> !info.state.isAir())
                .map(info -> info.pos)
                .map(transform)
                .map(p -> p.subtract(offset));

        String name = getMultiblockPath(block) + add;
        NongeneratedModels.NongeneratedModel base = nongeneratedModels.withExistingParent(name, mcLoc("block"))
                .customLoader(OBJLoaderBuilder::begin).modelLocation(model).detectCullableFaces(false).flipV(true).end()
                .texture("texture", texture)
                .texture("particle", texture);

        BlockModelBuilder split = this.models().withExistingParent(name + "_split", mcLoc("block"))
                .customLoader(SplitModelBuilder::begin)
                .innerModel(base)
                .parts(partsStream.collect(Collectors.toList()))
                .dynamic(false).end();

        return split;
    }


    /** From {@link blusunrize.immersiveengineering.data.blockstates.BlockStates} */
    private void createMultiblock(Block b, ModelFile masterModel, ModelFile mirroredModel, ResourceLocation particleTexture){
        createMultiblock(b, masterModel, mirroredModel, IEProperties.MULTIBLOCKSLAVE, IEProperties.FACING_HORIZONTAL, IEProperties.MIRRORED, 180, particleTexture);
    }

    /** From {@link blusunrize.immersiveengineering.data.blockstates.BlockStates} */
    private void createMultiblock(Block b, ModelFile masterModel, @Nullable ModelFile mirroredModel, Property<Boolean> isSlave, EnumProperty<Direction> facing, @Nullable Property<Boolean> mirroredState, int rotationOffset, ResourceLocation particleTex){
        Preconditions.checkArgument((mirroredModel == null) == (mirroredState == null));
        VariantBlockStateBuilder builder = getVariantBuilder(b);

        boolean[] possibleMirrorStates;
        if(mirroredState != null)
            possibleMirrorStates = new boolean[]{false, true};
        else
            possibleMirrorStates = new boolean[1];
        for(boolean mirrored:possibleMirrorStates)
            for(Direction dir:facing.getPossibleValues()){
                final int angleY;
                final int angleX;
                if(facing.getPossibleValues().contains(Direction.UP)){
                    angleX = -90 * dir.getStepY();
                    if(dir.getAxis() != Direction.Axis.Y)
                        angleY = getAngle(dir, rotationOffset);
                    else
                        angleY = 0;
                }else{
                    angleY = getAngle(dir, rotationOffset);
                    angleX = 0;
                }

                ModelFile model = mirrored ? mirroredModel : masterModel;
                VariantBlockStateBuilder.PartialBlockstate partialState = builder.partialState()
//						.with(isSlave, false)
                        .with(facing, dir);

                if(mirroredState != null)
                    partialState = partialState.with(mirroredState, mirrored);

                partialState.setModels(new ConfiguredModel(model, angleX, angleY, true));
            }
    }

    /** From {@link blusunrize.immersiveengineering.data.blockstates.BlockStates} */
    private int getAngle(Direction dir, int offset){
        return (int) ((dir.toYRot() + offset) % 360);
    }

    private String getMultiblockPath(Block b){
        return "multiblock/" + getPath(b);
    }

    private String getPath(Block b){
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(b)).getPath();
    }
}
