package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.impl.core.ProxyImpl;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.concurrent.CompletableFuture;

public interface NeoForgeProxy extends ProxyImpl {

    <T extends Record & ClientboundMessage<T>> CompletableFuture<Void> registerClientReceiverV2(T message, PlayPayloadContext context);

    <T extends Record & ServerboundMessage<T>> CompletableFuture<Void> registerServerReceiverV2(T message, PlayPayloadContext context);
}
