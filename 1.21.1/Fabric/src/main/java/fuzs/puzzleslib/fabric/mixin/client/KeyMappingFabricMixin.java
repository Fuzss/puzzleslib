package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
abstract class KeyMappingFabricMixin {

    @Inject(method = "isDown", at = @At("HEAD"), cancellable = true)
    public void isDown(CallbackInfoReturnable<Boolean> callback) {
        if (!KeyMappingHelper.INSTANCE.getKeyActivationContext(KeyMapping.class.cast(this)).isActive()) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "same", at = @At("HEAD"), cancellable = true)
    public void same(KeyMapping keyMapping, CallbackInfoReturnable<Boolean> callback) {
        if (!KeyMappingHelper.INSTANCE.isConflictingWith(KeyMapping.class.cast(this), keyMapping)) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    public void matches(int keysym, int scancode, CallbackInfoReturnable<Boolean> callback) {
        if (!KeyMappingHelper.INSTANCE.getKeyActivationContext(KeyMapping.class.cast(this)).isActive()) {
            callback.setReturnValue(false);
        }
    }
}
