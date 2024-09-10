package fuzs.puzzleslib.mixin.server;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.server.Eula;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Eula.class)
abstract class EulaMixin {

    @Inject(method = "readFile", at = @At("HEAD"), cancellable = true)
    private void readFile(CallbackInfoReturnable<Boolean> callback) {
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) callback.setReturnValue(true);
    }
}
