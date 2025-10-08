package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.fabric.impl.client.key.ActivationContextKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
abstract class KeyMappingFabricMixin implements ActivationContextKeyMapping {
    @Unique
    private KeyActivationContext puzzleslib$keyActivationContext = KeyActivationContext.UNIVERSAL;

    @Inject(method = "isDown", at = @At("HEAD"), cancellable = true)
    public void isDown(CallbackInfoReturnable<Boolean> callback) {
        if (!this.puzzleslib$getKeyActivationContext().isSupportedEnvironment()) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "same", at = @At("HEAD"), cancellable = true)
    public void same(KeyMapping keyMapping, CallbackInfoReturnable<Boolean> callback) {
        if (!this.puzzleslib$getKeyActivationContext()
                .hasConflict(ActivationContextKeyMapping.class.cast(keyMapping)
                        .puzzleslib$getKeyActivationContext())) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    public void matches(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> callback) {
        if (!this.puzzleslib$getKeyActivationContext().isSupportedEnvironment()) {
            callback.setReturnValue(false);
        }
    }

    @Override
    public void puzzleslib$setKeyActivationContext(KeyActivationContext keyActivationContext) {
        this.puzzleslib$keyActivationContext = keyActivationContext;
    }

    @Override
    public KeyActivationContext puzzleslib$getKeyActivationContext() {
        return this.puzzleslib$keyActivationContext;
    }
}
