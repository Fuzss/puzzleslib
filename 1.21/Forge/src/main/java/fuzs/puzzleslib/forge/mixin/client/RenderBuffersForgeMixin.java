package fuzs.puzzleslib.forge.mixin.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import fuzs.puzzleslib.forge.impl.client.core.context.RenderBuffersContextForgeImpl;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
abstract class RenderBuffersForgeMixin {

    @SuppressWarnings("target")
    @Inject(method = "lambda$new$1(Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;)V", at = @At("TAIL"))
    private void lambda$new$1(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> buffers, CallbackInfo callback) {
        RenderBuffersContextForgeImpl.addAll(buffers);
    }
}
