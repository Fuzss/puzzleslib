package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientEvents;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelManager.class)
abstract class ModelManagerFabricMixin {

    @Inject(
            method = "apply", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/resources/model/ModelManager;missingModel:Lnet/minecraft/client/resources/model/BakedModel;",
            shift = At.Shift.AFTER
    )
    )
    private void apply(@Coerce Object reloadState, ProfilerFiller profiler, CallbackInfo callback, @Local ModelBakery modelBakery) {
        FabricClientEvents.COMPLETE_MODEL_LOADING.invoker()
                .onCompleteModelLoading(() -> ModelManager.class.cast(this), () -> modelBakery);
    }
}