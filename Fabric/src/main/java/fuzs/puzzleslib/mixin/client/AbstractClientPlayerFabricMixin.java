package fuzs.puzzleslib.mixin.client;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.client.event.v1.FabricClientEvents;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
abstract class AbstractClientPlayerFabricMixin extends Player {

    public AbstractClientPlayerFabricMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(method = "getFieldOfViewModifier", at = @At("TAIL"), cancellable = true)
    public void getFieldOfViewModifier(CallbackInfoReturnable<Float> callback) {
        final float fovEffectScale = Minecraft.getInstance().options.fovEffectScale().get().floatValue();
        // if fov effects don't apply due to the option being set to 0 no need to fire the event
        if (fovEffectScale == 0.0F) return;
        // reverse fovEffectScale calculations applied by vanilla in return statement,
        // we could capture the original value previous to return, but this approach only needs one mixin
        DefaultedFloat fieldOfViewModifier = DefaultedFloat.fromValue((callback.getReturnValueF() - 1.0F) / fovEffectScale + 1.0F);
        FabricClientEvents.COMPUTE_FOV_MODIFIER.invoker().onComputeFovModifier(this, fieldOfViewModifier);
        fieldOfViewModifier.getAsOptionalFloat().map(value -> Mth.lerp(fovEffectScale, 1.0F, value)).ifPresent(callback::setReturnValue);
    }
}
