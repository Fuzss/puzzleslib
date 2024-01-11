package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.core.ContentRegistrationFlags;

import java.util.function.Consumer;

/**
 * Forge client factories
 */
public final class ForgeClientFactories implements ClientFactories {

    @Override
    public Consumer<ClientModConstructor> clientModConstructor(String modId, ContentRegistrationFlags... contentRegistrations) {
        return constructor -> ForgeClientModConstructor.construct(constructor, modId, contentRegistrations);
    }
}
