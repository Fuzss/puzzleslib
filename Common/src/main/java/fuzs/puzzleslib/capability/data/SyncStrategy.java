package fuzs.puzzleslib.capability.data;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.network.v2.ClientboundMessage;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;

/**
 * different behaviours for automatically syncing this capability
 */
public final class SyncStrategy<T extends Record & ClientboundMessage<T>> {
    /**
     * default state, no syncing is done automatically
     */
    public static final SyncStrategy<?> MANUAL = new SyncStrategy<>((o1, o2) -> {});
    /**
     * syncing is done automatically, but only with the capability holder
     */
    public static final SyncStrategy<?> SELF = new SyncStrategy<>((message, player) -> PuzzlesLib.NETWORK.sendTo(message, player));
    /**
     * syncing is done automatically, with the capability holder and every player tracking them
     * useful for capabilities that affect rendering (e.g. a glider is gliding)
     */
    public static final SyncStrategy<?> SELF_AND_TRACKING = new SyncStrategy<>((message, entity) -> PuzzlesLib.NETWORK.sendToAllTrackingAndSelf(message, entity));

    /**
     * message handler
     */
    private final BiConsumer<T, ServerPlayer> sender;

    /**
     * @param sender message handler
     */
    private SyncStrategy(BiConsumer<T, ServerPlayer> sender) {
        this.sender = sender;
    }

    @SuppressWarnings("unchecked")
    public <S extends Record & ClientboundMessage<S>> void accept(S message, ServerPlayer player) {
        this.sender.accept((T) message, player);
    }
}
