package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.impl.network.codec.CustomPacketPayloadAdapter;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface NeoForgeProxy extends ProxyImpl {

    static NeoForgeProxy get() {
        return (NeoForgeProxy) Proxy.INSTANCE;
    }

    PayloadTypesContext createPayloadTypesContext(String modId, RegisterPayloadHandlersEvent event);

    @Deprecated
    <M1, M2> CompletableFuture<Void> registerClientReceiver(CustomPacketPayloadAdapter<M1> payload, IPayloadContext context, Function<M1, ClientboundMessage<M2>> adapter);

    @Deprecated
    <M1, M2> CompletableFuture<Void> registerServerReceiver(CustomPacketPayloadAdapter<M1> payload, IPayloadContext context, Function<M1, ServerboundMessage<M2>> adapter);
}
