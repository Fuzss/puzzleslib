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
    @Unique
    private DefaultedInt puzzleslib$charge;

    public BowItemFabricMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged, CallbackInfo callback, Player player, boolean hasInfiniteAmmo, ItemStack projectileStack) {
        this.puzzleslib$charge = DefaultedInt.fromValue(this.getUseDuration(stack) - timeCharged);
        if (FabricPlayerEvents.ARROW_LOOSE.invoker().onArrowLoose(player, stack, level, this.puzzleslib$charge, !projectileStack.isEmpty() || hasInfiniteAmmo).isInterrupt()) {
            callback.cancel();
        }
    }

    @ModifyVariable(method = "releaseUsing", at = @At("STORE"), ordinal = 1)
    public int releaseUsing(int charge) {
        Objects.requireNonNull(this.puzzleslib$charge, "charge is null");
        charge = this.puzzleslib$charge.getAsOptionalInt().orElse(charge);
        this.puzzleslib$charge = null;
        return charge;
    }
}
