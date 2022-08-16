package fuzs.puzzleslib.capability;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.capability.data.*;
import fuzs.puzzleslib.init.FabricRegistryManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * class for registering and attaching mod capabilities, every mod gets their own instance,
 * mostly due to Forge requiring to register some events which should be done on a per mod basis
 * the structure of this is similar to {@link FabricRegistryManager}
 *
 * public facing methods in this class are basically the same for Forge and Fabric,
 * only difference is Forge requires an additional CapabilityToken which cannot be used in the common project,
 * therefore capabilities need to be created separately for each mod loader
 *
 * capabilities may be used inside the common project by calling {@link CapabilityController#makeCapabilityKey}
 * which will provide a placeholder which updates itself with the mod specific implementation once it is used
 */
//public class FabricCapabilityController implements ItemComponentInitializer, EntityComponentInitializer, BlockComponentInitializer, ChunkComponentInitializer, WorldComponentInitializer {
public class FabricCapabilityController implements EntityComponentInitializer {
    /**
     * capability controllers are stored for each mod separately to avoid concurrency issues, might not be need though
     */
    private static final Map<String, FabricCapabilityController> MOD_TO_CAPABILITIES = Maps.newConcurrentMap();
    /**
     * convert our own {@link PlayerRespawnStrategy} which is designed for Forge back to {@link RespawnCopyStrategy}
     */
    private static final Map<PlayerRespawnStrategy, RespawnCopyStrategy<Component>> STRATEGY_CONVERTER_MAP = ImmutableMap.<PlayerRespawnStrategy, RespawnCopyStrategy<Component>>builder()
            .put(PlayerRespawnStrategy.ALWAYS_COPY, RespawnCopyStrategy.ALWAYS_COPY)
            .put(PlayerRespawnStrategy.INVENTORY, RespawnCopyStrategy.INVENTORY)
            .put(PlayerRespawnStrategy.LOSSLESS, RespawnCopyStrategy.LOSSLESS_ONLY)
            .put(PlayerRespawnStrategy.NEVER, RespawnCopyStrategy.NEVER_COPY)
            .build();

    /**
     * namespace for this instance
     */
    private final String namespace;
    /**
     * internal storage for registering capability entries
     */
    private final Multimap<Class<?>, Consumer<Object>> typeToRegistration = ArrayListMultimap.create();

    /**
     * invoked by cardinal components entry point via reflection
     * this should better be a separate class, but kept in here to remain consistent with Forge and doesn't matter anyway
     */
    @Deprecated
    public FabricCapabilityController() {
        this("_internal");
    }

    /**
     * private constructor
     * @param namespace namespace for this instance
     */
    private FabricCapabilityController(String namespace) {
        this.namespace = namespace;
    }

//    /**
//     * WARNING make this work as the component needs to extend {@link dev.onyxstudios.cca.api.v3.item.ItemComponent}
//     *  (or not as it's supposed to get removed and using the item stack tag directly is perfectly fine)
//     * register capability to {@link ItemStack} objects
//     * @param capabilityKey path for internal name of this capability, will be used for serialization
//     * @param capabilityType interface for this capability
//     * @param capabilityFactory capability factory
//     * @param item item to apply to
//     * @param <C> capability type
//     * @return capability instance from capability manager
//     */
//    public <C extends CapabilityComponent> CapabilityKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Item item) {
//        return this.registerCapability(ItemStack.class, capabilityKey, capabilityType, componentKey -> o -> {
//            if (o instanceof ItemComponentFactoryRegistry registry) registry.register(item, componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)));
//        });
//    }
//
//    /**
//     * WARNING make this work as the component needs to extend {@link dev.onyxstudios.cca.api.v3.item.ItemComponent}
//     *  (or not as it's supposed to get removed and using the item stack tag directly is perfectly fine)
//     * register capability to {@link ItemStack} objects
//     * @param capabilityKey path for internal name of this capability, will be used for serialization
//     * @param capabilityType interface for this capability
//     * @param capabilityFactory capability factory called when attaching to an object
//     * @param itemFilter filter for item, can be used for matching class and instanceof
//     * @param <C> capability type
//     * @return capability instance from capability manager
//     */
//    public <C extends CapabilityComponent> CapabilityKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Predicate<Item> itemFilter) {
//        return this.registerCapability(ItemStack.class, capabilityKey, capabilityType, componentKey -> o -> {
//            if (o instanceof ItemComponentFactoryRegistry registry) registry.register(itemFilter, componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)));
//        });
//    }

    /**
     * register capability to {@link Entity} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param entityType        entity class to match
     * @param <T>               entity type
     * @return capability instance from capability manager
     * @param <C> capability type
     */
    public <T extends Entity, C extends CapabilityComponent> CapabilityKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, componentKey -> o -> {
            if (o instanceof EntityComponentFactoryRegistry registry) registry.registerFor(entityType, componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)));
        });
    }

    /**
     * register capability to {@link Entity} objects
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param capabilityFactory capability factory called when attaching to an object
     * @param respawnStrategy   strategy to use when returning from the end dimension or after dying
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    public <C extends CapabilityComponent> CapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, PlayerRespawnStrategy respawnStrategy) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, componentKey -> o -> {
            if (o instanceof EntityComponentFactoryRegistry registry) registry.registerForPlayers(componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)), STRATEGY_CONVERTER_MAP.get(respawnStrategy));
        });
    }

//    /**
//     * register capability to {@link BlockEntity} objects
//     * @param capabilityKey path for internal name of this capability, will be used for serialization
//     * @param capabilityType interface for this capability
//     * @param capabilityFactory capability factory called when attaching to an object
//     * @param blockEntityType   block entity class to match
//     * @return capability instance from capability manager
//     * @param <T> block entity type
//     * @param <C> capability type
//     */
//    public <T extends BlockEntity, C extends CapabilityComponent> CapabilityKey<C> registerBlockEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<T> blockEntityType) {
//        return this.registerCapability(BlockEntity.class, capabilityKey, capabilityType, componentKey -> o -> {
//            if (o instanceof BlockComponentFactoryRegistry registry) registry.registerFor(blockEntityType, componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)));
//        });
//    }
//
//    /**
//     * register capability to {@link LevelChunk} objects
//     * @param capabilityKey path for internal name of this capability, will be used for serialization
//     * @param capabilityType interface for this capability
//     * @param capabilityFactory capability factory called when attaching to an object
//     * @param <C> capability type
//     * @return capability instance from capability manager
//     */
//    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelChunkCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory) {
//        return this.registerCapability(LevelChunk.class, capabilityKey, capabilityType, componentKey -> o -> {
//            if (o instanceof ChunkComponentFactoryRegistry registry) registry.register(componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)));
//        });
//    }
//
//    /**
//     * register capability to {@link Level} objects
//     * @param capabilityKey path for internal name of this capability, will be used for serialization
//     * @param capabilityType interface for this capability
//     * @param capabilityFactory capability factory called when attaching to an object
//     * @param <C> capability type
//     * @return capability instance from capability manager
//     */
//    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory) {
//        return this.registerCapability(Level.class, capabilityKey, capabilityType, componentKey -> o -> {
//            if (o instanceof WorldComponentFactoryRegistry registry) registry.register(componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)));
//        });
//    }

    /**
     * register capabilities for a given object type
     * @param objectType type of object to attach to, only works for generic supertypes
     * @param capabilityKey path for internal name of this capability, will be used for serialization
     * @param capabilityType interface for this capability
     * @param factoryRegistration capability factory
     * @param <C> capability type
     * @return capability instance from capability manager
     */
    private <C extends CapabilityComponent> CapabilityKey<C> registerCapability(Class<?> objectType, String capabilityKey, Class<C> capabilityType, Function<ComponentKey<ComponentHolder>, Consumer<Object>> factoryRegistration) {
        final ComponentKey<ComponentHolder> componentKey = ComponentRegistryV3.INSTANCE.getOrCreate(new ResourceLocation(this.namespace, capabilityKey), ComponentHolder.class);
        this.typeToRegistration.put(objectType, factoryRegistration.apply(componentKey));
        return new FabricCapabilityKey<>(componentKey, capabilityType);
    }

//    @Override
//    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
//        registerComponentFactories(ItemStack.class, registry);
//    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registerComponentFactories(Entity.class, registry);
    }

//    @Override
//    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
//        registerComponentFactories(BlockEntity.class, registry);
//    }
//
//    @Override
//    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
//        registerComponentFactories(LevelChunk.class, registry);
//    }
//
//    @Override
//    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
//        registerComponentFactories(Level.class, registry);
//    }

    /**
     * register for all CapabilityController's, static to not confuse with actual instance as they're separate kinda
     * the instance this is called on is invoked by cardinal components, all other instances are created by mods themselves and need to be called upon here via {@link #MOD_TO_CAPABILITIES}
     * @param baseType clazz type in map (based on Forge's default capability providers)
     * @param registry component factory registry, needs to match precisely what's been used during registration as there's an unchecked cast
     * @param <T> the component factory registry type
     */
    private static <T> void registerComponentFactories(Class<?> baseType, T registry) {
        for (FabricCapabilityController controller : MOD_TO_CAPABILITIES.values()) {
            for (Consumer<Object> factoryRegistration : controller.typeToRegistration.get(baseType)) {
                factoryRegistration.accept(registry);
            }
        }
    }

    /**
     * creates a new capability controller for <code>namespace</code> or returns an existing one
     * @param namespace namespace used for registration
     * @return new mod specific capability controller
     */
    public static synchronized FabricCapabilityController of(String namespace) {
        return MOD_TO_CAPABILITIES.computeIfAbsent(namespace, key -> {
            final FabricCapabilityController manager = new FabricCapabilityController(namespace);
            PuzzlesLib.LOGGER.info("Creating capability controller for mod id {}", namespace);
            return manager;
        });
    }
}
