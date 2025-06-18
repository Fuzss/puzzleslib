package fuzs.puzzleslib.fabric.impl.attachment.builder;

import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import fuzs.puzzleslib.impl.attachment.ClientboundEntityDataAttachmentMessage;
import fuzs.puzzleslib.impl.attachment.builder.EntityDataAttachmentBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class FabricEntityDataAttachmentBuilder<V> extends FabricDataAttachmentBuilder<Entity, V> implements EntityDataAttachmentBuilder<V> {
    @Nullable
    private StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec;
    @Nullable
    private Function<Entity, PlayerSet> synchronizationTargets;
    private boolean copyOnDeath;

    public FabricEntityDataAttachmentBuilder() {
        super(Entity::registryAccess);
    }

    @Override
    @Nullable
    public BiConsumer<Entity, V> getSynchronizer(ResourceLocation resourceLocation, AttachmentTypeAdapter<Entity, V> attachmentType) {
        return this.getSynchronizer(attachmentType, this.streamCodec, this.synchronizationTargets);
    }

    @Override
    public void registerPayloadHandlers(AttachmentTypeAdapter<Entity, V> attachmentType, CustomPacketPayload.Type<ClientboundEntityDataAttachmentMessage<V>> payloadType, @Nullable StreamCodec<? super RegistryFriendlyByteBuf, V> streamCodec) {
        StreamCodec<? super RegistryFriendlyByteBuf, ClientboundEntityDataAttachmentMessage<V>> messageStreamCodec = ClientboundEntityDataAttachmentMessage.streamCodec(
                attachmentType,
                payloadType,
                this.streamCodec);
        FabricProxy.get()
                .createPayloadTypesContext(attachmentType.resourceLocation().getNamespace())
                .playToClient(payloadType, messageStreamCodec);
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
    public DataAttachmentRegistry.EntityBuilder<V> persistent(MapCodec<V> codec) {
        return (DataAttachmentRegistry.EntityBuilder<V>) super.persistent(codec);
    }

    @Override
    void configureBuilder(AttachmentRegistry.Builder<V> builder) {
        super.configureBuilder(builder);
        if (this.copyOnDeath) {
            // Fabric does not need this check, but NeoForge does, so implement it for both
            Objects.requireNonNull(this.codec, "codec is null");
            builder.copyOnDeath();
        }
    }
}
