package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import fuzs.puzzleslib.api.init.v3.gamerule.GameRulesFactory;
import fuzs.puzzleslib.api.init.v3.alchemy.PotionBrewingRegistry;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;

import java.util.Set;

public interface CommonFactories {
    CommonFactories INSTANCE = ServiceProviderHelper.load(CommonFactories.class);

    void constructMod(String modId, ModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle);

    ModContext getModContext(String modId);

    ProxyImpl getClientProxy();

    ProxyImpl getServerProxy();

    PotionBrewingRegistry getPotionBrewingRegistry();

    GameRulesFactory getGameRulesFactory();

    void registerLoadingHandlers();

    void registerEventHandlers();

    ToolTypeHelper getToolTypeHelper();

    CombinedIngredients getCombinedIngredients();
}
