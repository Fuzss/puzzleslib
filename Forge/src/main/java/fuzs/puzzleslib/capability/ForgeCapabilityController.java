package fuzs.puzzleslib.capability;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.PuzzlesLibForge;
import fuzs.puzzleslib.capability.data.*;
import fuzs.puzzleslib.init.ForgeRegistryManager;
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

import java.util.Map;
import java.util.function.Predicate;

/**
 * class for registering and attaching mod capabilities, every mod gets their own instance,
 * mostly due to Forge requiring to register some events which should be done on a per mod basis
 * the structure of this is similar to {@link ForgeRegistryManager}
 *
 * public facing methods in this class are basically the same for Forge and Fabric,
 * only difference is Forge requires an additional CapabilityToken which cannot be used in the common project,
 * therefore capabilities need to be created separately for each mod loader
 *
 * capabilities may be used inside the common project by calling {@link CapabilityController#makeCapabilityKey}
 * which will provide a placeholder which updates itself with the mod specific implementation once it is used
 */
public class ForgeCapabilityController {
    /**
     * capability controllers are stored for each mod separately to avoid concurrency issues, might not be need though
     */
    private static final Map<String, ForgeCapabilityController> MOD_TO_CAPABILITIES = Maps.newConcurrentMap();

    /**
     * namespace for this instance
     */
    private final String namespace;
    /**
     * internal storage for registering capability entries
     */
    private final Multimap<Class<?>, CapabilityData<?>> typeToData = ArrayListMultimap.create();

    /**
     * private constructor
     * @param namespace namespace for this instance
     */
    private ForgeCapabilityController(String namespace) {
        this.namespace = namespace;
    }

    /**
     * forge event
     */
    private void onRegisterCapabilities(final RegisterCapabilitiesEvent evt) {
        for (CapabilityData<?> data : this.typeToData.values()) {
            evt.register(data.capabilityType());
        }
    }

    /**
     * @param evt forge event
     */
    @Deprecated
    @SubscribeEvent
    public void onAttachCapabilities(final AttachCapabilitiesEvent<?> evt) {
        for (CapabilityData<?> data : this.typeToData.get((Class<?>) evt.getGenericType())) {
            if (data.filter().test(evt.getObject())) {
                evt.addCapability(data.capabilityKey(), data.capabilityFactory().createComponent(evt.getObject()));
            }
        }
    }

    /**
     * @param evt forge event
     */
    @Deprecated
    @SubscribeEvent
    public void onPlayerClone(final PlayerEvent.Clone evt) {
        if (this.typeToData.get(Entity.class).isEmpty()) return;
        // we have to revive caps and then invalidate them again since 1.17+
        evt.getOriginal().reviveCaps();
        for (CapabilityData<?> data : this.typeToData.get(Entity.class)) {
            evt.getOriginal().getCapability(data.capability()).ifPresent(oldCapability -> {
                evt.getEntity().getCapability(data.capability()).ifPresent(newCapability -> {
                    ((PlayerCapabilityData<?>) data).respawnStrategy().copy(oldCapability, newCapability, !evt.isWasDeath(), evt.getEntity().level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY));
                });
            });
        }
        evt.getOriginal().invalidateCaps();
    }

    /**
     * register capability to {@link ItemStack} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param item              item to apply capability to
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    public <C extends CapabilityComponent> CapabilityKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Item item, CapabilityToken<C> token) {
        return this.registerItemCapability(capabilityKey, capabilityType, capabilityFactory, o -> o == item, token);
    }

    /**
     * register capability to {@link ItemStack} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param itemFilter filter for item, can be used for matching class and instanceof
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    public <C extends CapabilityComponent> CapabilityKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Predicate<Item> itemFilter, CapabilityToken<C> token) {
        return this.registerCapability(ItemStack.class, capabilityKey, capabilityType, capabilityFactory, o -> o instanceof Item item && itemFilter.test(item), token);
    }

    /**
     * register capability to {@link Entity} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param entityType        entity class to match
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    public <C extends CapabilityComponent> CapabilityKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<? extends Entity> entityType, CapabilityToken<C> token) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, capabilityFactory, entityType::isInstance, token);
    }

    /**
     * register capability to {@link Entity} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param respawnStrategy   strategy to use when returning from the end dimension or after dying
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    public <C extends CapabilityComponent> CapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, PlayerRespawnStrategy respawnStrategy, CapabilityToken<C> token) {
        final Capability<C> capability = CapabilityManager.get(token);
        ResourceLocation key = new ResourceLocation(this.namespace, capabilityKey);
        this.typeToData.put(Entity.class, new PlayerCapabilityData<>(key, capability, capabilityType, provider -> new CapabilityHolder<>(capability, capabilityFactory.createComponent(provider)), Player.class::isInstance, respawnStrategy));
        return new ForgeCapabilityKey<>(capability, key, capabilityType);
    }

    /**
     * register capability to {@link BlockEntity} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param blockEntityType   block entity class to match
     * @param token capability token required to get capability instance from capability manager
     * @return capability instance from capability manager
     * @param <C> capability type
     */
    public <C extends CapabilityComponent> CapabilityKey<C> registerBlockEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<? extends BlockEntity> blockEntityType, CapabilityToken<C> token) {
        return this.registerCapability(BlockEntity.class, capabilityKey, capabilityType, capabilityFactory, blockEntityType::isInstance, token);
    }

    /**
     * register capability to {@link LevelChunk} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelChunkCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, CapabilityToken<C> token) {
        return this.registerCapability(LevelChunk.class, capabilityKey, capabilityType, capabilityFactory, o -> true, token);
    }

    /**
     * register capability to {@link Level} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, CapabilityToken<C> token) {
        return this.registerCapability(Level.class, capabilityKey, capabilityType, capabilityFactory, o -> true, token);
    }

    /**
     * register capabilities for a given object type
     * @param providerType type of object to attach to, only works for generic supertypes
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param filter filter for <code>providerType</code>
     * @param token capability token required to get capability instance from capability manager
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    private <C extends CapabilityComponent> CapabilityKey<C> registerCapability(Class<? extends ICapabilityProvider> providerType, String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Predicate<Object> filter, CapabilityToken<C> token) {
        final Capability<C> capability = CapabilityManager.get(token);
        ResourceLocation key = new ResourceLocation(this.namespace, capabilityKey);
        this.typeToData.put(providerType, new DefaultCapabilityData<>(key, capability, capabilityType, provider -> new CapabilityHolder<>(capability, capabilityFactory.createComponent(provider)), filter));
        return new ForgeCapabilityKey<>(capability, key, capabilityType);
    }

    /**
     * creates a new capability controller for <code>namespace</code> or returns an existing one
     * @param namespace namespace used for registration
     * @return new mod specific capability controller
     */
    public static synchronized ForgeCapabilityController of(String namespace) {
        return MOD_TO_CAPABILITIES.computeIfAbsent(namespace, key -> {
            final ForgeCapabilityController manager = new ForgeCapabilityController(namespace);
            // for registering capabilities
            PuzzlesLibForge.findModEventBus(namespace).addListener(manager::onRegisterCapabilities);
            // for attaching capabilities
            MinecraftForge.EVENT_BUS.register(manager);
            return manager;
        });
    }

    /**
     * base structure for capability data, we use this since actual data classes are records
     * @param <C> serializable capability
     */
    private interface CapabilityData<C extends CapabilityComponent> {
        /**
         * @return path for internal name of this capability, will be used for serialization
         */
        ResourceLocation capabilityKey();

        /**
         * @return capability instance
         */
        Capability<C> capability();

        /**
         * @return interface for this capability
         */
        Class<C> capabilityType();

        /**
         * @return capability factory called when attaching to an object
         */
        CapabilityFactory<CapabilityHolder<C>> capabilityFactory();

        /**
         * @return filter for provider type
         */
        Predicate<Object> filter();
    }

    /**
     * just a data class for all the things we need when registering capabilities...
     */
    private static record DefaultCapabilityData<C extends CapabilityComponent>(ResourceLocation capabilityKey, Capability<C> capability, Class<C> capabilityType, CapabilityFactory<CapabilityHolder<C>> capabilityFactory, Predicate<Object> filter) implements CapabilityData<C> {

    }

    /**
     * just a data class for all the things we need when registering capabilities...
     */
    private static record PlayerCapabilityData<C extends CapabilityComponent>(ResourceLocation capabilityKey, Capability<C> capability, Class<C> capabilityType, CapabilityFactory<CapabilityHolder<C>> capabilityFactory, Predicate<Object> filter, PlayerRespawnStrategy respawnStrategy) implements CapabilityData<C> {

    }
}
