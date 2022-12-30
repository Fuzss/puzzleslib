package fuzs.puzzleslib.client.core;

import java.util.function.Consumer;

/**
 * Fabric client factories
 */
public final class FabricClientFactories implements ClientFactories {

    @Override
    public Consumer<ClientModConstructor> clientModConstructor(String modId) {
        return constructor -> FabricClientModConstructor.construct(modId, constructor);
    }
}
