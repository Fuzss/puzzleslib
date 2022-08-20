package fuzs.puzzleslib.capability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.PuzzlesLibForge;
import fuzs.puzzleslib.capability.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * implementation of {@link CapabilityController} for Forge
 *
 * <p>after creating {@link CapabilityKey} in common project, the Forge project must call {@link #setCapabilityToken} by providing
 * the non-wrappable {@link net.minecraftforge.common.capabilities.CapabilityToken} for creating the actual {@link Capability}
 */
public class ForgeCapabilityController implements CapabilityController {
    /**
     * capability controllers are stored for each mod separately to avoid concurrency issues, might not be need though
     */
    private static final Map<String, ForgeCapabilityController> MOD_TO_CAPABILITIES = Maps.newConcurrentMap();

    /**
     * namespace for this instance
     */
    private final String namespace;
    /**
     * all keys, used to be able to check in {@link #idToCapabilityData} if all required capabilities are present
     */
    private final Multimap<Class<?>, ResourceLocation> providerClazzToIds = HashMultimap.create();
    /**
     * internal storage for registering capability entries
     */
    private final Map<ResourceLocation, CapabilityData<?>> idToCapabilityData = Maps.newHashMap();

    /**
     * private constructor
     * @param namespace namespace for this instance
     */
    private ForgeCapabilityController(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Predicate<Item> itemFilter) {
        return this.registerCapability(ItemStack.class, capabilityKey, capabilityType, capabilityFactory, o -> o instanceof Item item && itemFilter.test(item));
    }

    @Override
    public <T extends Entity, C extends CapabilityComponent> CapabilityKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, capabilityFactory, entityType::isInstance);
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, PlayerRespawnStrategy respawnStrategy) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, capabilityFactory, Player.class::isInstance, respawnStrategy);
    }

    @Override
    public <T extends BlockEntity, C extends CapabilityComponent> CapabilityKey<C> registerBlockEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<T> blockEntityType) {
        return this.registerCapability(BlockEntity.class, capabilityKey, capabilityType, capabilityFactory, blockEntityType::isInstance);
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelChunkCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory) {
        return this.registerCapability(LevelChunk.class, capabilityKey, capabilityType, capabilityFactory, o -> true);
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory) {
        return this.registerCapability(Level.class, capabilityKey, capabilityType, capabilityFactory, o -> true);
    }

    /**
     * register capabilities for a given object type
     *
     * @param providerType          type of object to attach to, only works for generic supertypes
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param filter                filter for <code>providerType</code>
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    private <C extends CapabilityComponent> CapabilityKey<C> registerCapability(Class<? extends ICapabilityProvider> providerType, String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Predicate<Object> filter) {
        return this.registerCapability(providerType, capabilityKey, capabilityType, capabilityFactory, filter, null);
    }

    /**
     * register capabilities for a given object type
     *
     * @param providerType          type of object to attach to, only works for generic supertypes
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param filter                filter for <code>providerType</code>
     * @param respawnStrategy       respawn strategy for players for copying capability data
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    private <C extends CapabilityComponent> CapabilityKey<C> registerCapability(Class<? extends ICapabilityProvider> providerType, String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Predicate<Object> filter, @Nullable PlayerRespawnStrategy respawnStrategy) {
        ResourceLocation key = new ResourceLocation(this.namespace, capabilityKey);
        this.providerClazzToIds.put(providerType, key);
        return new ForgeCapabilityKey<>(key, capabilityType, token -> {
            final Capability<C> capability = CapabilityManager.get(token);
            this.idToCapabilityData.put(key, new CapabilityData<>(key, capability, capabilityType, provider -> new CapabilityHolder<>(capability, capabilityFactory.createComponent(provider)), filter, respawnStrategy));
            return capability;
        });
    }

    private void onRegisterCapabilities(final RegisterCapabilitiesEvent evt) {
        for (CapabilityData<?> data : this.toCapabilityData(this.providerClazzToIds.values())) {
            evt.register(data.capabilityType());
        }
    }

    @SubscribeEvent
    public void onAttachCapabilities(final AttachCapabilitiesEvent<?> evt) {
        for (CapabilityData<?> data : this.toCapabilityData((Class<?>) evt.getGenericType())) {
            if (data.filter().test(evt.getObject())) {
                evt.addCapability(data.capabilityKey(), data.capabilityFactory().createComponent(evt.getObject()));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone evt) {
        if (this.providerClazzToIds.get(Entity.class).isEmpty()) return;
        // we have to revive caps and then invalidate them again since 1.17+
        evt.getOriginal().reviveCaps();
        for (CapabilityData<?> data : this.toCapabilityData(Entity.class)) {
            evt.getOriginal().getCapability(data.capability()).ifPresent(oldCapability -> {
                evt.getEntity().getCapability(data.capability()).ifPresent(newCapability -> {
                    data.respawnStrategy().copy(oldCapability, newCapability, !evt.isWasDeath(), evt.getEntity().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY));
                });
            });
        }
        evt.getOriginal().invalidateCaps();
    }

    /**
     * maps keys to corresponding {@link CapabilityData}, checks if the capability has been registered yet
     * by calling {@link #setCapabilityToken} to make sure it hasn't been forgotten
     *
     * @param providerClazz     clazz to get registered keys for
     * @return                  keys mapped to {@link CapabilityData}
     */
    private Collection<? extends CapabilityData<?>> toCapabilityData(Class<?> providerClazz) {
        return this.toCapabilityData(this.providerClazzToIds.get(providerClazz));
    }

    /**
     * maps <code>keys</code> to corresponding {@link CapabilityData}, checks if the capability has been registered yet
     * by calling {@link #setCapabilityToken} to make sure it hasn't been forgotten
     *
     * @param keys  {@link ResourceLocation} keys to map
     * @return      <code>keys</code> mapped to {@link CapabilityData}
     */
    private Collection<? extends CapabilityData<?>> toCapabilityData(Collection<ResourceLocation> keys) {
        return keys.stream().map(key -> {
            CapabilityData<?> data = this.idToCapabilityData.get(key);
            Objects.requireNonNull(data, "No valid capability implementation registered for %s".formatted(key));
            return data;
        }).toList();
    }

    /**
     * just hides a cast for Forge specific method
     *
     * @param key       the {@link CapabilityKey} to add the <code>token</code> to
     * @param token     the token, created with an anonymous class
     * @param <C>       capability type
     */
    public static <C extends CapabilityComponent> void setCapabilityToken(CapabilityKey<C> key, CapabilityToken<C> token) {
        ((ForgeCapabilityKey<C>) key).createCapability(token);
    }

    /**
     * creates a new capability controller for <code>namespace</code> or returns an existing one
     *
     * @param namespace     namespace used for registration
     * @return              the mod specific capability controller
     */
    public static synchronized ForgeCapabilityController of(String namespace) {
        return MOD_TO_CAPABILITIES.computeIfAbsent(namespace, key -> {
            final ForgeCapabilityController controller = new ForgeCapabilityController(namespace);
            // for registering capabilities
            PuzzlesLibForge.findModEventBus(namespace).addListener(controller::onRegisterCapabilities);
            // for attaching capabilities
            MinecraftForge.EVENT_BUS.register(controller);
            return controller;
        });
    }

    /**
     * just a data class for all the things we need when registering capabilities...
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capability            capability instance
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param filter                filter for provider type
     * @param respawnStrategy       respawn strategy for players for copying capability data
     * @param <C>                   capability type
     */
    private record CapabilityData<C extends CapabilityComponent>(ResourceLocation capabilityKey, Capability<C> capability, Class<C> capabilityType, CapabilityFactory<CapabilityHolder<C>> capabilityFactory, Predicate<Object> filter, PlayerRespawnStrategy respawnStrategy) {

    }
}
