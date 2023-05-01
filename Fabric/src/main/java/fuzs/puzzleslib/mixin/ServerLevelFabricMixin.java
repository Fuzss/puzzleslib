package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
abstract class ServerLevelFabricMixin extends Level {
    @Unique
    private DefaultedValue<Holder<SoundEvent>> puzzleslib$sound;
    @Unique
    private DefaultedValue<SoundSource> puzzleslib$source;
    @Unique
    private DefaultedFloat puzzleslib$volume;
    @Unique
    private DefaultedFloat puzzleslib$pitch;

    protected ServerLevelFabricMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, holder, supplier, bl, bl2, l, i);
    }

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"), cancellable = true)
    public void playSeededSound$0(@Nullable Player player, double x, double y, double z, Holder<SoundEvent> soundEvent, SoundSource source, float volume, float pitch, long seed, CallbackInfo callback) {
        this.puzzleslib$sound = DefaultedValue.fromValue(soundEvent);
        this.puzzleslib$source = DefaultedValue.fromValue(source);
        this.puzzleslib$volume = DefaultedFloat.fromValue(volume);
        this.puzzleslib$pitch = DefaultedFloat.fromValue(pitch);
        EventResult result = FabricEvents.PLAY_LEVEL_SOUND_AT_POSITION.invoker().onPlaySoundAtPosition(this, new Vec3(x, y, z), this.puzzleslib$sound, this.puzzleslib$source, this.puzzleslib$volume, this.puzzleslib$pitch);
        if (result.isInterrupt() || this.puzzleslib$sound.get() == null) callback.cancel();
    }

    @Inject(method = "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", at = @At("HEAD"), cancellable = true)
    public void playSeededSound$0(@Nullable Player player, Entity entity, Holder<SoundEvent> soundEvent, SoundSource source, float volume, float pitch, long seed, CallbackInfo callback) {
        this.puzzleslib$sound = DefaultedValue.fromValue(soundEvent);
        this.puzzleslib$source = DefaultedValue.fromValue(source);
        this.puzzleslib$volume = DefaultedFloat.fromValue(volume);
        this.puzzleslib$pitch = DefaultedFloat.fromValue(pitch);
        EventResult result = FabricEvents.PLAY_LEVEL_SOUND_AT_ENTITY.invoker().onPlaySoundAtEntity(this, entity, this.puzzleslib$sound, this.puzzleslib$source, this.puzzleslib$volume, this.puzzleslib$pitch);
        if (result.isInterrupt() || this.puzzleslib$sound.get() == null) callback.cancel();
    }

    @ModifyVariable(method = {"playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"}, at = @At("HEAD"), ordinal = 0)
    public Holder<SoundEvent> playSeededSound$1(Holder<SoundEvent> soundEvent) {
        Objects.requireNonNull(this.puzzleslib$sound, "sound is null");
        soundEvent = this.puzzleslib$sound.getAsOptional().orElse(soundEvent);
        this.puzzleslib$sound = null;
        return soundEvent;
    }

    @ModifyVariable(method = {"playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"}, at = @At("HEAD"), ordinal = 0)
    public SoundSource playSeededSound$2(SoundSource source) {
        Objects.requireNonNull(this.puzzleslib$source, "source is null");
        source = this.puzzleslib$source.getAsOptional().orElse(source);
        this.puzzleslib$source = null;
        return source;
    }

    @ModifyVariable(method = {"playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"}, at = @At("HEAD"), ordinal = 0)
    public float playSeededSound$3(float volume) {
        Objects.requireNonNull(this.puzzleslib$volume, "sound is null");
        volume = this.puzzleslib$volume.getAsOptionalFloat().orElse(volume);
        this.puzzleslib$volume = null;
        return volume;
    }

    @ModifyVariable(method = {"playSeededSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V", "playSeededSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;FFJ)V"}, at = @At("HEAD"), ordinal = 1)
    public float playSeededSound$4(float pitch) {
        Objects.requireNonNull(this.puzzleslib$pitch, "pitch is null");
        pitch = this.puzzleslib$pitch.getAsOptionalFloat().orElse(pitch);
        this.puzzleslib$pitch = null;
        return pitch;
    }
}
