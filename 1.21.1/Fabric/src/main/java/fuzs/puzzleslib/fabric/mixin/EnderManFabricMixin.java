package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.class)
abstract class EnderManFabricMixin extends Monster {

    protected EnderManFabricMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "isLookingAtMe", at = @At("HEAD"), cancellable = true)
    void isLookingAtMe(Player player, CallbackInfoReturnable<Boolean> callback) {
        EventResult result = FabricLivingEvents.LOOKING_AT_ENDERMAN.invoker()
                .onLookingAtEnderManCallback(EnderMan.class.cast(this), player);
        if (result.isInterrupt()) callback.setReturnValue(false);
    }
}
