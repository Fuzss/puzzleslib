package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.api.client.event.v1.ExtraScreenMouseEvents;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import fuzs.puzzleslib.fabric.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.fabric.impl.client.event.ExtraScreenExtensions;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

// increase priority to apply before Fabric Api, as we want this to be available in Fabric Api's before-init callback,
// which also fires at the head of vanilla's Screen::init method
@Mixin(value = Screen.class, priority = 500)
abstract class ScreenFabricMixin extends AbstractContainerEventHandler implements ExtraScreenExtensions {
    @Unique
    private Event<ExtraScreenMouseEvents.AllowMouseDrag> puzzleslib$allowMouseDragEvent;
    @Unique
    private Event<ExtraScreenMouseEvents.BeforeMouseDrag> puzzleslib$beforeMouseDragEvent;
    @Unique
    private Event<ExtraScreenMouseEvents.AfterMouseDrag> puzzleslib$afterMouseDragEvent;

    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("HEAD"))
    public void init(Minecraft client, int width, int height, CallbackInfo callback) {
        this.puzzleslib$allowMouseDragEvent = FabricEventFactory.createBooleanResult(ExtraScreenMouseEvents.AllowMouseDrag.class, false);
        this.puzzleslib$beforeMouseDragEvent = FabricEventFactory.create(ExtraScreenMouseEvents.BeforeMouseDrag.class);
        this.puzzleslib$afterMouseDragEvent = FabricEventFactory.create(ExtraScreenMouseEvents.AfterMouseDrag.class);
    }

    @Override
    public Event<ExtraScreenMouseEvents.AllowMouseDrag> puzzleslib$getAllowMouseDragEvent() {
        Objects.requireNonNull(this.puzzleslib$allowMouseDragEvent, "allow mouse drag event is null for screen " + this.getClass().getName());
        return this.puzzleslib$allowMouseDragEvent;
    }

    @Override
    public Event<ExtraScreenMouseEvents.BeforeMouseDrag> puzzleslib$getBeforeMouseDragEvent() {
        Objects.requireNonNull(this.puzzleslib$beforeMouseDragEvent, "before mouse drag event is null for screen " + this.getClass().getName());
        return this.puzzleslib$beforeMouseDragEvent;
    }

    @Override
    public Event<ExtraScreenMouseEvents.AfterMouseDrag> puzzleslib$getAfterMouseDragEvent() {
        Objects.requireNonNull(this.puzzleslib$afterMouseDragEvent, "after mouse drag event is null for screen " + this.getClass().getName());
        return this.puzzleslib$afterMouseDragEvent;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo callback) {
        if (AbstractContainerScreen.class.isInstance(this)) {
            FabricGuiEvents.CONTAINER_SCREEN_BACKGROUND.invoker().onDrawBackground(AbstractContainerScreen.class.cast(this), guiGraphics, mouseX, mouseY);
        }
    }
}
