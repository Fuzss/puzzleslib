package fuzs.puzzleslib.impl.capability;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.capability.v2.data.*;
import fuzs.puzzleslib.api.core.v1.ModContainerHelper;
import fuzs.puzzleslib.impl.capability.data.CapabilityHolder;
import fuzs.puzzleslib.impl.capability.data.ForgeCapabilityKey;
import fuzs.puzzleslib.impl.capability.data.ForgePlayerCapabilityKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * implementation of {@link CapabilityController} for Forge
 *
 * <p>after creating {@link fuzs.puzzleslib.api.capability.v2.data.CapabilityKey} in common project, the Forge project must call {@link fuzs.puzzleslib.api.capability.v2.ForgeCapabilityHelper#setCapabilityToken} by providing
 * the non-wrappable {@link net.minecraftforge.common.capabilities.CapabilityToken} for creating the actual {@link Capability}
 */
public class ForgeCapabilityController implements CapabilityController {
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

    public ForgeCapabilityController(String namespace) {
        this.namespace = namespace;
        // for registering capabilities
        ModContainerHelper.findModEventBus(namespace).ifPresent(eventBus -> {
            eventBus.addListener(this::onRegisterCapabilities);
        });
        // for attaching capabilities
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public <T extends Entity, C extends CapabilityComponent> CapabilityKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, capabilityFactory, entityType::isInstance);
    }

    @Override
    public <C extends CapabilityComponent> PlayerCapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, PlayerRespawnStrategy respawnStrategy) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, capabilityFactory, Player.class::isInstance, ForgePlayerCapabilityKey<C>::new).setRespawnStrategy(respawnStrategy);
    }

    @Override
    public <C extends CapabilityComponent> PlayerCapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, PlayerRespawnStrategy respawnStrategy, SyncStrategy<?> syncStrategy) {
        return ((ForgePlayerCapabilityKey<C>) this.registerPlayerCapability(capabilityKey, capabilityType, capabilityFactory, respawnStrategy)).setSyncStrategy(syncStrategy);
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
        return this.registerCapability(providerType, capabilityKey, capabilityType, capabilityFactory, filter, ForgeCapabilityKey<C>::new);
    }

    /**
     * register capabilities for a given object type
     *
     * @param providerType          type of object to attach to, only works for generic supertypes
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param filter                filter for <code>providerType</code>
     * @param capabilityKeyFactory  factory for the capability key implementation, required by players
     * @param <C>                   capability type
     * @param <T>                   special capability type for performing additional setup actions on the key
     * @return                      capability instance from capability manager
     */
    private <C extends CapabilityComponent, T extends CapabilityKey<C>> T registerCapability(Class<? extends ICapabilityProvider> providerType, String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Predicate<Object> filter, ForgeCapabilityKey.ForgeCapabilityKeyFactory<C, T> capabilityKeyFactory) {
        ResourceLocation key = new ResourceLocation(this.namespace, capabilityKey);
        this.providerClazzToIds.put(providerType, key);
        return capabilityKeyFactory.apply(key, capabilityType, token -> {
            final Capability<C> capability = CapabilityManager.get(token);
            this.idToCapabilityData.put(key, new CapabilityData<>(key, capabilityType, provider -> new CapabilityHolder<>(capability, capabilityFactory.createComponent(provider)), filter));
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

    /**
     * maps keys to corresponding {@link CapabilityData}, checks if the capability has been registered yet
     * by calling {@link fuzs.puzzleslib.api.capability.v2.ForgeCapabilityHelper#setCapabilityToken} to make sure it hasn't been forgotten
     *
     * @param providerClazz     clazz to get registered keys for
     * @return                  keys mapped to {@link CapabilityData}
     */
    private Collection<? extends CapabilityData<?>> toCapabilityData(Class<?> providerClazz) {
        return this.toCapabilityData(this.providerClazzToIds.get(providerClazz));
    }

    /**
     * maps <code>keys</code> to corresponding {@link CapabilityData}, checks if the capability has been registered yet
     * by calling {@link fuzs.puzzleslib.api.capability.v2.ForgeCapabilityHelper#setCapabilityToken} to make sure it hasn't been forgotten
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
     * just a data class for all the things we need when registering capabilities...
     *
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param capabilityFactory     capability factory called when attaching to an object
     * @param filter                filter for provider type
     * @param <C>                   capability type
     */
    private record CapabilityData<C extends CapabilityComponent>(ResourceLocation capabilityKey, Class<C> capabilityType, CapabilityFactory<CapabilityHolder<C>> capabilityFactory, Predicate<Object> filter) {

    }
}
