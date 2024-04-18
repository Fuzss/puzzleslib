package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@Mixin(BowItem.class)
abstract class BowItemFabricMixin extends ProjectileWeaponItem {

    public BowItemFabricMixin(Properties properties) {
        super(properties);
    }

@Inject(
        method = "releaseUsing",
        at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
                ordinal = 0,
                shift = At.Shift.BEFORE
        ),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
)
public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged, CallbackInfo callback, Player player, boolean hasInfiniteAmmo, ItemStack projectileStack, @Share("charge") LocalRef<DefaultedInt> chargeRef) {
    chargeRef.set(DefaultedInt.fromValue(this.getUseDuration(stack) - timeCharged));
    if (FabricPlayerEvents.ARROW_LOOSE.invoker()
            .onArrowLoose(player, stack, level, chargeRef.get(), !projectileStack.isEmpty() || hasInfiniteAmmo)
            .isInterrupt()) {
        callback.cancel();
    } else {
        // This is where you would normally handle the event if not interrupted
    }
}

    @ModifyVariable(method = "releaseUsing", at = @At("STORE"), ordinal = 1)
    public int releaseUsing(int charge, @Share("charge") LocalRef<DefaultedInt> chargeRef) {
        Objects.requireNonNull(chargeRef.get(), "charge is null");
        charge = chargeRef.get().getAsOptionalInt().orElse(charge);
        return charge;
    }
}
