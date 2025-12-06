package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import fuzs.puzzleslib.fabric.impl.client.core.context.RenderBuffersContextFabricImpl;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.SequencedMap;

@Mixin(RenderBuffers.class)
abstract class RenderBuffersFabricMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "STORE", ordinal = 0))
    public SequencedMap<RenderType, ByteBufferBuilder> init(SequencedMap<RenderType, ByteBufferBuilder> sequencedMap) {
        RenderBuffersContextFabricImpl.addAll(sequencedMap);
        return sequencedMap;
    }
}
