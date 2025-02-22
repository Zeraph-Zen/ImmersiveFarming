package net.etylop.immersivefarming.network.serverbound;

import com.google.common.base.MoreObjects;
import com.mojang.datafixers.util.Pair;
import net.etylop.immersivefarming.entity.AbstractDrawnEntity;
import net.etylop.immersivefarming.network.Message;
import net.etylop.immersivefarming.network.ServerMessageContext;
import net.etylop.immersivefarming.world.IFWorld;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.Optional;

public final class ActionKeyMessage implements Message {
    @Override
    public void encode(final FriendlyByteBuf buf) {
    }

    @Override
    public void decode(final FriendlyByteBuf buf) {
    }

    public static void handle(final ActionKeyMessage msg, final ServerMessageContext ctx) {
        final ServerPlayer player = ctx.getPlayer();
        final Entity pulling = MoreObjects.firstNonNull(player.getVehicle(), player);
        final Level world = player.level;
        IFWorld.get(world).map(w -> w.getDrawn(pulling)).orElse(Optional.empty())
            .map(c -> Pair.of(c, (Entity) null))
            .or(() -> world.getEntitiesOfClass(AbstractDrawnEntity.class, pulling.getBoundingBox().inflate(2.0D), entity -> entity != pulling).stream()
                .min(Comparator.comparing(pulling::distanceTo))
                .map(c -> Pair.of(c, pulling))
            ).ifPresent(p -> p.getFirst().setPulling(p.getSecond()));
    }
}
