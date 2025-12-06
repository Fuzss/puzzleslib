package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import fuzs.puzzleslib.fabric.impl.event.GrindstoneExperienceHolder;
import fuzs.puzzleslib.fabric.mixin.accessor.GrindstoneMenuFabricAccessor;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4")
abstract class GrindstoneMenu$ResultSlotFabricMixin extends Slot {
    @Shadow
    @Final
    GrindstoneMenu this$0;
    @Nullable
    @Unique
    private DefaultedValue<ItemStack> puzzleslib$topInput;
    @Nullable
    @Unique
    private DefaultedValue<ItemStack> puzzleslib$bottomInput;

    public GrindstoneMenu$ResultSlotFabricMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    public void onTake$0(Player player, ItemStack stack, CallbackInfo callback) {
        Container repairSlots = ((GrindstoneMenuFabricAccessor) this.this$0).puzzleslib$getRepairSlots();
        DefaultedValue<ItemStack> topInput = this.puzzleslib$topInput = DefaultedValue.fromValue(repairSlots.getItem(0));
        DefaultedValue<ItemStack> bottomInput = this.puzzleslib$bottomInput = DefaultedValue.fromValue(repairSlots.getItem(
                1));
        FabricPlayerEvents.GRINDSTONE_USE.invoker().onGrindstoneUse(topInput, bottomInput, player);
    }

    @Inject(method = "onTake",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V",
                    shift = At.Shift.AFTER),
            cancellable = true)
    public void onTake$1(Player player, ItemStack stack, CallbackInfo callback) {
        Objects.requireNonNull(this.puzzleslib$topInput, "top input is null");
        Objects.requireNonNull(this.puzzleslib$bottomInput, "bottom input is null");
        Optional<ItemStack> topInput = this.puzzleslib$topInput.getAsOptional();
        Optional<ItemStack> bottomInput = this.puzzleslib$bottomInput.getAsOptional();
        this.puzzleslib$topInput = this.puzzleslib$bottomInput = null;
        if (topInput.isPresent() || bottomInput.isPresent()) {
            Container repairSlots = ((GrindstoneMenuFabricAccessor) this.this$0).puzzleslib$getRepairSlots();
            repairSlots.setItem(0, topInput.orElse(ItemStack.EMPTY));
            repairSlots.setItem(1, bottomInput.orElse(ItemStack.EMPTY));
            callback.cancel();
        }
    }

    @Inject(method = "getExperienceAmount", at = @At("HEAD"), cancellable = true)
    private void getExperienceAmount(Level level, CallbackInfoReturnable<Integer> callback) {
        int xp = ((GrindstoneExperienceHolder) this.this$0).puzzleslib$getExperience();
        if (xp != -1) callback.setReturnValue(xp);
    }
}
