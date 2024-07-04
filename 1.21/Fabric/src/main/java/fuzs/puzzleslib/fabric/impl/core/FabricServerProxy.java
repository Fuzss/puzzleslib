package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.api.network.v3.serialization.CustomPacketPayloadAdapter;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Function;

public class FabricServerProxy implements FabricProxy {

    @Override
    public <M1, M2> void registerClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<M1>> type, Function<M1, ClientboundMessage<M2>> adapter) {
        // NO-OP
    }

    @Override
    public <M1, M2> void registerServerReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<M1>> type, Function<M1, ServerboundMessage<M2>> adapter) {
        ServerPlayNetworking.registerGlobalReceiver(type,
                (CustomPacketPayloadAdapter<M1> payload, ServerPlayNetworking.Context context) -> {
                    context.server().execute(() -> {
                        ServerboundMessage<M2> message = adapter.apply(payload.unwrap());
                        message.getHandler()
                                .handle((M2) message,
                                        context.server(),
                                        context.player().connection,
                                        context.player(),
                                        context.player().serverLevel()
                                );
                    });
                }
        );
    }
}
