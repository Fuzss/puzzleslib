package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Gui.class)
abstract class GuiFabricMixin {
    @Shadow
    @Nullable
    private Screen screen;

    @ModifyVariable(method = "setScreen",
                    at = @At(value = "LOAD", ordinal = 0),
                    slice = @Slice(from = @At(value = "INVOKE",
                                              target = "Lnet/minecraft/client/player/LocalPlayer;respawn()V")),
                    ordinal = 0,
                    argsOnly = true)
    public Screen setScreen(@Nullable Screen screen) {
        // this implementation does not allow for cancelling a new screen being set,
        // due to vanilla's Screen::remove call happening before the new screen is properly computed (in regard to title &amp; death screens),
        // making the implementation difficult
        return FabricGuiEvents.SCREEN_OPENING.invoker()
                .onScreenOpening(this.screen, screen)
                .getInterrupt()
                .orElse(screen);
    }
}
