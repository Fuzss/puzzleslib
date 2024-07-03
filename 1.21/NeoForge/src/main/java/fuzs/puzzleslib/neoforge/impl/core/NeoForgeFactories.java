package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v3.GameRulesFactory;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.impl.core.CommonFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.ProxyImpl;
import fuzs.puzzleslib.neoforge.impl.data.NeoForgeTagAppender;
import fuzs.puzzleslib.neoforge.impl.event.NeoForgeEventInvokerRegistryImpl;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeGameRulesFactory;
import fuzs.puzzleslib.neoforge.impl.item.NeoForgeToolTypeHelper;
import fuzs.puzzleslib.neoforge.impl.item.crafting.NeoForgeCombinedIngredients;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

public final class NeoForgeFactories implements CommonFactories {

    @Override
    public void constructMod(String modId, ModConstructor modConstructor, Set<ContentRegistrationFlags> availableFlags, Set<ContentRegistrationFlags> flagsToHandle) {
        NeoForgeModConstructor.construct(modConstructor, modId, availableFlags, flagsToHandle);
    }

    @Override
    public ModContext getModContext(String modId) {
        return new NeoForgeModContext(modId);
    }

    @Override
    public ProxyImpl getClientProxy() {
        return new NeoForgeClientProxy();
    }

    @Override
    public ProxyImpl getServerProxy() {
        return new NeoForgeServerProxy();
    }

    @Override
    public GameRulesFactory getGameRulesFactory() {
        return new NeoForgeGameRulesFactory();
    }

    @Override
    public void registerLoadingHandlers() {
        NeoForgeEventInvokerRegistryImpl.registerLoadingHandlers();
    }

    @Override
    public void registerEventHandlers() {
        NeoForgeEventInvokerRegistryImpl.registerEventHandlers();
    }

    @Override
    public ToolTypeHelper getToolTypeHelper() {
        return new NeoForgeToolTypeHelper();
    }

    @Override
    public CombinedIngredients getCombinedIngredients() {
        return new NeoForgeCombinedIngredients();
    }

    @Override
    public <T> AbstractTagAppender<T> getTagAppender(TagBuilder tagBuilder, String modId, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        return new NeoForgeTagAppender<>(tagBuilder, modId, keyExtractor);
    }
}
