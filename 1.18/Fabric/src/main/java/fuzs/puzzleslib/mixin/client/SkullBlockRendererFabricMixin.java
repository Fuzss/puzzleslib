package fuzs.puzzleslib.mixin.client;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.impl.client.event.SkullRendererRegistryImpl;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.level.block.SkullBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SkullBlockRenderer.class)
abstract class SkullBlockRendererFabricMixin {

    @ModifyVariable(method = "createSkullRenderers", at = @At("STORE"))
    private static ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> puzzleslib$createSkullRenderers(ImmutableMap.Builder<SkullBlock.Type, SkullModelBase> builder, EntityModelSet entityModelSet) {
        SkullRendererRegistryImpl.addAll(entityModelSet, builder);
        return builder;
    }
}
