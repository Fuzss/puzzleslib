package fuzs.puzzleslib.client.core;

import java.util.function.Consumer;

/**
 * Forge client factories
 */
public final class ForgeClientFactories implements ClientFactories {

    @Override
    public Consumer<ClientModConstructor> clientModConstructor(String modId) {
        return constructor -> ForgeClientModConstructor.construct(modId, constructor);
    }
}
