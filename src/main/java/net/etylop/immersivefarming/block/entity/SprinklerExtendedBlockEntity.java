package net.etylop.immersivefarming.block.entity;

import net.etylop.immersivefarming.particle.IFParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SprinklerExtendedBlockEntity extends SprinklerBlockEntity {

    public SprinklerExtendedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.sprinklerRotationSpeed = 1f;
    }

    @Override
    protected void spawnParticles() {
        BlockPos pos = getBlockPos().above();
        float angle = (float) (-2*Math.PI*this.sprinklerRotation/360);
        for(int i = 0; i < 50; i++) {
            double velocity = 0.8;
            getLevelNonnull().addParticle(IFParticles.SPRINKLER_PARTICLES.get(),
                    pos.getX() + 0.5d, pos.getY() + 0.6d, pos.getZ() + 0.5d,
                    Math.cos(angle)*velocity+0.1*getRandom(), 0.6*velocity+0.4*getRandom(), Math.sin(angle)*velocity+0.1*getRandom());
        }
    }

    private double getRandom() {return 2*(Math.random()-0.5);}
}
