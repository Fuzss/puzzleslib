package fuzs.puzzleslib.fabric.impl.attachment.builder;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import fuzs.puzzleslib.impl.attachment.AttachmentTypeAdapter;
import fuzs.puzzleslib.impl.attachment.ClientboundEntityDataAttachmentMessage;
import fuzs.puzzleslib.impl.attachment.builder.EntityDataAttachmentBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.player.LocalPlayer;
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

public final class FabricEntityDataAttachmentBuilder<A> extends FabricDataAttachmentBuilder<Entity, A> implements EntityDataAttachmentBuilder<A> {
    @Nullable
    private StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec;
    @Nullable
    private Function<Entity, PlayerSet> synchronizationTargets;
    private boolean copyOnDeath;

    @Override
    @Nullable
    public BiConsumer<Entity, A> getSynchronizer(ResourceLocation resourceLocation, AttachmentTypeAdapter<Entity, A> attachmentType) {
        return this.getSynchronizer(resourceLocation, attachmentType, this.streamCodec, this.synchronizationTargets);
    }

    @Override
    public void registerPayloadHandlers(ResourceLocation resourceLocation, AttachmentTypeAdapter<Entity, A> attachmentType, CustomPacketPayload.Type<ClientboundEntityDataAttachmentMessage<A>> type, @Nullable StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec) {
        StreamCodec<? super RegistryFriendlyByteBuf, ClientboundEntityDataAttachmentMessage<A>> messageStreamCodec = ClientboundEntityDataAttachmentMessage.streamCodec(
                type,
                this.streamCodec
        );
        PayloadTypeRegistry.playS2C().register(type, messageStreamCodec);
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            ClientPlayNetworking.registerGlobalReceiver(type, (ClientboundEntityDataAttachmentMessage<A> message, ClientPlayNetworking.Context context) -> {
                context.client().submit(() -> {
                    LocalPlayer player = context.player();
                    Entity entity = player.clientLevel.getEntity(message.entityId());
                    if (entity != null) {
                        attachmentType.setData(entity, message.value());
                    }
                });
            });
        }
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<A> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec, @Nullable Function<Entity, PlayerSet> synchronizationTargets) {
        Objects.requireNonNull(streamCodec, "stream codec is null");
        this.streamCodec = streamCodec;
        this.synchronizationTargets = synchronizationTargets;
        return this;
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<A> copyOnDeath() {
        this.copyOnDeath = true;
        return this;
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<A> defaultValue(A defaultValue) {
        return EntityDataAttachmentBuilder.super.defaultValue(defaultValue);
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<A> defaultValue(Predicate<Entity> defaultFilter, A defaultValue) {
        Objects.requireNonNull(defaultFilter, "default filter is null");
        Objects.requireNonNull(defaultValue, "default value is null");
        this.defaultValues.put(defaultFilter, defaultValue);
        return this;
    }

    @Override
    public DataAttachmentRegistry.EntityBuilder<A> persistent(Codec<A> codec) {
        return (DataAttachmentRegistry.EntityBuilder<A>) super.persistent(codec);
    }

    @Override
    void configureBuilder(AttachmentRegistry.Builder<A> builder) {
        super.configureBuilder(builder);
        if (this.copyOnDeath) {
            // Fabric does not need this check, but NeoForge does
            Objects.requireNonNull(this.codec, "codec is null");
            builder.copyOnDeath();
        }
    }
}
