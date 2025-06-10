package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.impl.event.GrindstoneExperienceHolder;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
abstract class GrindstoneMenu$ResultSlotFabricMixin extends Slot {
    @Shadow
    @Final
    private GrindstoneMenu this$0;

    public GrindstoneMenu$ResultSlotFabricMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(method = "getExperienceAmount", at = @At("HEAD"), cancellable = true)
    private void getExperienceAmount(Level level, CallbackInfoReturnable<Integer> callback) {
        ((GrindstoneExperienceHolder) this.this$0).puzzleslib$getExperiencePointReward()
                .ifPresent(callback::setReturnValue);
    }
}
