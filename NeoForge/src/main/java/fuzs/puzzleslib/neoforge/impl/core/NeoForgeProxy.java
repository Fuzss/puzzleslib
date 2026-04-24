package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.common.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public interface NeoForgeProxy extends ProxyImpl {

    static NeoForgeProxy get() {
        return (NeoForgeProxy) ProxyImpl.INSTANCE;
    }

    PayloadTypesContext createPayloadTypesContext(String modId, RegisterPayloadHandlersEvent event);
}
