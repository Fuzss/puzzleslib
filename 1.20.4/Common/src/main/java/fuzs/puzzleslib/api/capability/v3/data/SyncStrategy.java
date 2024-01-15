package fuzs.puzzleslib.api.capability.v3.data;

import fuzs.puzzleslib.impl.PuzzlesLibMod;
import fuzs.puzzleslib.impl.capability.ClientboundSyncCapabilityMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Different behaviours for automatically syncing capability components to tracking remotes.
 */
public enum SyncStrategy {
    /**
     * Default state, no syncing is done automatically.
     */
    MANUAL {

        @Override
        public <T extends Entity> void send(ResourceLocation identifier, CapabilityComponent<T> capabilityComponent) {

        }
    },
    /**
     * Syncing is done automatically with just the capability holder if it is a {@link ServerPlayer}.
     *
     * <p>Useful for capabilities that control an ability, like the amount of midair jumps left.
     */
    SELF {

        @Override
        public <T extends Entity> void send(ResourceLocation identifier, CapabilityComponent<T> capabilityComponent) {
            if (capabilityComponent.getHolder() instanceof ServerPlayer player) {
                PuzzlesLibMod.NETWORK.sendTo(player, this.toPacket(identifier, capabilityComponent));
            }
        }
    },
    /**
     * Syncing is done automatically with the capability holder and every {@link ServerPlayer} tracking them.
     *
     * <p>Useful for capabilities that affect rendering (e.g. a glider is equipped for gliding).
     */
    TRACKING {

        @Override
        public <T extends Entity> void send(ResourceLocation identifier, CapabilityComponent<T> capabilityComponent) {
            PuzzlesLibMod.NETWORK.sendToAllTracking(capabilityComponent.getHolder(), this.toPacket(identifier, capabilityComponent), true);
        }
    };

    /**
     * Send a {@link CapabilityComponent} to remotes.
     *
     * @param identifier          identifier from {@link CapabilityKey#identifier()}
     * @param capabilityComponent the component to send to remotes
     * @param <T>                 entity type
     */
    public abstract <T extends Entity> void send(ResourceLocation identifier, CapabilityComponent<T> capabilityComponent);

    final <T extends Entity> ClientboundSyncCapabilityMessage toPacket(ResourceLocation identifier, CapabilityComponent<T> capabilityComponent) {
        return new ClientboundSyncCapabilityMessage(identifier, capabilityComponent.getHolder().getId(), capabilityComponent.toCompoundTag());
    }
}
