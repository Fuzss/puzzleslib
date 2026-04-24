package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.impl.client.core.context.EntitySpectatorShadersContextFabricImpl;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class GameRendererFabricMixin {
    @Shadow
    @Nullable
    private Identifier postEffectId;

    @Inject(method = "checkEntityPostEffect", at = @At("TAIL"))
    public void checkEntityPostEffect(@Nullable Entity entity, CallbackInfo callback) {
        // Vanilla has set no effect, so we look for one. This mirrors the implementation on NeoForge.
        if (this.postEffectId == null) {
            EntitySpectatorShadersContextFabricImpl.getEntityShader(entity).ifPresent(this::setPostEffect);
        }
    }

    @Shadow
    protected abstract void setPostEffect(Identifier id);
}
