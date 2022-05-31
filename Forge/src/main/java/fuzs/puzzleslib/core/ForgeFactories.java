package fuzs.puzzleslib.core;

import fuzs.puzzleslib.network.ForgeNetworkHandler;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.ForgeClientProxy;
import fuzs.puzzleslib.proxy.ForgeServerProxy;
import fuzs.puzzleslib.proxy.Proxy;

import java.util.function.Supplier;

public class ForgeFactories implements Factories {

    @Override
    public NetworkHandler network(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return ForgeNetworkHandler.of(modId, clientAcceptsVanillaOrMissing, serverAcceptsVanillaOrMissing);
    }

    @Override
    public Supplier<Proxy> clientProxy() {
        return () -> new ForgeClientProxy();
    }

    @Override
    public Supplier<Proxy> serverProxy() {
        return () -> new ForgeServerProxy();
    }
}
