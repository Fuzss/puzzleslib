package fuzs.puzzleslib.neoforge.impl.capability.data;

import fuzs.puzzleslib.api.capability.v2.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class NeoForgePlayerCapabilityKey<T, C extends CapabilityComponent<T>> extends NeoForgeCapabilityKey<T, C> implements PlayerCapabilityKey<C> {
    /**
     * has a respawn strategy for players for copying capability data been set
     * we don't need to have the actual strategy, just make sure it isn't set multiple times using this
     */
    private boolean respawnStrategy;
    /**
     * strategy for syncing this capability data to remote
     */
    private SyncStrategy syncStrategy = SyncStrategies.MANUAL;

    /**
     * @param id                capability id
     * @param componentClass    capability type class
     * @param factory           factory for creating the actual capability, needs to wait until we have the {@link CapabilityToken}
     */
    public NeoForgePlayerCapabilityKey(ResourceLocation id, Class<C> componentClass, CapabilityTokenFactory<C> factory) {
        super(id, componentClass);
    }

    /**
     * @param respawnStrategy   respawn strategy for players for copying capability data
     * @return                  builder
     */
    public NeoForgePlayerCapabilityKey<C> setRespawnStrategy(CopyStrategy respawnStrategy) {
        // do this to avoid registering the event multiple times accidentally somehow
        if (this.respawnStrategy) throw new IllegalStateException("Attempting to set new player respawn strategy when it has already been set");
        this.respawnStrategy = true;
        MinecraftForge.EVENT_BUS.addListener((final PlayerEvent.Clone evt) -> this.onPlayerClone(evt, respawnStrategy));
        return this;
    }

    /**
     * @param syncStrategy      strategy for syncing this capability data to remote
     * @return                  builder
     */
    public NeoForgePlayerCapabilityKey<C> setSyncStrategy(SyncStrategy syncStrategy) {
        if (this.syncStrategy != SyncStrategies.MANUAL) throw new IllegalStateException("Attempting to set new sync behaviour when it has already been set");
        this.syncStrategy = syncStrategy;
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerChangedDimension);
        if (syncStrategy == SyncStrategies.SELF_AND_TRACKING) {
            MinecraftForge.EVENT_BUS.addListener(this::onStartTracking);
        }
        return this;
    }

    @Override
    public void syncToRemote(ServerPlayer receiver) {
        PlayerCapabilityKey.syncCapabilityToRemote(this.ge, player, this.syncStrategy, this.orThrow(player), this.identifier(), false);
    }

    private void onPlayerClone(final PlayerEvent.Clone evt, CopyStrategy respawnStrategy) {
        // we have to revive caps and then invalidate them again since 1.17+
        evt.getOriginal().reviveCaps();
        this.maybeGet(evt.getOriginal()).ifPresent(oldCapability -> {
            this.maybeGet(evt.getEntity()).ifPresent(newCapability -> {
                respawnStrategy.copy(oldCapability, newCapability, !evt.isWasDeath(), evt.getEntity().level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY));
            });
        });
        evt.getOriginal().invalidateCaps();
    }

    private void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent evt) {
        Player player = evt.getEntity();
        this.maybeGet(player).ifPresent(capability -> {
            PlayerCapabilityKey.syncCapabilityToRemote(player, (ServerPlayer) player, this.syncStrategy, capability, this.identifier(), true);
        });
    }

    private void onPlayerChangedDimension(final PlayerEvent.PlayerChangedDimensionEvent evt) {
        Player player = evt.getEntity();
        this.maybeGet(player).ifPresent(capability -> {
            PlayerCapabilityKey.syncCapabilityToRemote(player, (ServerPlayer) player, this.syncStrategy, capability, this.identifier(), true);
        });
    }

    private void onStartTracking(final PlayerEvent.StartTracking evt) {
        this.maybeGet(evt.getTarget()).ifPresent(capability -> {
            // we only want to sync to the client that just started tracking, so use SyncStrategy#SELF
            PlayerCapabilityKey.syncCapabilityToRemote(evt.getTarget(), (ServerPlayer) evt.getEntity(), (SyncStrategy) SyncStrategies.SELF, capability, this.identifier(), true);
        });
    }
}