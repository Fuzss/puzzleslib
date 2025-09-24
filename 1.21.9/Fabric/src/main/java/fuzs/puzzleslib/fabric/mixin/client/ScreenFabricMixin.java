package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.api.client.event.v1.AfterBackgroundCallback;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.fabric.impl.client.event.ExtraScreenExtensions;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Screen.class)
abstract class ScreenFabricMixin extends AbstractContainerEventHandler implements ExtraScreenExtensions {
    @Unique
    private Event<AfterBackgroundCallback> puzzleslib$afterBackgroundEvent;

    @Inject(method = "renderWithTooltipAndSubtitles",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
                    shift = At.Shift.AFTER))
    public void renderWithTooltipAndSubtitles(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback) {
        AfterBackgroundCallback.afterBackground(Screen.class.cast(this))
                .invoker()
                .onAfterBackground(Screen.class.cast(this), guiGraphics, mouseX, mouseY, partialTick);
    }

    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("HEAD"))
    public void init(Minecraft client, int width, int height, CallbackInfo callback) {
        this.puzzleslib$afterBackgroundEvent = FabricEventFactory.create(AfterBackgroundCallback.class);
    }

    @Override
    public Event<AfterBackgroundCallback> puzzleslib$getAfterBackgroundEvent() {
        Objects.requireNonNull(this.puzzleslib$afterBackgroundEvent,
                "after background event is null for screen " + this.getClass().getName());
        return this.puzzleslib$afterBackgroundEvent;
    }
}
