package fuzs.puzzleslib.fabric.impl.capability;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.onyxstudios.cca.api.v3.component.ComponentAccess;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.*;
import fuzs.puzzleslib.fabric.api.capability.v3.initializer.BlockComponentInitializerImpl;
import fuzs.puzzleslib.fabric.api.capability.v3.initializer.ChunkComponentInitializerImpl;
import fuzs.puzzleslib.fabric.api.capability.v3.initializer.EntityComponentInitializerImpl;
import fuzs.puzzleslib.fabric.api.capability.v3.initializer.WorldComponentInitializerImpl;
import fuzs.puzzleslib.fabric.impl.capability.data.*;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class FabricCapabilityController implements CapabilityController {
    private final Map<Class<? extends ComponentAccess>, Queue<Consumer<Object>>> componentRegistrars = Maps.newIdentityHashMap();
    private final String modId;

    public FabricCapabilityController(String modId) {
        this.modId = modId;
    }

    @Override
    public <T extends Entity, C extends CapabilityComponent<T>> EntityCapabilityKey.Mutable<T, C> registerEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, identifier, capabilityFactory, EntityComponentInitializerImpl.getEntityFactory(entityType), (FabricCapabilityKey.Factory<T, C, FabricEntityCapabilityKey<T, C>>) FabricEntityCapabilityKey::new);
    }

    @Override
    public <T extends BlockEntity, C extends CapabilityComponent<T>> BlockEntityCapabilityKey<T, C> registerBlockEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> blockEntityType) {
        return this.registerCapability(BlockEntity.class, identifier, capabilityFactory, BlockComponentInitializerImpl.getBlockEntityFactory(blockEntityType), (FabricCapabilityKey.Factory<T, C, FabricBlockEntityCapabilityKey<T, C>>) FabricBlockEntityCapabilityKey::new);
    }

    @Override
    public <C extends CapabilityComponent<LevelChunk>> LevelChunkCapabilityKey<C> registerLevelChunkCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory) {
        return this.registerCapability(LevelChunk.class, identifier, capabilityFactory, ChunkComponentInitializerImpl.getLevelChunkFactory(), (FabricCapabilityKey.Factory<LevelChunk, C, FabricLevelChunkCapabilityKey<C>>) FabricLevelChunkCapabilityKey::new);
    }

    @Override
    public <C extends CapabilityComponent<Level>> LevelCapabilityKey<C> registerLevelCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory) {
        return this.registerCapability(Level.class, identifier, capabilityFactory, WorldComponentInitializerImpl.getLevelFactory(), (FabricCapabilityKey.Factory<Level, C, FabricLevelCapabilityKey<C>>) FabricLevelCapabilityKey::new);
    }

    @SuppressWarnings("unchecked")
    private <T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> K registerCapability(Class<? extends ComponentAccess> holderType, String identifier, Supplier<C> capabilityFactory, ComponentFactoryRegistrar<T, C> capabilityRegistrar, FabricCapabilityKey.Factory<T, C, K> capabilityKeyFactory) {
        GlobalCapabilityRegister.testHolderType(holderType);
        ResourceLocation capabilityName = new ResourceLocation(this.modId, identifier);
        ComponentKey<ComponentAdapter<T, C>> componentKey = (ComponentKey<ComponentAdapter<T, C>>) (ComponentKey<?>) ComponentRegistryV3.INSTANCE.getOrCreate(capabilityName, ComponentAdapter.class);
        Object[] capabilityKey = new Object[1];
        this.componentRegistrars.computeIfAbsent(holderType, $ -> Lists.newLinkedList()).offer((Object o) -> {
            capabilityRegistrar.accept(o, componentKey, (T t) -> {
                C capabilityComponent = capabilityFactory.get();
                Objects.requireNonNull(capabilityComponent, "capability component is null");
                capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) capabilityKey[0], t);
                return new ComponentAdapter<>(capabilityComponent);
            });
        });
        return (K) (capabilityKey[0] = capabilityKeyFactory.apply(componentKey));
    }

    public static <T> void registerComponentFactories(Class<? extends ComponentAccess> holderType, T registry) {
        Collection<FabricCapabilityController> controllers = ModContext.getCapabilityControllers().map(FabricCapabilityController.class::cast).toList();
        for (FabricCapabilityController controller : controllers) {
            Queue<Consumer<Object>> queue = controller.componentRegistrars.get(holderType);
            while (queue != null && !queue.isEmpty()) {
                queue.poll().accept(registry);
            }
        }
    }
}
