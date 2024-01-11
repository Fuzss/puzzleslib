package fuzs.puzzleslib.capability;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import dev.onyxstudios.cca.api.v3.component.ComponentFactory;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.capability.data.CapabilityComponent;
import fuzs.puzzleslib.capability.data.PlayerRespawnStrategy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * helper object for registering and attaching mod capabilities, needs to be extended by every mod individually
 * this basically is the same as {@link fuzs.puzzleslib.registry.RegistryManager}
 */
//public class CapabilityController implements ItemComponentInitializer, EntityComponentInitializer, BlockComponentInitializer, ChunkComponentInitializer, WorldComponentInitializer {
public class CapabilityController implements EntityComponentInitializer {
    /**
     * capability controllers are stored for each mod separately to avoid concurrency issues, might not be need though
     */
    private static final Map<String, CapabilityController> MOD_TO_CAPABILITIES = Maps.newConcurrentMap();

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
     * this should better be a separate class, but kept in here to remain consistent with Forge and doesn't matter anyways
     */
    @Deprecated
    public CapabilityController() {
        this("_internal");
    }

    /**
     * private constructor
     * @param namespace namespace for this instance
     */
    private CapabilityController(String namespace) {
        this.namespace = namespace;
    }

//    /**
//     * register capability to {@link ItemStack} objects
//     * @param capabilityKey path for internal name of this capability, will be used for serialization
//     * @param capabilityType interface for this capability
//     * @param capabilityFactory capability factory
//     * @param item item to apply to
//     * @param <C> capability type
//     * @return capability instance from capability manager
//     */
//    public <C extends ItemCapabilityComponent> ComponentKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, ComponentFactory<ItemStack, C> capabilityFactory, Item item) {
//        return this.registerCapability(ItemStack.class, capabilityKey, capabilityType, componentKey -> o -> {
//            if (o instanceof ItemComponentFactoryRegistry registry) registry.register(item, componentKey, capabilityFactory);
//        });
//    }
//
//    /**
//     * register capability to {@link ItemStack} objects
//     * @param capabilityKey path for internal name of this capability, will be used for serialization
//     * @param capabilityType interface for this capability
//     * @param capabilityFactory capability factory called when attaching to an object
//     * @param itemFilter filter for item, can be used for matching class and instanceof
//     * @param <C> capability type
//     * @return capability instance from capability manager
//     */
//    public <C extends ItemCapabilityComponent> ComponentKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, ComponentFactory<ItemStack, C> capabilityFactory, Predicate<Item> itemFilter) {
//        return this.registerCapability(ItemStack.class, capabilityKey, capabilityType, componentKey -> o -> {
//            if (o instanceof ItemComponentFactoryRegistry registry) registry.register(itemFilter, componentKey, capabilityFactory);
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
    public <T extends Entity, C extends CapabilityComponent> ComponentKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, ComponentFactory<T, C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, componentKey -> o -> {
            if (o instanceof EntityComponentFactoryRegistry registry) registry.registerFor(entityType, componentKey, capabilityFactory);
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
    public <C extends CapabilityComponent> ComponentKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, ComponentFactory<Player, C> capabilityFactory, PlayerRespawnStrategy respawnStrategy) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, componentKey -> o -> {
            if (o instanceof EntityComponentFactoryRegistry registry) registry.registerForPlayers(componentKey, capabilityFactory, respawnStrategy.toComponentStrategy());
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
//    public <T extends BlockEntity, C extends CapabilityComponent> ComponentKey<C> registerBlockEntityCapability(String capabilityKey, Class<C> capabilityType, ComponentFactory<T, C> capabilityFactory, Class<T> blockEntityType) {
//        return this.registerCapability(BlockEntity.class, capabilityKey, capabilityType, componentKey -> o -> {
//            if (o instanceof BlockComponentFactoryRegistry registry) registry.registerFor(blockEntityType, componentKey, capabilityFactory);
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
//    public <C extends CapabilityComponent> ComponentKey<C> registerLevelChunkCapability(String capabilityKey, Class<C> capabilityType, ComponentFactory<ChunkAccess, C> capabilityFactory) {
//        return this.registerCapability(LevelChunk.class, capabilityKey, capabilityType, componentKey -> o -> {
//            if (o instanceof ChunkComponentFactoryRegistry registry) registry.register(componentKey, capabilityFactory);
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
//    public <C extends CapabilityComponent> ComponentKey<C> registerLevelCapability(String capabilityKey, Class<C> capabilityType, ComponentFactory<Level, C> capabilityFactory) {
//        return this.registerCapability(Level.class, capabilityKey, capabilityType, componentKey -> o -> {
//            if (o instanceof WorldComponentFactoryRegistry registry) registry.register(componentKey, capabilityFactory);
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
    private <C extends CapabilityComponent> ComponentKey<C> registerCapability(Class<?> objectType, String capabilityKey, Class<C> capabilityType, Function<ComponentKey<C>, Consumer<Object>> factoryRegistration) {
        final ComponentKey<C> componentKey = ComponentRegistryV3.INSTANCE.getOrCreate(this.locate(capabilityKey), capabilityType);
        this.typeToRegistration.put(objectType, factoryRegistration.apply(componentKey));
        return componentKey;
    }

    /**
     * @param path path for location
     * @return resource location for {@link #namespace}
     */
    private ResourceLocation locate(String path) {
        if (StringUtils.isEmpty(path)) throw new IllegalArgumentException("Can't register object without name");
        return new ResourceLocation(this.namespace, path);
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
        for (CapabilityController controller : MOD_TO_CAPABILITIES.values()) {
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
    public static synchronized CapabilityController of(String namespace) {
        return MOD_TO_CAPABILITIES.computeIfAbsent(namespace, key -> {
            final CapabilityController manager = new CapabilityController(namespace);
            PuzzlesLib.LOGGER.info("Creating capability controller for mod id {}", namespace);
            return manager;
        });
    }
}
