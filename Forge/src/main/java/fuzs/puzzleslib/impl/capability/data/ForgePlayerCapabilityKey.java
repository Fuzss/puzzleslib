package fuzs.puzzleslib.impl.capability.data;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import fuzs.puzzleslib.api.capability.v2.data.PlayerCapabilityKey;
import fuzs.puzzleslib.api.capability.v2.data.PlayerRespawnCopyStrategy;
import fuzs.puzzleslib.api.capability.v2.data.SyncStrategy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * implementation of {@link fuzs.puzzleslib.api.capability.v2.data.CapabilityKey} for players on Forge
 *
 * @param <C> capability type
 */
public class ForgePlayerCapabilityKey<C extends CapabilityComponent> extends ForgeCapabilityKey<C> implements PlayerCapabilityKey<C> {
    /**
     * has a respawn strategy for players for copying capability data been set
     * we don't need to have the actual strategy, just make sure it isn't set multiple times using this
     */
    private boolean respawnStrategy;
    /**
     * strategy for syncing this capability data to remote
     */
    private SyncStrategy syncStrategy = SyncStrategy.MANUAL;

    /**
     * @param id                capability id
     * @param componentClass    capability type class
     * @param factory           factory for creating the actual capability, needs to wait until we have the {@link CapabilityToken}
     */
    public ForgePlayerCapabilityKey(ResourceLocation id, Class<C> componentClass, CapabilityTokenFactory<C> factory) {
        super(id, componentClass, factory);
    }

    /**
     * @param respawnStrategy   respawn strategy for players for copying capability data
     * @return                  builder
     */
    public ForgePlayerCapabilityKey<C> setRespawnStrategy(PlayerRespawnCopyStrategy respawnStrategy) {
        // do this to avoid registering the event multiple times accidentally somehow
        if (this.respawnStrategy) throw new IllegalStateException("Attempting to set new player respawn strategy when it has already been set");
        this.respawnStrategy = true;
        MinecraftForge.EVENT_BUS.addListener((final PlayerEvent.Clone evt) -> this.onPlayerClone(evt, respawnStrategy));
        return this;
    }

    @Override
    void validateCapability() {
        super.validateCapability();
        if (!this.respawnStrategy) throw new IllegalStateException("Player respawn strategy missing from capability %s".formatted(this.getId()));
    }

    /**
     * @param syncStrategy      strategy for syncing this capability data to remote
     * @return                  builder
     */
    public ForgePlayerCapabilityKey<C> setSyncStrategy(SyncStrategy syncStrategy) {
        if (this.syncStrategy != SyncStrategy.MANUAL) throw new IllegalStateException("Attempting to set new sync behaviour when it has already been set");
        this.syncStrategy = syncStrategy;
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerChangedDimension);
        if (syncStrategy == SyncStrategy.SELF_AND_TRACKING) {
            MinecraftForge.EVENT_BUS.addListener(this::onStartTracking);
        }
        return this;
    }

    @Override
    public void syncToRemote(ServerPlayer player) {
        PlayerCapabilityKey.syncCapabilityToRemote(player, player, this.syncStrategy, this.orThrow(player), this.getId(), false);
    }

    private void onPlayerClone(final PlayerEvent.Clone evt, PlayerRespawnCopyStrategy respawnStrategy) {
        // we have to revive caps and then invalidate them again since 1.17+
        evt.getOriginal().reviveCaps();
        this.maybeGet(evt.getOriginal()).ifPresent(oldCapability -> {
            this.maybeGet(evt.getEntity()).ifPresent(newCapability -> {
                respawnStrategy.copy(oldCapability, newCapability, !evt.isWasDeath(), evt.getEntity().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY));
            });
        });
        evt.getOriginal().invalidateCaps();
    }

    private void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent evt) {
        Player player = evt.getEntity();
        this.maybeGet(player).ifPresent(capability -> {
            PlayerCapabilityKey.syncCapabilityToRemote(player, (ServerPlayer) player, this.syncStrategy, capability, this.getId(), true);
        });
    }

    private void onPlayerChangedDimension(final PlayerEvent.PlayerChangedDimensionEvent evt) {
        Player player = evt.getEntity();
        this.maybeGet(player).ifPresent(capability -> {
            PlayerCapabilityKey.syncCapabilityToRemote(player, (ServerPlayer) player, this.syncStrategy, capability, this.getId(), true);
        });
    }

    private void onStartTracking(final PlayerEvent.StartTracking evt) {
        this.maybeGet(evt.getTarget()).ifPresent(capability -> {
            // we only want to sync to the client that just started tracking, so use SyncStrategy#SELF
            PlayerCapabilityKey.syncCapabilityToRemote(evt.getTarget(), (ServerPlayer) evt.getEntity(), SyncStrategy.SELF, capability, this.getId(), true);
        });
    }
}
