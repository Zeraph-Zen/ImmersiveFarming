package net.etylop.immersivefarming.block.entity;

import net.etylop.immersivefarming.block.IFBlocks;
import net.etylop.immersivefarming.particle.IFParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SprinklerExtendedBlockEntity extends SprinklerBlockEntity {

    public SprinklerExtendedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.sprinklerRotationSpeed = 1f;
        this.referenceBlock = IFBlocks.SPRINKLER_EXTENDED.get();
    }

    @Override
    protected void spawnParticles() {
        BlockPos pos = getBlockPos().above();
        float angle = (float) (-2*Math.PI*this.sprinklerRotation/360);
        for(int i = 0; i < 120; i++) {
            double velocity = 0.8+0.6*Math.random();
            getLevelNonnull().addParticle(IFParticles.SPRINKLER_PARTICLES.get(),
                    pos.getX() + 0.5d, pos.getY() + 0.6d, pos.getZ() + 0.5d,
                    (Math.cos(angle)+0.12*getRandom())*velocity, (0.35+0.3*Math.random())*velocity, (Math.sin(angle)+0.12*getRandom())*velocity);
        }
    }

    private double getRandom() {return 2*(Math.random()-0.5);}
}
