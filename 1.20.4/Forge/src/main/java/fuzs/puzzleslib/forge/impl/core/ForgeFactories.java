package fuzs.puzzleslib.forge.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v3.GameRulesFactory;
import fuzs.puzzleslib.api.init.v3.PotionBrewingRegistry;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.forge.impl.data.ForgeTagAppender;
import fuzs.puzzleslib.forge.impl.event.ForgeEventInvokerRegistryImpl;
import fuzs.puzzleslib.forge.impl.init.ForgeGameRulesFactory;
import fuzs.puzzleslib.forge.impl.init.ForgePotionBrewingRegistry;
import fuzs.puzzleslib.forge.impl.item.ForgeToolTypeHelper;
import fuzs.puzzleslib.forge.impl.item.crafting.ForgeCombinedIngredients;
import fuzs.puzzleslib.impl.core.CommonFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.ProxyImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

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
        return new ForgePotionBrewingRegistry();
    }

    @Override
    public GameRulesFactory getGameRulesFactory() {
        return new ForgeGameRulesFactory();
    }

    @Override
    public void registerLoadingHandlers() {
        ForgeEventInvokerRegistryImpl.registerLoadingHandlers();
    }

    @Override
    public void registerEventHandlers() {
        ForgeEventInvokerRegistryImpl.registerEventHandlers();
    }

    @Override
    public ToolTypeHelper getToolTypeHelper() {
        return new ForgeToolTypeHelper();
    }

    @Override
    public CombinedIngredients getCombinedIngredients() {
        return new ForgeCombinedIngredients();
    }

    @Override
    public <T> AbstractTagAppender<T> getTagAppender(TagBuilder tagBuilder, String modId, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        return new ForgeTagAppender<>(tagBuilder, modId, keyExtractor);
    }
}
