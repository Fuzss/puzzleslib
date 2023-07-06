package fuzs.puzzleslib.capability;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.capability.data.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * implementation of {@link CapabilityController} for Fabric
 *
 * <p>this is not good right now, as Puzzles doesn't depend on Cardinal Components itself and this is only loaded when it is present via other means (another mod)
 * since the API is modular not all modules might be present though, which will crash this
 * so we once again only use the entity module, since this has been working fine in the past as it's the module most mods use
 * needs a new approach in the future though...
 */
public class FabricCapabilityController implements CapabilityController, EntityComponentInitializer, BlockComponentInitializer, ChunkComponentInitializer, WorldComponentInitializer {
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
    private final Multimap<Class<?>, Consumer<Object>> providerClazzToRegistration = ArrayListMultimap.create();

    /**
     * private constructor
     *
     * @param namespace     namespace for this instance
     */
    private FabricCapabilityController(String namespace) {
        this.namespace = namespace;
    }

    /**
     * invoked by cardinal components entry point via reflection
     * this should better be a separate class, but kept in here to remain consistent with Forge and doesn't matter anyway
     */
    @Deprecated
    public FabricCapabilityController() {
        this("_internal");
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerItemCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Predicate<Item> itemFilter) {
        throw new RuntimeException("Registering item stack capabilities is currently not supported on Fabric, use ItemStack#tag to attach additional data");
    }

    @Override
    public <T extends Entity, C extends CapabilityComponent> CapabilityKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, componentKey -> o -> {
            if (o instanceof EntityComponentFactoryRegistry registry) registry.registerFor(entityType, componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)));
        });
    }

    @Override
    public <C extends CapabilityComponent> PlayerCapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, PlayerRespawnStrategy respawnStrategy) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, componentKey -> o -> {
            if (o instanceof EntityComponentFactoryRegistry registry) registry.registerForPlayers(componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)), STRATEGY_CONVERTER_MAP.get(respawnStrategy));
        }, FabricPlayerCapabilityKey<C>::new);
    }

    @Override
    public <C extends CapabilityComponent> PlayerCapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, PlayerRespawnStrategy respawnStrategy, SyncStrategy<?> syncStrategy) {
        return ((FabricPlayerCapabilityKey<C>) this.registerPlayerCapability(capabilityKey, capabilityType, capabilityFactory, respawnStrategy)).setSyncStrategy(syncStrategy);
    }

    @Override
    public <T extends BlockEntity, C extends CapabilityComponent> CapabilityKey<C> registerBlockEntityCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory, Class<T> blockEntityType) {
        return this.registerCapability(BlockEntity.class, capabilityKey, capabilityType, componentKey -> o -> {
            if (o instanceof BlockComponentFactoryRegistry registry) registry.registerFor(blockEntityType, componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)));
        });
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelChunkCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory) {
        return this.registerCapability(LevelChunk.class, capabilityKey, capabilityType, componentKey -> o -> {
            if (o instanceof ChunkComponentFactoryRegistry registry) registry.register(componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)));
        });
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelCapability(String capabilityKey, Class<C> capabilityType, CapabilityFactory<C> capabilityFactory) {
        return this.registerCapability(Level.class, capabilityKey, capabilityType, componentKey -> o -> {
            if (o instanceof WorldComponentFactoryRegistry registry) registry.register(componentKey, o1 -> new ComponentHolder(capabilityFactory.createComponent(o1)));
        });
    }

    /**
     * register capabilities for a given object type
     *
     * @param objectType            type of object to attach to, only works for generic supertypes
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param factoryRegistration   capability factory
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    private <C extends CapabilityComponent> CapabilityKey<C> registerCapability(Class<?> objectType, String capabilityKey, Class<C> capabilityType, Function<ComponentKey<ComponentHolder>, Consumer<Object>> factoryRegistration) {
        return this.registerCapability(objectType, capabilityKey, capabilityType, factoryRegistration, FabricCapabilityKey<C>::new);
    }

    /**
     * register capabilities for a given object type
     *
     * @param objectType            type of object to attach to, only works for generic supertypes
     * @param capabilityKey         path for internal name of this capability, will be used for serialization
     * @param capabilityType        interface for this capability
     * @param factoryRegistration   capability factory
     * @param capabilityKeyFactory  factory for the capability key implementation, required by players
     * @param <C>                   capability type
     * @return                      capability instance from capability manager
     */
    private <C extends CapabilityComponent, T extends CapabilityKey<C>> T registerCapability(Class<?> objectType, String capabilityKey, Class<C> capabilityType, Function<ComponentKey<ComponentHolder>, Consumer<Object>> factoryRegistration, FabricCapabilityKey.FabricCapabilityKeyFactory<C, T> capabilityKeyFactory) {
        final ComponentKey<ComponentHolder> componentKey = ComponentRegistryV3.INSTANCE.getOrCreate(new ResourceLocation(this.namespace, capabilityKey), ComponentHolder.class);
        this.providerClazzToRegistration.put(objectType, factoryRegistration.apply(componentKey));
        return capabilityKeyFactory.apply(componentKey, capabilityType);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registerComponentFactories(Entity.class, registry);
    }

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        registerComponentFactories(BlockEntity.class, registry);
    }

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        registerComponentFactories(LevelChunk.class, registry);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registerComponentFactories(Level.class, registry);
    }

    /**
     * register for all CapabilityController's, static to not confuse with actual instance as they're separate kinda
     * the instance this is called on is invoked by cardinal components, all other instances are created by mods themselves and need to be called upon here via {@link #MOD_TO_CAPABILITIES}
     *
     * @param baseType  clazz type in map (based on Forge's default capability providers)
     * @param registry  component factory registry, needs to match precisely what's been used during registration as there's an unchecked cast
     * @param <T>       the component factory registry type
     */
    private static <T> void registerComponentFactories(Class<?> baseType, T registry) {
        for (FabricCapabilityController controller : MOD_TO_CAPABILITIES.values()) {
            for (Consumer<Object> factoryRegistration : controller.providerClazzToRegistration.get(baseType)) {
                factoryRegistration.accept(registry);
            }
        }
    }

    /**
     * creates a new capability controller for <code>namespace</code> or returns an existing one
     *
     * @param namespace     namespace used for registration
     * @return              the mod specific capability controller
     */
    public static synchronized CapabilityController of(String namespace) {
        return MOD_TO_CAPABILITIES.computeIfAbsent(namespace, key -> {
            final FabricCapabilityController controller = new FabricCapabilityController(namespace);
            PuzzlesLib.LOGGER.info("Creating capability controller for mod id {}", namespace);
            return controller;
        });
    }
}
