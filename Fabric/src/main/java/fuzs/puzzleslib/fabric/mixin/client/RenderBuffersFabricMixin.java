package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import fuzs.puzzleslib.fabric.impl.client.event.RenderBuffersRegistryImpl;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderBuffers.class)
abstract class RenderBuffersFabricMixin {

    @Inject(method = "method_54639(Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;)V", at = @At("TAIL"))
    private void method_54639(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> buffers, CallbackInfo callback) {
        RenderBuffersRegistryImpl.addAll(buffers);
    }
}
