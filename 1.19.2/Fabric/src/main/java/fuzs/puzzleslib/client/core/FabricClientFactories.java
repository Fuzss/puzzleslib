package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.core.ContentRegistrationFlags;

import java.util.function.Consumer;

/**
 * Fabric client factories
 */
public final class FabricClientFactories implements ClientFactories {

    @Override
    public Consumer<ClientModConstructor> clientModConstructor(String modId, ContentRegistrationFlags... contentRegistrations) {
        return constructor -> FabricClientModConstructor.construct(constructor, modId, contentRegistrations);
    }
}
