package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public final class ModelEvents {

    private ModelEvents() {

    }

    public static EventInvoker<ModifyBakingResult> modifyBakingResult(@Nullable String modId) {
        return EventInvoker.lookup(ModifyBakingResult.class, modId);
    }

    public static EventInvoker<BakingCompleted> bakingCompleted(@Nullable String modId) {
        return EventInvoker.lookup(BakingCompleted.class, modId);
    }

    @FunctionalInterface
    public interface ModifyBakingResult {

        /**
         * Fired when the resource manager is reloading models and models have been baked, but before they are passed on for caching.
         * <p>Use a {@link Supplier} for {@link ModelBakery} to prevent an issue with loading the {@link net.minecraft.client.renderer.Sheets} too early on Fabric,
         * preventing modded materials from being added.
         *
         * @param models       all baked models for modifying
         * @param modelBakery  the bakery
         */
        void onModifyBakingResult(Map<ResourceLocation, BakedModel> models, Supplier<ModelBakery> modelBakery);
    }

    @FunctionalInterface
    public interface BakingCompleted {

        /**
         * Fired after the resource manager has reloaded models. Does not allow for modifying the models map, for that use {@link ModifyBakingResult}.
         * <p>Use a {@link Supplier} for {@link ModelManager} and {@link ModelBakery} to prevent an issue with loading the {@link net.minecraft.client.renderer.Sheets} too early on Fabric,
         * preventing modded materials from being added.
         *
         * @param modelManager model manager instance
         * @param models       all baked models, the collection is read-only
         * @param modelBakery  the bakery
         */
        void onBakingCompleted(Supplier<ModelManager> modelManager, Map<ResourceLocation, BakedModel> models, Supplier<ModelBakery> modelBakery);
    }
}