package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderMan.class)
abstract class EnderManFabricMixin extends Monster {

    protected EnderManFabricMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "isBeingStaredBy", at = @At("TAIL"))
    boolean isBeingStaredBy(boolean isBeingStaredBy, Player player) {
        return isBeingStaredBy && FabricLivingEvents.LOOKING_AT_ENDERMAN.invoker().onLookingAtEnderManCallback(
                EnderMan.class.cast(this), player).isPass();
    }
}
