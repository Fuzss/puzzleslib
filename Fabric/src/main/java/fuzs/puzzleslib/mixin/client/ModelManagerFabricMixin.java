package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.events.v2.ModelEvents;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;

@Mixin(ModelManager.class)
public abstract class ModelManagerFabricMixin {
    @Shadow
    private Map<ResourceLocation, BakedModel> bakedRegistry;
    @Unique
    private ThreadLocal<ModelBakery> puzzleslib$modelBakery = new ThreadLocal<>();

    // we cannot use inject as the return value is a package-private class, not sure how to deal with that other than access widener
    @ModifyVariable(method = "loadModels", at = @At("LOAD"), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=dispatch")))
    private ModelBakery loadModels(ModelBakery modelBakery) {
        // we cannot capture the local later in apply since we pass on method parameters, so store this here for later use
        this.puzzleslib$modelBakery.set(modelBakery);
        ModelEvents.MODIFY_BAKING_RESULT.invoker().onModifyBakingResult(modelBakery.getBakedTopLevelModels(), modelBakery);
        return modelBakery;
    }

    @Inject(method = "apply", at = @At(value = "FIELD", target = "Lnet/minecraft/client/resources/model/ModelManager;missingModel:Lnet/minecraft/client/resources/model/BakedModel;", shift = At.Shift.AFTER))
    private void apply(CallbackInfo callback) {
        ModelBakery modelBakery = this.puzzleslib$modelBakery.get();
        Objects.requireNonNull(modelBakery, "model bakery is null");
        ModelEvents.BAKING_COMPLETED.invoker().onBakingCompleted(ModelManager.class.cast(this), this.bakedRegistry, modelBakery);
        this.puzzleslib$modelBakery.remove();
    }
}