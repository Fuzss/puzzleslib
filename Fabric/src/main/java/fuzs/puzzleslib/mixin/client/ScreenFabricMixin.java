package fuzs.puzzleslib.mixin.client;

import fuzs.puzzleslib.api.client.event.v1.ExtraScreenMouseEvents;
import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.impl.client.event.ExtraScreenExtensions;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.Minecraft;
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
    private Event<ExtraScreenMouseEvents.AllowMouseDrag> allowMouseDragEvent;
    @Unique
    private Event<ExtraScreenMouseEvents.BeforeMouseDrag> beforeMouseDragEvent;
    @Unique
    private Event<ExtraScreenMouseEvents.AfterMouseDrag> afterMouseDragEvent;

    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("HEAD"))
    public void init(Minecraft client, int width, int height, CallbackInfo callback) {
        this.allowMouseDragEvent = FabricEventFactory.createSimpleResult(ExtraScreenMouseEvents.AllowMouseDrag.class, false);
        this.beforeMouseDragEvent = FabricEventFactory.create(ExtraScreenMouseEvents.BeforeMouseDrag.class);
        this.afterMouseDragEvent = FabricEventFactory.create(ExtraScreenMouseEvents.AfterMouseDrag.class);
    }

    @Override
    public Event<ExtraScreenMouseEvents.AllowMouseDrag> puzzleslib$getAllowMouseDragEvent() {
        Objects.requireNonNull(this.allowMouseDragEvent, "allow mouse drag event is null for screen " + this.getClass().getName());
        return this.allowMouseDragEvent;
    }

    @Override
    public Event<ExtraScreenMouseEvents.BeforeMouseDrag> puzzleslib$getBeforeMouseDragEvent() {
        Objects.requireNonNull(this.allowMouseDragEvent, "before mouse drag event is null for screen " + this.getClass().getName());
        return this.beforeMouseDragEvent;
    }

    @Override
    public Event<ExtraScreenMouseEvents.AfterMouseDrag> puzzleslib$getAfterMouseDragEvent() {
        Objects.requireNonNull(this.allowMouseDragEvent, "after mouse drag event is null for screen " + this.getClass().getName());
        return this.afterMouseDragEvent;
    }
}
