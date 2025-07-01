package fuzs.puzzleslib.neoforge.impl.attachment.builder;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import fuzs.puzzleslib.impl.attachment.ClientboundEntityDataAttachmentMessage;
import fuzs.puzzleslib.impl.attachment.builder.EntityDataAttachmentBuilder;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.impl.core.NeoForgeProxy;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class NeoForgeEntityDataAttachmentBuilder<V> extends NeoForgeDataAttachmentBuilder<Entity, V> implements EntityDataAttachmentBuilder<V> {
    @Nullable
    private StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec;
    @Nullable
    private Function<Entity, PlayerSet> synchronizationTargets;
    private boolean copyOnDeath;

    public NeoForgeEntityDataAttachmentBuilder() {
        super(Entity::registryAccess);
    }

    @Override
    @Nullable
    public BiConsumer<Entity, V> getSynchronizer(ResourceLocation resourceLocation, AttachmentTypeAdapter<Entity, V> attachmentType) {
        return this.getSynchronizer(attachmentType, this.streamCodec, this.synchronizationTargets);
    }

    @Override
    public void registerPayloadHandlers(AttachmentTypeAdapter<Entity, V> attachmentType, CustomPacketPayload.Type<ClientboundEntityDataAttachmentMessage<V>> payloadType, @Nullable StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec) {
        NeoForgeModContainerHelper.getOptionalModEventBus(attachmentType.resourceLocation().getNamespace())
                .ifPresent((IEventBus eventBus) -> {
                    eventBus.addListener((final RegisterPayloadHandlersEvent evt) -> {
                        StreamCodec<? super RegistryFriendlyByteBuf, ClientboundEntityDataAttachmentMessage<V>> messageStreamCodec = ClientboundEntityDataAttachmentMessage.streamCodec(
                                attachmentType,
                                payloadType,
                                this.streamCodec);
                        NeoForgeProxy.get()
                                .createPayloadTypesContext(attachmentType.resourceLocation().getNamespace(), evt)
                                .playToClient(payloadType, messageStreamCodec);
                    });
                });
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<V> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec, @Nullable Function<Entity, PlayerSet> synchronizationTargets) {
        Objects.requireNonNull(streamCodec, "stream codec is null");
        this.streamCodec = streamCodec;
        this.synchronizationTargets = synchronizationTargets;
        return this;
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<V> copyOnDeath() {
        this.copyOnDeath = true;
        return this;
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<V> defaultValue(V defaultValue) {
        return EntityDataAttachmentBuilder.super.defaultValue(defaultValue);
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<V> defaultValue(Function<RegistryAccess, V> defaultValueProvider) {
        return EntityDataAttachmentBuilder.super.defaultValue(defaultValueProvider);
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<V> defaultValue(Predicate<Entity> defaultFilter, Function<RegistryAccess, V> defaultValueProvider) {
        Objects.requireNonNull(defaultFilter, "default filter is null");
        Objects.requireNonNull(defaultValueProvider, "default value provider is null");
        this.defaultValues.put(defaultFilter, defaultValueProvider);
        return this;
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<V> persistent(Codec<V> codec) {
        return (DataAttachmentRegistry.EntityBuilder<V>) super.persistent(codec);
    }

    @Override
    void configureBuilder(ResourceLocation resourceLocation, AttachmentType.Builder<V> builder) {
        super.configureBuilder(resourceLocation, builder);
        if (this.copyOnDeath) {
            Objects.requireNonNull(this.codec, "codec is null");
            builder.copyOnDeath();
        }
    }
}
