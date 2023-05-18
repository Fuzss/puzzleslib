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
import net.minecraft.world.level.chunk.ChunkAccess;
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
import java.util.function.Function;
import java.util.function.Predicate;

public final class ForgeCapabilityController implements CapabilityController {
    private final String namespace;
    private final Multimap<Class<?>, ResourceLocation> providerClazzToIds = HashMultimap.create();
    private final Map<ResourceLocation, CapabilityData<?, ?>> idToCapabilityData = Maps.newHashMap();

    public ForgeCapabilityController(String namespace) {
        this.namespace = namespace;
        // for registering capabilities
        ModContainerHelper.findModEventBus(namespace).ifPresent(eventBus -> {
            eventBus.addListener(this::onRegisterCapabilities);
        });
        // for attaching capabilities via AttachCapabilitiesEvent, this is the only method that supports using
        // a wildcard for the generic event, allowing to listen to all subtypes simultaneously
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public <T extends Entity, C extends CapabilityComponent> CapabilityKey<C> registerEntityCapability(String capabilityKey, Class<C> capabilityType, Function<T, C> capabilityFactory, Class<T> entityType) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, capabilityFactory, entityType::isInstance);
    }

    @Override
    public <C extends CapabilityComponent> PlayerCapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, Function<Player, C> capabilityFactory, PlayerRespawnStrategy respawnStrategy) {
        return this.registerCapability(Entity.class, capabilityKey, capabilityType, capabilityFactory, Player.class::isInstance, ForgePlayerCapabilityKey<C>::new).setRespawnStrategy(respawnStrategy);
    }

    @Override
    public <C extends CapabilityComponent> PlayerCapabilityKey<C> registerPlayerCapability(String capabilityKey, Class<C> capabilityType, Function<Player, C> capabilityFactory, PlayerRespawnStrategy respawnStrategy, SyncStrategy syncStrategy) {
        return ((ForgePlayerCapabilityKey<C>) this.registerPlayerCapability(capabilityKey, capabilityType, capabilityFactory, respawnStrategy)).setSyncStrategy(syncStrategy);
    }

    @Override
    public <T extends BlockEntity, C extends CapabilityComponent> CapabilityKey<C> registerBlockEntityCapability(String capabilityKey, Class<C> capabilityType, Function<T, C> capabilityFactory, Class<T> blockEntityType) {
        return this.registerCapability(BlockEntity.class, capabilityKey, capabilityType, capabilityFactory, blockEntityType::isInstance);
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelChunkCapability(String capabilityKey, Class<C> capabilityType, Function<ChunkAccess, C> capabilityFactory) {
        return this.registerCapability(LevelChunk.class, capabilityKey, capabilityType, capabilityFactory, o -> true);
    }

    @Override
    public <C extends CapabilityComponent> CapabilityKey<C> registerLevelCapability(String capabilityKey, Class<C> capabilityType, Function<Level, C> capabilityFactory) {
        return this.registerCapability(Level.class, capabilityKey, capabilityType, capabilityFactory, o -> true);
    }

    private <T, C extends CapabilityComponent> CapabilityKey<C> registerCapability(Class<? extends ICapabilityProvider> providerType, String capabilityKey, Class<C> capabilityType, Function<T, C> capabilityFactory, Predicate<Object> filter) {
        return this.registerCapability(providerType, capabilityKey, capabilityType, capabilityFactory, filter, ForgeCapabilityKey<C>::new);
    }

    private <T, C1 extends CapabilityComponent, C2 extends CapabilityKey<C1>> C2 registerCapability(Class<? extends ICapabilityProvider> providerType, String capabilityKey, Class<C1> capabilityType, Function<T, C1> capabilityFactory, Predicate<Object> filter, ForgeCapabilityKey.ForgeCapabilityKeyFactory<C1, C2> capabilityKeyFactory) {
        ResourceLocation key = new ResourceLocation(this.namespace, capabilityKey);
        this.providerClazzToIds.put(providerType, key);
        return capabilityKeyFactory.apply(key, capabilityType, token -> {
            final Capability<C1> capability = CapabilityManager.get(token);
            this.idToCapabilityData.put(key, new CapabilityData<T, C1>(key, capabilityType, o -> new CapabilityHolder<>(capability, capabilityFactory.apply(o)), filter));
            return capability;
        });
    }

    private void onRegisterCapabilities(final RegisterCapabilitiesEvent evt) {
        for (CapabilityData<?, ?> data : this.toCapabilityData(this.providerClazzToIds.values())) {
            evt.register(data.capabilityType());
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public <T> void onAttachCapabilities(final AttachCapabilitiesEvent<?> evt) {
        Class<?> providerClazz = (Class<?>) evt.getGenericType();
        for (CapabilityData<?, ?> data : this.toCapabilityData(this.providerClazzToIds.get(providerClazz))) {
            if (data.filter().test(evt.getObject())) {
                evt.addCapability(data.capabilityKey(), ((CapabilityData<T, ?>) data).capabilityFactory().apply((T) evt.getObject()));
            }
        }
    }

    private Collection<? extends CapabilityData<?, ?>> toCapabilityData(Collection<ResourceLocation> keys) {
        return keys.stream().map(key -> {
            CapabilityData<?, ?> data = this.idToCapabilityData.get(key);
            Objects.requireNonNull(data, "No valid capability implementation registered for %s".formatted(key));
            return data;
        }).toList();
    }

    private record CapabilityData<T, C extends CapabilityComponent>(ResourceLocation capabilityKey, Class<C> capabilityType, Function<T, CapabilityHolder<C>> capabilityFactory, Predicate<Object> filter) {

    }
}
