package fuzs.puzzleslib.impl.capability;

import fuzs.puzzleslib.api.capability.v2.data.SyncStrategy;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

public final class SyncStrategyImpl implements SyncStrategy {
    private final BiConsumer<?, ServerPlayer> sender;

    public <T extends Record & ClientboundMessage<T>> SyncStrategyImpl(BiConsumer<T, ServerPlayer> sender) {
        this.sender = sender;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Record & ClientboundMessage<T>> void sendTo(T message, ServerPlayer player) {
        ((BiConsumer<T, ServerPlayer>) this.sender).accept(message, player);
    }
}
