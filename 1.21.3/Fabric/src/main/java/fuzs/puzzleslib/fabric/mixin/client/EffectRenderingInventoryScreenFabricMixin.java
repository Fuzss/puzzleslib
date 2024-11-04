package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedBoolean;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricGuiEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(EffectRenderingInventoryScreen.class)
abstract class EffectRenderingInventoryScreenFabricMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    @Unique
    private DefaultedBoolean puzzleslib$smallWidgets;
    @Unique
    private DefaultedInt puzzleslib$horizontalOffset;

    public EffectRenderingInventoryScreenFabricMixin(T abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I", ordinal = 0), ordinal = 0)
    private boolean renderEffects$0(boolean fullSize) {
        this.puzzleslib$smallWidgets = DefaultedBoolean.fromValue(!fullSize);
        return fullSize;
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I", ordinal = 0), ordinal = 2)
    private int renderEffects$1(int horizontalOffset) {
        this.puzzleslib$horizontalOffset = DefaultedInt.fromValue(horizontalOffset);
        return horizontalOffset;
    }

    @Inject(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I", ordinal = 0), cancellable = true)
    private void renderEffects$2(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo callback) {
        int i = this.leftPos + this.imageWidth + 2;
        int j = this.width - i;
        Objects.requireNonNull(this.puzzleslib$smallWidgets, "full size rendering is null");
        Objects.requireNonNull(this.puzzleslib$horizontalOffset, "horizontal offset is null");
        EventResult result = FabricGuiEvents.INVENTORY_MOB_EFFECTS.invoker().onInventoryMobEffects(this, j, this.puzzleslib$smallWidgets, this.puzzleslib$horizontalOffset);
        if (result.isInterrupt()) {
            this.puzzleslib$smallWidgets = null;
            this.puzzleslib$horizontalOffset = null;
            callback.cancel();
        }
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I", ordinal = 0), ordinal = 0)
    private boolean renderEffects$3(boolean fullSize) {
        Objects.requireNonNull(this.puzzleslib$smallWidgets, "full size rendering is null");
        fullSize = this.puzzleslib$smallWidgets.getAsOptionalBoolean().map(t -> !t).orElse(fullSize);
        this.puzzleslib$smallWidgets = null;
        return fullSize;
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I", ordinal = 0), ordinal = 2)
    private int renderEffects$4(int horizontalOffset) {
        Objects.requireNonNull(this.puzzleslib$horizontalOffset, "horizontal offset is null");
        horizontalOffset = this.puzzleslib$horizontalOffset.getAsOptionalInt().orElse(horizontalOffset);
        this.puzzleslib$horizontalOffset = null;
        return horizontalOffset;
    }

    @ModifyVariable(method = "renderEffects", at = @At("STORE"))
    private List<Component> renderEffects$5(List<Component> lines, @Local(ordinal = 0) MobEffectInstance mobEffectInstance) {
        FabricGuiEvents.GATHER_EFFECT_SCREEN_TOOLTIP.invoker().onGatherEffectScreenTooltip(EffectRenderingInventoryScreen.class.cast(this), mobEffectInstance, lines);
        return lines;
    }
}
