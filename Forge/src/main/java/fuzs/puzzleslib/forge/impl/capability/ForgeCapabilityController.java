package fuzs.puzzleslib.forge.impl.capability;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.*;
import fuzs.puzzleslib.api.core.v1.utility.ReflectionHelper;
import fuzs.puzzleslib.forge.api.core.v1.ForgeModContainerHelper;
import fuzs.puzzleslib.forge.impl.capability.data.*;
import fuzs.puzzleslib.impl.capability.GlobalCapabilityRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ForgeCapabilityController implements CapabilityController {
    private final Multimap<Class<?>, CapabilityData<?, ?>> capabilityData = Multimaps.newListMultimap(Maps.newIdentityHashMap(), Lists::newArrayList);
    private final String modId;

    public ForgeCapabilityController(String modId) {
        this.modId = modId;
        ForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent(eventBus -> {
            eventBus.addListener(this::onRegisterCapabilities);
        });
    }

    {
        // for attaching capabilities via AttachCapabilitiesEvent, this is the only method that supports using
        // a wildcard for the generic event, allowing to listen to all subtypes simultaneously
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public <T extends Entity, C extends CapabilityComponent<T>> EntityCapabilityKey.Mutable<T, C> registerEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, identifier, capabilityType, capabilityFactory, entityType::isInstance, (ForgeCapabilityKey.ForgeCapabilityKeyFactory<T, C, ForgeEntityCapabilityKey<T, C>>) ForgeEntityCapabilityKey::new);
    }

    @Override
    public <T extends BlockEntity, C extends CapabilityComponent<T>> BlockEntityCapabilityKey<T, C> registerBlockEntityCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Class<T> blockEntityType) {
        return this.registerCapability(BlockEntity.class, identifier, capabilityType, capabilityFactory, blockEntityType::isInstance, (ForgeCapabilityKey.ForgeCapabilityKeyFactory<T, C, ForgeBlockEntityCapabilityKey<T, C>>) ForgeBlockEntityCapabilityKey::new);
    }

    @Override
    public <C extends CapabilityComponent<LevelChunk>> LevelChunkCapabilityKey<C> registerLevelChunkCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory) {
        return this.registerCapability(LevelChunk.class, identifier, capabilityType, capabilityFactory, (ForgeCapabilityKey.ForgeCapabilityKeyFactory<LevelChunk, C, ForgeLevelChunkCapabilityKey<C>>) ForgeLevelChunkCapabilityKey::new);
    }

    @Override
    public <C extends CapabilityComponent<Level>> LevelCapabilityKey<C> registerLevelCapability(String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory) {
        return this.registerCapability(Level.class, identifier, capabilityType, capabilityFactory, (ForgeCapabilityKey.ForgeCapabilityKeyFactory<Level, C, ForgeLevelCapabilityKey<C>>) ForgeLevelCapabilityKey::new);
    }

    private <T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> K registerCapability(Class<? extends ICapabilityProvider> holderType, String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, ForgeCapabilityKey.ForgeCapabilityKeyFactory<T, C, K> capabilityKeyFactory) {
        return this.registerCapability(holderType, identifier, capabilityType, capabilityFactory, holderType::isInstance, capabilityKeyFactory);
    }

    @SuppressWarnings("unchecked")
    private <T, C extends CapabilityComponent<T>, K extends CapabilityKey<T, C>> K registerCapability(Class<? extends ICapabilityProvider> holderType, String identifier, Class<C> capabilityType, Supplier<C> capabilityFactory, Predicate<Object> filter, ForgeCapabilityKey.ForgeCapabilityKeyFactory<T, C, K> capabilityKeyFactory) {
        GlobalCapabilityRegister.testHolderType(holderType);
        ResourceLocation capabilityName = new ResourceLocation(this.modId, identifier);
        Capability<C> capability = this.getCapability(capabilityType);
        Objects.requireNonNull(capability, "capability is null");
        K capabilityKey = capabilityKeyFactory.apply(capabilityName, capability, filter, capabilityFactory);
        Function<T, CapabilityAdapter<T, C>> capabilityAdapterFactory = (T t) -> {
            C capabilityComponent = capabilityFactory.get();
            Objects.requireNonNull(capabilityComponent, "capability component is null");
            capabilityComponent.initialize((CapabilityKey<T, CapabilityComponent<T>>) capabilityKey, t);
            return new CapabilityAdapter<>(capability, capabilityComponent);
        };
        CapabilityData<T, C> capabilityData = new CapabilityData<>(capabilityName, capabilityType, filter, capabilityAdapterFactory);
        this.capabilityData.put(holderType, capabilityData);
        return capabilityKey;
    }

    @SuppressWarnings("unchecked")
    private <T, C extends CapabilityComponent<T>> Capability<C> getCapability(Class<C> capabilityType) {
        try {
            Method method = ReflectionHelper.findMethod(CapabilityManager.class, "get", String.class, boolean.class);
            Objects.requireNonNull(method, "CapabilityManager::get is null");
            String internalName = Type.getInternalName(capabilityType);
            return (Capability<C>) MethodHandles.lookup().unreflect(method).invoke(CapabilityManager.INSTANCE, internalName, false);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private void onRegisterCapabilities(final RegisterCapabilitiesEvent evt) {
        for (CapabilityData<?, ?> data : this.capabilityData.values()) {
            evt.register(data.type());
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public <T> void onAttachCapabilities(final AttachCapabilitiesEvent<?> evt) {
        Class<?> holderType = (Class<?>) evt.getGenericType();
        for (CapabilityData<?, ?> data : this.capabilityData.get(holderType)) {
            if (data.test(evt.getObject())) {
                evt.addCapability(data.identifier(), ((CapabilityData<T, ?>) data).apply((T) evt.getObject()));
            }
        }
    }

    private record CapabilityData<T, C extends CapabilityComponent<T>>(ResourceLocation identifier,
                                                                       Class<C> type,
                                                                       Predicate<Object> filter,
                                                                       Function<T, CapabilityAdapter<T, C>> factory) {

        public CapabilityAdapter<T, C> apply(T t) {
            return this.factory.apply(t);
        }

        public boolean test(@Nullable Object o) {
            return o != null && this.filter.test(o);
        }
    }
}
