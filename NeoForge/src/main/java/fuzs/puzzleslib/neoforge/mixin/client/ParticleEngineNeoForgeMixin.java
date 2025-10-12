package fuzs.puzzleslib.neoforge.mixin.client;

import fuzs.puzzleslib.neoforge.impl.client.core.context.ParticleProvidersContextNeoForgeImpl;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleGroup;
import net.minecraft.client.particle.ParticleRenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleEngine.class)
abstract class ParticleEngineNeoForgeMixin {

    @Inject(method = "createParticleGroup", at = @At("HEAD"), cancellable = true)
    private void createParticleGroup(ParticleRenderType particleRenderType, CallbackInfoReturnable<ParticleGroup<?>> callback) {
        ParticleGroup<?> particleGroup = ParticleProvidersContextNeoForgeImpl.createParticleGroup(particleRenderType,
                ParticleEngine.class.cast(this));
        if (particleGroup != null) {
            callback.setReturnValue(particleGroup);
        }
    }
}
