package fuzs.puzzleslib.impl.attachment;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.event.v1.entity.player.AfterChangeDimensionCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerCopyEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerNetworkEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerTrackingEvents;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface EntityDataAttachmentBuilderImpl<A> extends DataAttachmentRegistry.EntityBuilder<A> {

    @Nullable
    default BiConsumer<Entity, A> getSynchronizer(ResourceLocation resourceLocation, AttachmentTypeAdapter<Entity, A> attachmentType, @Nullable StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec, @Nullable Function<Entity, PlayerSet> synchronizationTargets) {
        if (streamCodec == null) {
            return null;
        } else {
            CustomPacketPayload.Type<ClientboundEntityDataAttachmentMessage<A>> type = new CustomPacketPayload.Type<>(
                    resourceLocation);
            this.registerPayloadHandlers(resourceLocation, attachmentType, type, streamCodec);
            this.registerEventHandlers(attachmentType, type, synchronizationTargets);
            return this.getDefaultSynchronizer(attachmentType, type, synchronizationTargets);
        }
    }

    void registerPayloadHandlers(ResourceLocation resourceLocation, AttachmentTypeAdapter<Entity, A> attachmentType, CustomPacketPayload.Type<ClientboundEntityDataAttachmentMessage<A>> type, @Nullable StreamCodec<? super RegistryFriendlyByteBuf, A> streamCodec);

    private void registerEventHandlers(AttachmentTypeAdapter<Entity, A> attachmentType, CustomPacketPayload.Type<ClientboundEntityDataAttachmentMessage<A>> type, @Nullable Function<Entity, PlayerSet> synchronizationTargets) {
        PlayerNetworkEvents.LOGGED_IN.register((ServerPlayer serverPlayer) -> {
            this.broadcast(type, serverPlayer, attachmentType);
        });
        AfterChangeDimensionCallback.EVENT.register((ServerPlayer serverPlayer, ServerLevel from, ServerLevel to) -> {
            this.broadcast(type, serverPlayer, attachmentType);
        });
        PlayerCopyEvents.RESPAWN.register((ServerPlayer serverPlayer, boolean originalStillAlive) -> {
            this.broadcast(type, serverPlayer, attachmentType);
        });
        if (synchronizationTargets != null) {
            PlayerTrackingEvents.START.register((Entity trackedEntity, ServerPlayer serverPlayer) -> {
                this.broadcast(type, trackedEntity, PlayerSet.ofPlayer(serverPlayer), attachmentType);
            });
        }
    }

    private BiConsumer<Entity, A> getDefaultSynchronizer(AttachmentTypeAdapter<Entity, A> attachmentType, CustomPacketPayload.Type<ClientboundEntityDataAttachmentMessage<A>> type, @Nullable Function<Entity, PlayerSet> synchronizationTargets) {
        return (Entity entity, A value) -> {
            PlayerSet playerSet;
            if (synchronizationTargets != null) {
                playerSet = synchronizationTargets.apply(entity);
            } else {
                playerSet = PlayerSet.ofEntity(entity);
            }
            this.broadcast(type, entity, playerSet, attachmentType);
        };
    }

    private void broadcast(CustomPacketPayload.Type<ClientboundEntityDataAttachmentMessage<A>> type, ServerPlayer serverPlayer, AttachmentTypeAdapter<Entity, A> attachmentType) {
        this.broadcast(type, serverPlayer, PlayerSet.ofPlayer(serverPlayer), attachmentType);
    }

    private void broadcast(CustomPacketPayload.Type<ClientboundEntityDataAttachmentMessage<A>> type, Entity entity, PlayerSet playerSet, AttachmentTypeAdapter<Entity, A> attachmentType) {
        if (attachmentType.hasData(entity)) {
            ClientboundEntityDataAttachmentMessage<A> message = new ClientboundEntityDataAttachmentMessage<>(type,
                    entity.getId(),
                    attachmentType.getData(entity)
            );
            playerSet.broadcast(new ClientboundCustomPayloadPacket(message));
        }
    }
}
