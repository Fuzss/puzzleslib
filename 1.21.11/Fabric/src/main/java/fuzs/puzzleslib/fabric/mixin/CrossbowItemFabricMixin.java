package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
abstract class CrossbowItemFabricMixin extends ProjectileWeaponItem {

    public CrossbowItemFabricMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "performShooting", at = @At("HEAD"), cancellable = true)
    public void performShooting(Level level, LivingEntity shooter, InteractionHand usedHand, ItemStack weapon, float velocity, float inaccuracy, @Nullable LivingEntity target, CallbackInfo callback) {
        if (level instanceof ServerLevel && shooter instanceof Player player) {
            EventResult result = FabricPlayerEvents.ARROW_LOOSE.invoker().onArrowLoose(player, weapon, level, MutableInt.fromValue(1), true);
            if (result.isInterrupt()) callback.cancel();
        }
    }
}
