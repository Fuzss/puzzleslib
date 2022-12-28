package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.impl.client.renderer.EntitySpectatorShaderRegistryImpl;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class GameRendererFabricMixin {
    @Shadow
    @Nullable
    private PostChain postEffect;

    @Inject(method = "checkEntityPostEffect", at = @At("TAIL"))
    public void puzzleslib$checkEntityPostEffect(@Nullable Entity entity, CallbackInfo callback) {
        if (this.postEffect == null && entity != null) {
            EntitySpectatorShaderRegistryImpl.getEntityShader(entity).ifPresent(this::loadEffect);
        }
    }

    @Shadow
    private void loadEffect(ResourceLocation resourceLocation) {
        throw new IllegalStateException();
    }
}
