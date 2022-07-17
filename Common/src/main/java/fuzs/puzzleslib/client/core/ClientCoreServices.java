package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.core.CoreServices;

/**
 * services which may only be loaded on the client side
 */
public class ClientCoreServices extends CoreServices {
    /**
     * a collection of utility methods for registering client side content
     *
     * @deprecated use {@link ClientModConstructor} instead for implementing everything directly on the main mod client class
     */
    @Deprecated(forRemoval = true)
    public static final ClientRegistration CLIENT_REGISTRATION = load(ClientRegistration.class);
    /**
     * important client exclusive factories
     */
    public static final ClientFactories FACTORIES = load(ClientFactories.class);
}
