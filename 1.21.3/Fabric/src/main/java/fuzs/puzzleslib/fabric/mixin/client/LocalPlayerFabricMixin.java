package fuzs.puzzleslib.fabric.mixin.client;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.fabric.api.client.event.v1.FabricClientPlayerEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LocalPlayer.class)
abstract class LocalPlayerFabricMixin extends AbstractClientPlayer {
    @Shadow
    public Input input;
    @Unique
    private DefaultedValue<Holder<SoundEvent>> puzzleslib$sound;
    @Unique
    private DefaultedFloat puzzleslib$volume;
    @Unique
    private DefaultedFloat puzzleslib$pitch;

    public LocalPlayerFabricMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V", shift = At.Shift.AFTER))
    public void aiStep(CallbackInfo callback) {
        FabricClientPlayerEvents.MOVEMENT_INPUT_UPDATE.invoker().onMovementInputUpdate(LocalPlayer.class.cast(this), this.input);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        EventResult result = FabricLivingEvents.LIVING_ATTACK.invoker().onLivingAttack(this, source, amount);
        if (result.isInterrupt()) callback.setReturnValue(false);
    }

    @Inject(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
    public void playSound$0(SoundEvent soundEvent, float volume, float pitch, CallbackInfo callback) {
        this.puzzleslib$sound = DefaultedValue.fromValue(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(soundEvent));
        this.puzzleslib$volume = DefaultedFloat.fromValue(volume);
        this.puzzleslib$pitch = DefaultedFloat.fromValue(pitch);
        EventResult result = FabricLevelEvents.PLAY_LEVEL_SOUND_AT_ENTITY.invoker().onPlaySoundAtEntity(this.level(), this, this.puzzleslib$sound, MutableValue.fromValue(this.getSoundSource()), this.puzzleslib$volume, this.puzzleslib$pitch);
        if (result.isInterrupt() || this.puzzleslib$sound.get() == null) callback.cancel();
    }

    @ModifyVariable(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), ordinal = 0)
    public SoundEvent playSound$1(SoundEvent soundEvent) {
        Objects.requireNonNull(this.puzzleslib$sound, "sound is null");
        soundEvent = this.puzzleslib$sound.getAsOptional().map(Holder::value).orElse(soundEvent);
        this.puzzleslib$sound = null;
        return soundEvent;
    }

    @ModifyVariable(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), ordinal = 0)
    public float playSound$3(float volume) {
        Objects.requireNonNull(this.puzzleslib$volume, "sound is null");
        volume = this.puzzleslib$volume.getAsOptionalFloat().orElse(volume);
        this.puzzleslib$volume = null;
        return volume;
    }

    @ModifyVariable(method = "playSound(Lnet/minecraft/sounds/SoundEvent;FF)V", at = @At("HEAD"), ordinal = 1)
    public float playSound$4(float pitch) {
        Objects.requireNonNull(this.puzzleslib$pitch, "pitch is null");
        pitch = this.puzzleslib$pitch.getAsOptionalFloat().orElse(pitch);
        this.puzzleslib$pitch = null;
        return pitch;
    }
}
