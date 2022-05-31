package fuzs.puzzleslib.core;

import fuzs.puzzleslib.network.FabricNetworkHandler;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.proxy.FabricClientProxy;
import fuzs.puzzleslib.proxy.FabricServerProxy;
import fuzs.puzzleslib.proxy.Proxy;

import java.util.function.Supplier;

public class FabricFactories implements Factories {

    @Override
    public NetworkHandler network(String modId, boolean clientAcceptsVanillaOrMissing, boolean serverAcceptsVanillaOrMissing) {
        return FabricNetworkHandler.of(modId);
    }

    @Override
    public Supplier<Proxy> clientProxy() {
        return () -> new FabricClientProxy();
    }

    @Override
    public Supplier<Proxy> serverProxy() {
        return () -> new FabricServerProxy();
    }
}
