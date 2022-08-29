package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.impl.client.resources.model.ModelManagerExtension;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin extends SimplePreparableReloadListener<ModelBakery> implements ModelManagerExtension {
    @Unique
    private ModelBakery puzzleslib_modelBakery;

    @Inject(method = "apply", at = @At(value = "FIELD", target = "Lnet/minecraft/client/resources/model/ModelManager;missingModel:Lnet/minecraft/client/resources/model/BakedModel;", shift = At.Shift.AFTER))
    protected void apply$inject$field$missingModel(ModelBakery modelBakery, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo callback) {
        this.puzzleslib_modelBakery = modelBakery;
    }

    @Accessor("bakedRegistry")
    @Override
    public abstract Map<ResourceLocation, BakedModel> puzzleslib_getBakedRegistry();

    @Override
    public ModelBakery puzzleslib_getModelBakery() {
        Objects.requireNonNull(this.puzzleslib_modelBakery, "Attempted to query model bakery before it has been initialized");
        return this.puzzleslib_modelBakery;
    }
}
