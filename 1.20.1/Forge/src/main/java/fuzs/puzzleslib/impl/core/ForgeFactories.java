package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v2.GameRulesFactory;
import fuzs.puzzleslib.api.init.v2.PotionBrewingRegistry;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.impl.data.ForgeTagAppender;
import fuzs.puzzleslib.impl.event.ForgeEventInvokerRegistryImpl;
import fuzs.puzzleslib.impl.init.ForgeGameRulesFactory;
import fuzs.puzzleslib.impl.init.PotionBrewingRegistryForge;
import fuzs.puzzleslib.impl.item.ForgeToolTypeHelper;
import fuzs.puzzleslib.impl.item.crafting.ForgeCombinedIngredients;
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
        return new PotionBrewingRegistryForge();
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
    public <T> AbstractTagAppender<T> getTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        return new ForgeTagAppender<>(tagBuilder, keyExtractor);
    }
}
