package fuzs.puzzleslib.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientPlayerEvents;
import fuzs.puzzleslib.impl.event.data.DefaultedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayer.class)
abstract class AbstractClientPlayerFabricMixin extends Player {

    public AbstractClientPlayerFabricMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @ModifyReturnValue(method = "getFieldOfViewModifier", at = @At("TAIL"))
    public float getFieldOfViewModifier(float scaledFieldOfView, @Local(ordinal = 0) float fieldOfView) {
        float fovEffectScale = Minecraft.getInstance().options.fovEffectScale().get().floatValue();
        // if fov effects don't apply due to the option being set to 0, so no need to fire the event
        if (fovEffectScale != 0.0F) {
            // reverse fovEffectScale calculations applied by vanilla in return statement,
            // we could capture the original value previous to return, but this approach only needs one mixin
            DefaultedFloat fieldOfViewModifier = DefaultedFloat.fromValue(fieldOfView);
            FabricClientPlayerEvents.COMPUTE_FOV_MODIFIER.invoker().onComputeFovModifier(this, fieldOfViewModifier);
            return fieldOfViewModifier.getAsOptionalFloat()
                    .map(value -> Mth.lerp(fovEffectScale, 1.0F, value))
                    .orElse(scaledFieldOfView);
        } else {
            return scaledFieldOfView;
        }
    }
}
