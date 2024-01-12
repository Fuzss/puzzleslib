package fuzs.puzzleslib.forge.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.puzzleslib.impl.core.ProxyImpl;
import net.minecraftforge.event.network.CustomPayloadEvent;

public interface ForgeProxy extends ProxyImpl {

    <T extends Record & ClientboundMessage<T>> void registerClientReceiverV2(T message, CustomPayloadEvent.Context context);

    <T extends Record & ServerboundMessage<T>> void registerServerReceiverV2(T message, CustomPayloadEvent.Context context);
}
