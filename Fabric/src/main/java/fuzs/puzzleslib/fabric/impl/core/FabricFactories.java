package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.init.v3.gamerule.GameRulesFactory;
import fuzs.puzzleslib.api.init.v3.alchemy.PotionBrewingRegistry;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.fabric.impl.event.FabricEventInvokerRegistryImpl;
import fuzs.puzzleslib.fabric.impl.init.FabricGameRulesFactory;
import fuzs.puzzleslib.fabric.impl.init.FabricPotionBrewingRegistry;
import fuzs.puzzleslib.fabric.impl.item.FabricToolTypeHelper;
import fuzs.puzzleslib.impl.core.CommonFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.ProxyImpl;
import fuzs.puzzleslib.fabric.impl.item.crafting.FabricCombinedIngredients;

import java.util.Set;

public final class FabricFactories implements CommonFactories {

    @Override
    public void constructMod(String modId, ModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        FabricModConstructor.construct(modConstructor, modId, availableFlags, flagsToHandle);
    }

    @Override
    public ModContext getModContext(String modId) {
        return new FabricModContext(modId);
    }

    @Override
    public ProxyImpl getClientProxy() {
        return new FabricClientProxy();
    }

    @Override
    public ProxyImpl getServerProxy() {
        return new FabricServerProxy();
    }

    @Override
    public PotionBrewingRegistry getPotionBrewingRegistry() {
        return new FabricPotionBrewingRegistry();
    }

    @Override
    public GameRulesFactory getGameRulesFactory() {
        return new FabricGameRulesFactory();
    }

    @Override
    public void registerLoadingHandlers() {
        FabricEventInvokerRegistryImpl.registerLoadingHandlers();
    }

    @Override
    public void registerEventHandlers() {
        FabricEventInvokerRegistryImpl.registerEventHandlers();
    }

    @Override
    public ToolTypeHelper getToolTypeHelper() {
        return new FabricToolTypeHelper();
    }

    @Override
    public CombinedIngredients getCombinedIngredients() {
        return new FabricCombinedIngredients();
    }
}
