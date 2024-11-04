package fuzs.puzzleslib.api.block.v1;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

import java.util.Objects;

/**
 * An extended {@link SoundType} allowing for easily setting new values.
 */
public class MutableSoundType extends HolderBackedSoundType {

    /**
     * @param volume     sound volume
     * @param pitch      sound pitch
     * @param breakSound break sound
     * @param stepSound  step sound
     * @param placeSound place sound
     * @param hitSound   hit sound
     * @param fallSound  fall sound
     */
    public MutableSoundType(float volume, float pitch, SoundEvent breakSound, SoundEvent stepSound, SoundEvent placeSound, SoundEvent hitSound, SoundEvent fallSound) {
        super(volume,
                pitch,
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(breakSound),
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(stepSound),
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(placeSound),
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(hitSound),
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(fallSound)
        );
    }

    /**
     * @param volume     sound volume
     * @param pitch      sound pitch
     * @param breakSound break sound
     * @param stepSound  step sound
     * @param placeSound place sound
     * @param hitSound   hit sound
     * @param fallSound  fall sound
     */
    public MutableSoundType(float volume, float pitch, Holder<SoundEvent> breakSound, Holder<SoundEvent> stepSound, Holder<SoundEvent> placeSound, Holder<SoundEvent> hitSound, Holder<SoundEvent> fallSound) {
        super(volume, pitch, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    /**
     * @param soundType the original sound type
     * @return mutable copy of sound type
     */
    public static MutableSoundType copyOf(SoundType soundType) {
        Objects.requireNonNull(soundType, "sound type is null");
        if (soundType instanceof HolderBackedSoundType holderBackedSoundType) {
            return new MutableSoundType(holderBackedSoundType.getVolume(),
                    holderBackedSoundType.getPitch(),
                    holderBackedSoundType.breakSound,
                    holderBackedSoundType.stepSound,
                    holderBackedSoundType.placeSound,
                    holderBackedSoundType.hitSound,
                    holderBackedSoundType.fallSound
            );
        } else {
            return new MutableSoundType(soundType.getVolume(),
                    soundType.getPitch(),
                    soundType.getBreakSound(),
                    soundType.getStepSound(),
                    soundType.getPlaceSound(),
                    soundType.getHitSound(),
                    soundType.getFallSound()
            );
        }
    }

    /**
     * @param volume a new volume
     * @return the mutable instance
     */
    public MutableSoundType setVolume(float volume) {
        return new MutableSoundType(volume,
                this.getPitch(),
                this.breakSound,
                this.stepSound,
                this.placeSound,
                this.hitSound,
                this.fallSound
        );
    }

    /**
     * @param pitch a new pitch
     * @return the mutable instance
     */
    public MutableSoundType setPitch(float pitch) {
        return new MutableSoundType(this.getVolume(),
                pitch,
                this.breakSound,
                this.stepSound,
                this.placeSound,
                this.hitSound,
                this.fallSound
        );
    }

    /**
     * @param breakSound a new break sound
     * @return the mutable instance
     */
    public MutableSoundType setBreakSound(SoundEvent breakSound) {
        Objects.requireNonNull(breakSound, "break sound is null");
        return this.setBreakSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(breakSound));
    }

    /**
     * @param breakSound a new break sound
     * @return the mutable instance
     */
    public MutableSoundType setBreakSound(Holder<SoundEvent> breakSound) {
        Objects.requireNonNull(breakSound, "break sound is null");
        return new MutableSoundType(this.getVolume(),
                this.getPitch(),
                breakSound,
                this.stepSound,
                this.placeSound,
                this.hitSound,
                this.fallSound
        );
    }

    /**
     * @param stepSound a new step sound
     * @return the mutable instance
     */
    public MutableSoundType setStepSound(SoundEvent stepSound) {
        Objects.requireNonNull(stepSound, "step sound is null");
        return this.setStepSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(stepSound));
    }

    /**
     * @param stepSound a new step sound
     * @return the mutable instance
     */
    public MutableSoundType setStepSound(Holder<SoundEvent> stepSound) {
        Objects.requireNonNull(stepSound, "step sound is null");
        return new MutableSoundType(this.getVolume(),
                this.getPitch(),
                this.breakSound,
                stepSound,
                this.placeSound,
                this.hitSound,
                this.fallSound
        );
    }

    /**
     * @param placeSound a new place sound
     * @return the mutable instance
     */
    public MutableSoundType setPlaceSound(SoundEvent placeSound) {
        Objects.requireNonNull(placeSound, "place sound is null");
        return this.setPlaceSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(placeSound));
    }

    /**
     * @param placeSound a new place sound
     * @return the mutable instance
     */
    public MutableSoundType setPlaceSound(Holder<SoundEvent> placeSound) {
        Objects.requireNonNull(placeSound, "place sound is null");
        return new MutableSoundType(this.getVolume(),
                this.getPitch(),
                this.breakSound,
                this.stepSound,
                placeSound,
                this.hitSound,
                this.fallSound
        );
    }

    /**
     * @param hitSound a new hit sound
     * @return the mutable instance
     */
    public MutableSoundType setHitSound(SoundEvent hitSound) {
        Objects.requireNonNull(hitSound, "hit sound is null");
        return this.setHitSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(hitSound));
    }

    /**
     * @param hitSound a new hit sound
     * @return the mutable instance
     */
    public MutableSoundType setHitSound(Holder<SoundEvent> hitSound) {
        Objects.requireNonNull(hitSound, "hit sound is null");
        return new MutableSoundType(this.getVolume(),
                this.getPitch(),
                this.breakSound,
                this.stepSound,
                this.placeSound,
                hitSound,
                this.fallSound
        );
    }

    /**
     * @param fallSound a new fall sound
     * @return the mutable instance
     */
    public MutableSoundType setFallSound(SoundEvent fallSound) {
        Objects.requireNonNull(fallSound, "fall sound is null");
        return this.setFallSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(fallSound));
    }

    /**
     * @param fallSound a new fall sound
     * @return the mutable instance
     */
    public MutableSoundType setFallSound(Holder<SoundEvent> fallSound) {
        Objects.requireNonNull(fallSound, "fall sound is null");
        return new MutableSoundType(this.getVolume(),
                this.getPitch(),
                this.breakSound,
                this.stepSound,
                this.placeSound,
                this.hitSound,
                fallSound
        );
    }
}
