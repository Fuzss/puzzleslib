package fuzs.puzzleslib.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Minecraft.class)
abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> {

    public MinecraftMixin(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    @ModifyArg(method = "runTick",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/sounds/SoundManager;pauseAllExcept([Lnet/minecraft/sounds/SoundSource;)V"))
    private SoundSource[] runTick(SoundSource[] soundSources) {
        return ArrayUtils.removeElement(soundSources, SoundSource.MUSIC);
    }
}
