package fuzs.puzzleslib.impl.core.proxy;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagAppender;
import fuzs.puzzleslib.api.init.v3.registry.RegistryFactory;
import fuzs.puzzleslib.api.item.v2.ToolTypeHelper;
import fuzs.puzzleslib.api.item.v2.crafting.CombinedIngredients;
import fuzs.puzzleslib.impl.attachment.DataAttachmentRegistryImpl;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.core.context.ModConstructorImpl;
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
