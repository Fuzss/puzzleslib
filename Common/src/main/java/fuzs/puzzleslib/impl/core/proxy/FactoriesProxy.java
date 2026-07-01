package fuzs.puzzleslib.impl.core.proxy;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.AbstractRecipeProvider;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v3.GameRulesFactory;
import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface FactoriesProxy {

    ModConstructorImpl<ModConstructor> getModConstructorImpl();

    ModContext getModContext(String modId);

    RegistryFactory getRegistryFactoryV3();

    fuzs.puzzleslib.api.init.v4.registry.RegistryFactory getRegistryFactoryV4();

    GameRulesFactory getGameRulesFactory();

    ToolTypeHelper getToolTypeHelper();

    CombinedIngredients getCombinedIngredients();

    <T> AbstractTagAppender<T> getTagAppenderV2(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor);

    <T> fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> getTagAppenderV3(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor);

    DataAttachmentRegistryImpl getDataAttachmentRegistry();

    RecipeOutput getTransformingRecipeOutput(RecipeOutput recipeOutput, UnaryOperator<Recipe<?>> operator);

    RecipeOutput getIdentifiableRecipeOutput(AbstractRecipeProvider provider, CachedOutput output, HolderLookup.Provider registries, List<CompletableFuture<?>> completableFutures);
}
