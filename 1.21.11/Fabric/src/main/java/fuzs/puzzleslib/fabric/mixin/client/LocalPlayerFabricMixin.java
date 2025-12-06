package fuzs.puzzleslib.fabric.mixin.client;

import com.google.common.base.Preconditions;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientPlayerEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LocalPlayer.class)
abstract class LocalPlayerFabricMixin extends AbstractClientPlayer {
    @Shadow
    public ClientInput input;

    public LocalPlayerFabricMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(
            method = "aiStep", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;tick()V", shift = At.Shift.AFTER
    )
    )
    public void aiStep(CallbackInfo callback) {
        FabricClientPlayerEvents.MOVEMENT_INPUT_UPDATE.invoker()
                .onMovementInputUpdate(LocalPlayer.class.cast(this), this.input);
    }

    @ModifyArgs(
            method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
    )
    )
    public void playSound(Args args, @Cancellable CallbackInfo callback) {
        Preconditions.checkArgument(args.get(3) instanceof SoundEvent, "sound event is wrong type");
        EventResult eventResult = FabricEventImplHelper.onPlaySound((MutableValue<Holder<SoundEvent>> soundEvent, MutableValue<SoundSource> soundSource, MutableFloat soundVolume, MutableFloat soundPitch) -> {
                    return FabricLevelEvents.PLAY_SOUND_AT_ENTITY.invoker()
                            .onPlaySoundAtEntity(this.level(), this, soundEvent, soundSource, soundVolume, soundPitch);
                }, args, MutableValue.fromEvent((Holder<SoundEvent> holder) -> args.set(3, holder.value()),
                        () -> BuiltInRegistries.SOUND_EVENT.wrapAsHolder(args.get(3))), 4,
                5,
                6);
        if (eventResult.isInterrupt()) callback.cancel();
    }
}
