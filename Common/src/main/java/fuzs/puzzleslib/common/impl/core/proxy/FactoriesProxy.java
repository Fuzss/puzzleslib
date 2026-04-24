package fuzs.puzzleslib.common.impl.core.proxy;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.common.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.common.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.common.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.common.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.common.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.common.impl.core.ModContext;
import fuzs.puzzleslib.common.impl.core.context.ModConstructorImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface FactoriesProxy {

    ModConstructorImpl<ModConstructor> getModConstructorImpl();

    ModContext getModContext(String modId);

    RegistryFactory getRegistryFactory();

    ToolTypeHelper getToolTypeHelper();

    CombinedIngredients getCombinedIngredients();

    <T> AbstractTagAppender<T> getTagAppender(TagBuilder tagBuilder, @Nullable Function<T, ResourceKey<T>> keyExtractor);

    DataAttachmentRegistryImpl getDataAttachmentRegistry();

    RecipeOutput getTransformingRecipeOutput(RecipeOutput recipeOutput, UnaryOperator<Recipe<?>> operator);

    RecipeOutput getRecipeProviderOutput(CachedOutput output, String modId, PackOutput packOutput, HolderLookup.Provider registries, Consumer<CompletableFuture<?>> consumer);

    RecipeOutput getThrowingRecipeOutput();
}
