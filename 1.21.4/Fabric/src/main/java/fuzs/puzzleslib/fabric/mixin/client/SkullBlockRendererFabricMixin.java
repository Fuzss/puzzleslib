package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.puzzleslib.fabric.impl.client.event.SkullRendererRegistryImpl;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SkullBlockRenderer.class)
abstract class SkullBlockRendererFabricMixin {

    @ModifyReturnValue(method = "createModel", at = @At("RETURN"))
    private static @Nullable SkullModelBase createModel(@Nullable SkullModelBase skullModel, EntityModelSet modelSet, SkullBlock.Type type) {
        return skullModel == null ? SkullRendererRegistryImpl.createSkullModel(type, modelSet) : skullModel;
    }
}
