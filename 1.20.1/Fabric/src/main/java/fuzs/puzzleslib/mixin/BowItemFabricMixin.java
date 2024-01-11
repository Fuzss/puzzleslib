package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricPlayerEvents;
import fuzs.puzzleslib.api.event.v1.data.DefaultedInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@Mixin(BowItem.class)
abstract class BowItemFabricMixin extends ProjectileWeaponItem {
    // accessed on both render and server threads, and since we remove it again after charge has been set to local variable this is necessary
    @Unique
    private final ThreadLocal<DefaultedInt> puzzleslib$charge = new ThreadLocal<>();

    public BowItemFabricMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged, CallbackInfo callback, Player player, boolean hasInfiniteAmmo, ItemStack projectileStack) {
        this.puzzleslib$charge.set(DefaultedInt.fromValue(this.getUseDuration(stack) - timeCharged));
        if (FabricPlayerEvents.ARROW_LOOSE.invoker().onArrowLoose(player, stack, level, this.puzzleslib$charge.get(), !projectileStack.isEmpty() || hasInfiniteAmmo).isInterrupt()) {
            callback.cancel();
        }
    }

    @ModifyVariable(method = "releaseUsing", at = @At("STORE"), ordinal = 1)
    public int releaseUsing(int charge) {
        Objects.requireNonNull(this.puzzleslib$charge.get(), "charge is null");
        charge = this.puzzleslib$charge.get().getAsOptionalInt().orElse(charge);
        this.puzzleslib$charge.remove();
        return charge;
    }
}
