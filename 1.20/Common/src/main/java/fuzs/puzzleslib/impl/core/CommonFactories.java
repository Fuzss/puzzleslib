package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.server.ServerTickEvents;
import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public interface CommonFactories {
    CommonFactories INSTANCE = ServiceProviderHelper.load(CommonFactories.class);

    void constructMod(String modId, ModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle);

    ModContext getModContext(String modId);

    Proxy getClientProxy();

    Proxy getServerProxy();

    PotionBrewingRegistry getPotionBrewingRegistry();

    GameRulesFactory getGameRulesFactory();

    <T> EventInvoker<T> getEventInvoker(Class<T> clazz, @Nullable Object context);

    ToolTypeHelper getToolTypeHelper();
}
