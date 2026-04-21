package fuzs.puzzleslib.api.block.v1;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

/**
 * An extended {@link SoundType} allowing for using {@link Holder Holders}, to allow for using
 * {@link SoundEvent SoundEvents} that have not necessarily been registered yet.
 */
public class HolderBackedSoundType extends SoundType {
    final Holder<SoundEvent> breakSound;
    final Holder<SoundEvent> stepSound;
    final Holder<SoundEvent> placeSound;
    final Holder<SoundEvent> hitSound;
    final Holder<SoundEvent> fallSound;

    /**
     * @param volume     sound volume
     * @param pitch      sound pitch
     * @param breakSound break sound
     * @param stepSound  step sound
     * @param placeSound place sound
     * @param hitSound   hit sound
     * @param fallSound  fall sound
     */
    public HolderBackedSoundType(float volume, float pitch, Holder<SoundEvent> breakSound, Holder<SoundEvent> stepSound, Holder<SoundEvent> placeSound, Holder<SoundEvent> hitSound, Holder<SoundEvent> fallSound) {
        super(volume, pitch, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY, SoundEvents.EMPTY);
        this.breakSound = breakSound;
        this.stepSound = stepSound;
        this.placeSound = placeSound;
        this.hitSound = hitSound;
        this.fallSound = fallSound;
    }

    @Override
    public SoundEvent getBreakSound() {
        return this.breakSound.value();
    }

    @Override
    public SoundEvent getStepSound() {
        return this.stepSound.value();
    }

    @Override
    public SoundEvent getPlaceSound() {
        return this.placeSound.value();
    }

    @Override
    public SoundEvent getHitSound() {
        return this.hitSound.value();
    }

    @Override
    public SoundEvent getFallSound() {
        return this.fallSound.value();
    }
}
