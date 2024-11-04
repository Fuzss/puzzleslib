package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
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
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BowItem.class)
abstract class BowItemFabricMixin extends ProjectileWeaponItem {

    public BowItemFabricMixin(Properties properties) {
        super(properties);
    }

    @ModifyVariable(method = "releaseUsing", at = @At("STORE"), ordinal = 1)
    public int releaseUsing(int chargeValue, ItemStack bow, Level level, LivingEntity livingEntity, int timeCharged, @Local(
            ordinal = 1
    ) ItemStack ammo) {
        DefaultedInt charge = DefaultedInt.fromValue(this.getUseDuration(bow, livingEntity) - timeCharged);
        if (FabricPlayerEvents.ARROW_LOOSE.invoker()
                .onArrowLoose((Player) livingEntity, bow, level, charge, !ammo.isEmpty())
                .isInterrupt()) {
            // returning zero will effectively cancel the method as it won't process for a charge too low
            return 0;
        } else {
            return charge.getAsOptionalInt().orElse(chargeValue);
        }
    }
}
