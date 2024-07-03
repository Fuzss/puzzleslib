package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.CustomPacketPayloadAdapter;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FabricServerProxy implements FabricProxy {

    @Override
    public <T extends MessageV2<T>> void registerLegacyClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type) {
        // NO-OP
    }

    @Override
    public <T extends MessageV2<T>> void registerLegacyServerReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type) {
        ServerPlayNetworking.registerGlobalReceiver(type, (CustomPacketPayloadAdapter<T> payload, ServerPlayNetworking.Context context) -> {
            context.server().execute(() -> payload.unwrap().makeHandler().handle(payload.unwrap(), context.player(), context.server()));
        });
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type) {
        // NO-OP
    }

    @Override
    public <T extends Record & ServerboundMessage<T>> void registerServerReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<T>> type) {
        ServerPlayNetworking.registerGlobalReceiver(type, (CustomPacketPayloadAdapter<T> payload, ServerPlayNetworking.Context context) -> {
            context.server().execute(() -> payload.unwrap().getHandler().handle(payload.unwrap(), context.server(), context.player().connection, context.player(), context.player().serverLevel()));
        });
    }
}
