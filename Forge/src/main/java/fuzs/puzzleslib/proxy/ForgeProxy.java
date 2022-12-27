package fuzs.puzzleslib.proxy;

import fuzs.puzzleslib.network.v2.ClientboundMessage;
import fuzs.puzzleslib.network.v2.ServerboundMessage;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface ForgeProxy extends Proxy {

    <T extends Record & ClientboundMessage<T>> void registerClientReceiverV2(T message, Supplier<NetworkEvent.Context> supplier);

    <T extends Record & ServerboundMessage<T>> void registerServerReceiverV2(T message, Supplier<NetworkEvent.Context> supplier);
}
