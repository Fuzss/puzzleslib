package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.impl.client.PuzzlesLibClient;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
abstract class OptionsFabricMixin {

    @Inject(method = "load", at = @At("HEAD"))
    public void load(CallbackInfo callback) {
        PuzzlesLibClient.initializeGameOptions(Options.class.cast(this));
    }
}
