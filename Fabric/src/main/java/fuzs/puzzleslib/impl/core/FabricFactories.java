package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;
import fuzs.puzzleslib.impl.event.FabricEventInvokerRegistryImpl;
import fuzs.puzzleslib.impl.init.FabricGameRulesFactory;
import fuzs.puzzleslib.impl.init.PotionBrewingRegistryFabric;

import java.util.function.Supplier;

public final class FabricFactories implements CommonFactories {

    @Override
    public void constructMod(String modId, ModConstructor modConstructor, ContentRegistrationFlags... contentRegistrations) {
        FabricModConstructor.construct(modConstructor, modId, contentRegistrations);
    }

    @Override
    public ModContext getModContext(String modId) {
        return new FabricModContext(modId);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Supplier<Proxy> getClientProxy() {
        return () -> new FabricClientProxy();
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public Supplier<Proxy> getServerProxy() {
        return () -> new FabricServerProxy();
    }

    @Override
    public PotionBrewingRegistry getPotionBrewingRegistry() {
        return new PotionBrewingRegistryFabric();
    }

    @Override
    public GameRulesFactory getGameRulesFactory() {
        return new FabricGameRulesFactory();
    }

    @Override
    public <T> EventInvoker<T> getEventInvoker(Class<T> clazz) {
        return FabricEventInvokerRegistryImpl.INSTANCE.lookup(clazz);
    }
}
