package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ModelManager.class)
abstract class ModelManagerFabricMixin {
    @Shadow
    private Map<ResourceLocation, BakedModel> bakedRegistry;

    @Inject(
            method = "loadModels",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;forEach(Ljava/util/function/BiConsumer;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void loadModels(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, ModelBakery modelBakery, CallbackInfoReturnable<Object> callback) {
        FabricClientEvents.MODIFY_BAKING_RESULT.invoker()
                .onModifyBakingResult(modelBakery.getBakedTopLevelModels(), () -> modelBakery);
    }

    @Inject(
            method = "apply",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/resources/model/ModelManager;missingModel:Lnet/minecraft/client/resources/model/BakedModel;",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void apply(@Coerce Object reloadState, ProfilerFiller profiler, CallbackInfo callback, ModelBakery modelBakery) {
        FabricClientEvents.BAKING_COMPLETED.invoker()
                .onBakingCompleted(() -> ModelManager.class.cast(this), this.bakedRegistry, () -> modelBakery);
        FabricClientEvents.COMPLETE_MODEL_LOADING.invoker()
                .onCompleteModelLoading(() -> ModelManager.class.cast(this), () -> modelBakery);
    }
}