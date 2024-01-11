package fuzs.puzzleslib.fabric.impl.capability;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.capability.v2.data.*;
import fuzs.puzzleslib.fabric.api.capability.v2.initializer.BlockComponentInitializerImpl;
import fuzs.puzzleslib.fabric.api.capability.v2.initializer.ChunkComponentInitializerImpl;
import fuzs.puzzleslib.fabric.api.capability.v2.initializer.EntityComponentInitializerImpl;
import fuzs.puzzleslib.fabric.api.capability.v2.initializer.WorldComponentInitializerImpl;
import fuzs.puzzleslib.fabric.impl.capability.data.ComponentHolder;
import fuzs.puzzleslib.fabric.impl.capability.data.FabricCapabilityKey;
import fuzs.puzzleslib.fabric.impl.capability.data.FabricPlayerCapabilityKey;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public final class FabricCapabilityController implements CapabilityController {
    private final String namespace;
    private final Multimap<Class<?>, Consumer<Object>> capabilityTypes = Multimaps.newListMultimap(Maps.newIdentityHashMap(), Lists::newArrayList);

    public FabricCapabilityController(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public <T extends Entity, C extends CapabilityComponent> CapabilityKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, Function<T, C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, capabilityFactory, EntityComponentInitializerImpl.getEntityFactory(entityType));
    }

    @Override
    public <C extends CapabilityComponent> PlayerCapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, Function<Player, C> capabilityFactory, PlayerRespawnCopyStrategy respawnStrategy) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, capabilityFactory, EntityComponentInitializerImpl.getPlayerFactory(respawnStrategy), FabricPlayerCapabilityKey<C>::new);
    }

    @Override
    public <C extends CapabilityComponent> PlayerCapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, Function<Player, C> capabilityFactory, PlayerRespawnCopyStrategy respawnStrategy, SyncStrategy syncStrategy) {
        return ((FabricPlayerCapabilityKey<C>) this.registerPlayerCapability(capabilityKey, capabilityType, capabilityFactory, respawnStrategy)).setSyncStrategy(syncStrategy);
    }

    @Override
    public <T extends BlockEntity, C extends CapabilityComponent> CapabilityKey<C> registerBlockEntityCapability(String capabilityKey, Class<C> capabilityType, Function<T, C> capabilityFactory, Class<T> blockEntityType) {
        return this.registerCapability(BlockEntity.class, capabilityKey, capabilityType, capabilityFactory, BlockComponentInitializerImpl.getBlockEntityFactory(blockEntityType));
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelChunkCapability(String capabilityKey, Class<C> capabilityType, Function<ChunkAccess, C> capabilityFactory) {
        return this.registerCapability(LevelChunk.class, capabilityKey, capabilityType, capabilityFactory, ChunkComponentInitializerImpl.getLevelChunkFactory());
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelCapability(String capabilityKey, Class<C> capabilityType, Function<Level, C> capabilityFactory) {
        return this.registerCapability(Level.class, capabilityKey, capabilityType, capabilityFactory, WorldComponentInitializerImpl.getLevelFactory());
    }

    private <T, C extends CapabilityComponent> CapabilityKey<C> registerCapability(Class<?> objectType, String capabilityKey, Class<C> capabilityType, Function<T, C> capabilityFactory, ComponentFactoryRegistry<T> capabilityRegistry) {
        return this.registerCapability(objectType, capabilityKey, capabilityType, capabilityFactory, capabilityRegistry, FabricCapabilityKey<C>::new);
    }

    private <T, C1 extends CapabilityComponent, C2 extends CapabilityKey<C1>> C2 registerCapability(Class<?> objectType, String capabilityKey, Class<C1> capabilityType, Function<T, C1> capabilityFactory, ComponentFactoryRegistry<T> capabilityRegistry, FabricCapabilityKey.FabricCapabilityKeyFactory<C1, C2> capabilityKeyFactory) {
        if (!VALID_CAPABILITY_TYPES.contains(objectType)) {
            throw new IllegalArgumentException(objectType + " is an invalid type");
        }
        final ComponentKey<ComponentHolder> componentKey = ComponentRegistryV3.INSTANCE.getOrCreate(new ResourceLocation(this.namespace, capabilityKey), ComponentHolder.class);
        this.capabilityTypes.put(objectType, o -> capabilityRegistry.accept(o, componentKey, o1 -> new ComponentHolder(capabilityFactory.apply(o1))));
        return capabilityKeyFactory.apply(componentKey, capabilityType);
    }

    public static <T> void registerComponentFactories(Class<?> baseType, T registry) {
        Collection<FabricCapabilityController> controllers = ModContext.getCapabilityControllers().map(FabricCapabilityController.class::cast).toList();
        for (FabricCapabilityController controller : controllers) {
            for (Consumer<Object> factoryRegistration : controller.capabilityTypes.get(baseType)) {
                factoryRegistration.accept(registry);
            }
            controller.capabilityTypes.get(baseType).clear();
        }
    }
}
