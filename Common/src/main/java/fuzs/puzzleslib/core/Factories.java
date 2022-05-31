package fuzs.puzzleslib.core;

import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.Proxy;

import java.util.function.Supplier;

/**
 * all sorts of instance factories that need to be created on a per mod basis
 */
public interface Factories {

    /**
     * creates a new network handler
     * @param modId id for channel name
     * @return mod specific network handler with default channel
     */
    default NetworkHandler network(String modId) {
        return this.network(modId, false, false);
    }

    /**
     * creates a new network handler
     * @param modId id for channel name
     * @param clientAcceptsVanillaOrMissing are servers without this mod or vanilla compatible
     * @param serverAcceptsVanillaOrMissing are clients without this mod or vanilla compatible
     * @return mod specific network handler with configured channel
     */
    NetworkHandler network(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing);

    Supplier<Proxy> clientProxy();

    Supplier<Proxy> serverProxy();
}
