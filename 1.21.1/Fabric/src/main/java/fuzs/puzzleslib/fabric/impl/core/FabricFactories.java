package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v3.GameRulesFactory;
import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.fabric.impl.data.FabricTagAppender;
import fuzs.puzzleslib.fabric.impl.event.FabricEventInvokerRegistryImpl;
import fuzs.puzzleslib.fabric.impl.init.FabricGameRulesFactory;
import fuzs.puzzleslib.fabric.impl.init.FabricRegistryFactory;
import fuzs.puzzleslib.fabric.impl.item.FabricToolTypeHelper;
import fuzs.puzzleslib.fabric.impl.item.crafting.FabricCombinedIngredients;
import fuzs.puzzleslib.impl.core.CommonFactories;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.ProxyImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;

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
    public RegistryFactory getRegistryFactory() {
        return new FabricRegistryFactory();
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

    @Override
    public <T> AbstractTagAppender<T> getTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor) {
        return new FabricTagAppender<>(tagBuilder, keyExtractor);
    }
}
