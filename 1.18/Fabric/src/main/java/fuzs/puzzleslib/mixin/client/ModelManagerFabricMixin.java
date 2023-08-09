package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelManager.class)
abstract class ModelManagerFabricMixin extends SimplePreparableReloadListener<ModelBakery> {

    @Shadow
    private Map<ResourceLocation, BakedModel> bakedRegistry;

    @Inject(method = "apply", at = @At(value = "FIELD", target = "Lnet/minecraft/client/resources/model/ModelManager;missingModel:Lnet/minecraft/client/resources/model/BakedModel;", shift = At.Shift.AFTER))
    protected void apply(ModelBakery modelBakery, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo callback) {
        ModelEvents.BAKING_COMPLETED.invoker().onBakingCompleted(ModelManager.class.cast(this), this.bakedRegistry, modelBakery);
    }
}