package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.impl.core.CommonFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.ProxyImpl;
import fuzs.puzzleslib.neoforge.impl.event.ForgeEventInvokerRegistryImpl;
import fuzs.puzzleslib.neoforge.impl.init.ForgeGameRulesFactory;
import fuzs.puzzleslib.neoforge.impl.init.PotionBrewingRegistryForge;
import fuzs.puzzleslib.neoforge.impl.item.ForgeToolTypeHelper;
import fuzs.puzzleslib.neoforge.impl.item.crafting.ForgeCombinedIngredients;

import java.util.Set;

public final class ForgeFactories implements CommonFactories {

    @Override
    public void constructMod(String modId, ModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        ForgeModConstructor.construct(modConstructor, modId, availableFlags, flagsToHandle);
    }

    @Override
    public ModContext getModContext(String modId) {
        return new ForgeModContext(modId);
    }

    @Override
    public ProxyImpl getClientProxy() {
        return new ForgeClientProxy();
    }

    @Override
    public ProxyImpl getServerProxy() {
        return new ForgeServerProxy();
    }

    @Override
    public PotionBrewingRegistry getPotionBrewingRegistry() {
        return new PotionBrewingRegistryForge();
    }

    @Override
    public GameRulesFactory getGameRulesFactory() {
        return new ForgeGameRulesFactory();
    }

    @Override
    public void registerEventInvokers() {
        ForgeEventInvokerRegistryImpl.register();
    }

    @Override
    public ToolTypeHelper getToolTypeHelper() {
        return new ForgeToolTypeHelper();
    }

    @Override
    public CombinedIngredients getCombinedIngredients() {
        return new ForgeCombinedIngredients();
    }
}
