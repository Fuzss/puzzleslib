package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
abstract class KeyMappingFabricMixin {

    @Inject(method = "same", at = @At("HEAD"), cancellable = true)
    public void same(KeyMapping keyMapping, CallbackInfoReturnable<Boolean> callback) {
        if (!KeyMappingHelper.INSTANCE.conflictsWith(KeyMapping.class.cast(this), keyMapping)) callback.setReturnValue(false);
    }
}
