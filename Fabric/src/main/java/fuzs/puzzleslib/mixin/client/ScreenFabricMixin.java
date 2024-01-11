package fuzs.puzzleslib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.ExtraScreenMouseEvents;
import fuzs.puzzleslib.api.client.event.v1.FabricScreenEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.FabricEventFactory;
import fuzs.puzzleslib.impl.client.event.ExtraScreenExtensions;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

// increase priority to apply before Fabric Api, as we want this to be available in Fabric Api's before-init callback,
// which also fires at the head of vanilla's Screen::init method
@Mixin(value = Screen.class, priority = 500)
abstract class ScreenFabricMixin extends AbstractContainerEventHandler implements ExtraScreenExtensions {
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    protected Font font;
    @Unique
    private Event<ExtraScreenMouseEvents.AllowMouseDrag> puzzleslib$allowMouseDragEvent;
    @Unique
    private Event<ExtraScreenMouseEvents.BeforeMouseDrag> puzzleslib$beforeMouseDragEvent;
    @Unique
    private Event<ExtraScreenMouseEvents.AfterMouseDrag> puzzleslib$afterMouseDragEvent;

    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V", at = @At("HEAD"))
    public void init(Minecraft client, int width, int height, CallbackInfo callback) {
        this.puzzleslib$allowMouseDragEvent = FabricEventFactory.createSimpleResult(ExtraScreenMouseEvents.AllowMouseDrag.class, false);
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
        Objects.requireNonNull(this.puzzleslib$allowMouseDragEvent, "before mouse drag event is null for screen " + this.getClass().getName());
        return this.puzzleslib$beforeMouseDragEvent;
    }

    @Override
    public Event<ExtraScreenMouseEvents.AfterMouseDrag> puzzleslib$getAfterMouseDragEvent() {
        Objects.requireNonNull(this.puzzleslib$allowMouseDragEvent, "after mouse drag event is null for screen " + this.getClass().getName());
        return this.puzzleslib$afterMouseDragEvent;
    }

    @Inject(method = "renderTooltipInternal", at = @At("HEAD"), cancellable = true)
    private void renderTooltipInternal(PoseStack poseStack, List<ClientTooltipComponent> components, int mouseX, int mouseY, CallbackInfo callback) {
        if (components.isEmpty()) return;
        EventResult result = FabricScreenEvents.RENDER_TOOLTIP.invoker().onRenderTooltip(poseStack, mouseX, mouseY, this.width, this.height, this.font, components);
        if (result.isInterrupt()) callback.cancel();
    }
}
