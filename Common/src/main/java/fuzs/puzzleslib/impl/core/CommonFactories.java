package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;

import java.util.function.Supplier;

public interface CommonFactories {
    CommonFactories INSTANCE = ServiceProviderHelper.load(CommonFactories.class);

    void constructMod(String modId, ModConstructor modConstructor, ContentRegistrationFlags... contentRegistrations);

    ModContext getModContext(String modId);

    Supplier<Proxy> getClientProxy();

    Supplier<Proxy> getServerProxy();

    PotionBrewingRegistry getPotionBrewingRegistry();

    GameRulesFactory getGameRulesFactory();

    <T> EventInvoker<T> getEventInvoker(Class<T> clazz);
}
