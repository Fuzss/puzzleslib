package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class FabricServerProxy implements FabricProxy {

    @Override
    public <M1, M2> void registerClientReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<M1>> type, BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally, Function<M1, ClientboundMessage<M2>> messageAdapter) {
        // NO-OP
    }

    @Override
    public <M1, M2> void registerServerReceiver(CustomPacketPayload.Type<CustomPacketPayloadAdapter<M1>> type, BiConsumer<Throwable, Consumer<Component>> disconnectExceptionally, Function<M1, ServerboundMessage<M2>> messageAdapter) {
        ServerPlayNetworking.registerGlobalReceiver(type,
                (CustomPacketPayloadAdapter<M1> payload, ServerPlayNetworking.Context context) -> {
                    context.server().submit(() -> {
                        ServerboundMessage<M2> message = messageAdapter.apply(payload.unwrap());
                        message.getHandler()
                                .handle(message.unwrap(),
                                        context.server(),
                                        context.player().connection,
                                        context.player(),
                                        context.player().serverLevel()
                                );
                    }).exceptionally((Throwable throwable) -> {
                        disconnectExceptionally.accept(throwable, context.responseSender()::disconnect);
                        return null;
                    });
                }
        );
    }
}
