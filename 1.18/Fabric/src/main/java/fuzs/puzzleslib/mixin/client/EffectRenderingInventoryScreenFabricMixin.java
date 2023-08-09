package fuzs.puzzleslib.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.FabricScreenEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedBoolean;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
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

import java.util.Collection;
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

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void renderEffects$0(PoseStack poseStack, int mouseX, int mouseY, CallbackInfo callback) {
        int i = this.leftPos + this.imageWidth + 2;
        int j = this.width - i;
        Collection<MobEffectInstance> collection = this.minecraft.player.getActiveEffects();
        if (collection.isEmpty() || j < 32) return;
        this.puzzleslib$smallWidgets = DefaultedBoolean.fromValue(j < 120);
        this.puzzleslib$horizontalOffset = DefaultedInt.fromValue(i);
        EventResult result = FabricScreenEvents.INVENTORY_MOB_EFFECTS.invoker().onInventoryMobEffects(this, j, this.puzzleslib$smallWidgets, this.puzzleslib$horizontalOffset);
        if (result.isInterrupt()) callback.cancel();
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I", ordinal = 0), ordinal = 0)
    private boolean renderEffects$1(boolean fullSize) {
        Objects.requireNonNull(this.puzzleslib$smallWidgets, "full size rendering is null");
        fullSize = this.puzzleslib$smallWidgets.getAsOptionalBoolean().map(t -> !t).orElse(fullSize);
        this.puzzleslib$smallWidgets = null;
        return fullSize;
    }

    @ModifyVariable(method = "renderEffects", at = @At(value = "INVOKE", target = "Ljava/util/Collection;size()I", ordinal = 0), ordinal = 2)
    private int renderEffects$2(int horizontalOffset) {
        Objects.requireNonNull(this.puzzleslib$horizontalOffset, "horizontal offset is null");
        horizontalOffset = this.puzzleslib$horizontalOffset.getAsOptionalInt().orElse(horizontalOffset);
        this.puzzleslib$horizontalOffset = null;
        return horizontalOffset;
    }
}
