package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
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
    private static void performShooting(Level level, LivingEntity shooter, InteractionHand usedHand, ItemStack crossbowStack, float velocity, float inaccuracy, CallbackInfo callback) {
        if (!(shooter instanceof Player player)) return;
        EventResult result = FabricPlayerEvents.ARROW_LOOSE.invoker().onArrowLoose(player, crossbowStack, level, MutableInt.fromValue(1), true);
        if (result.isInterrupt()) callback.cancel();
    }
}
