package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface ForgeProxy extends ProxyImpl {

    <T extends Record & ClientboundMessage<T>> void registerClientReceiverV2(T message, Supplier<NetworkEvent.Context> supplier);

    <T extends Record & ServerboundMessage<T>> void registerServerReceiverV2(T message, Supplier<NetworkEvent.Context> supplier);
}
