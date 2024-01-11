package fuzs.puzzleslib.neoforge.mixin.client;

import fuzs.puzzleslib.neoforge.impl.client.event.ForgeModelBakerImpl;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ModelManager.class)
abstract class ModelManagerForgeMixin {

    @Inject(method = "loadModels", at = @At("HEAD"))
    private void loadModels(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> atlasPreparations, ModelBakery modelBakery, CallbackInfoReturnable<Object> callback) {
        ForgeModelBakerImpl.setAtlasPreparations(atlasPreparations);
    }
}
