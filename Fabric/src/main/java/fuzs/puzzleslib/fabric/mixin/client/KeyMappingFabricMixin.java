package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.fabric.impl.client.key.ActivationContextKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(KeyMapping.class)
abstract class KeyMappingFabricMixin implements ActivationContextKeyMapping {
    @Nullable @Unique private KeyActivationContext puzzleslib$keyActivationContext;

    @Inject(method = "isDown", at = @At("HEAD"), cancellable = true)
    public void isDown(CallbackInfoReturnable<Boolean> callback) {
        if (!this.puzzleslib$getKeyActivationContext().isSupportedEnvironment()) {
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
    public void puzzleslib$setKeyActivationContext(KeyActivationContext context) {
        Objects.requireNonNull(context, "context is null");
        this.puzzleslib$keyActivationContext = context;
    }

    @Override
    public KeyActivationContext puzzleslib$getKeyActivationContext() {
        return this.puzzleslib$keyActivationContext != null ? this.puzzleslib$keyActivationContext :
                KeyActivationContext.UNIVERSAL;
    }
}
