package fuzs.puzzleslib.api.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class PlayLevelSoundEvents {
    public static final EventInvoker<AtPosition> POSITION = EventInvoker.lookup(AtPosition.class);
    public static final EventInvoker<AtEntity> ENTITY = EventInvoker.lookup(AtEntity.class);

    private PlayLevelSoundEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface AtPosition {

        /**
         * Called when a sound event is played at a specific position in a level.
         *
         * @param level       the current level
         * @param position    the position the sound is to be played at
         * @param soundEvent  the sound event, can be replaced
         * @param soundSource the sound source
         * @param soundVolume the sound volume
         * @param soundPitch  the sound pitch
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the sound from playing</li>
         *         <li>{@link EventResult#PASS PASS} to allow the sound to play normally</li>
         *         </ul>
         */
        EventResult onPlaySoundAtPosition(Level level, Vec3 position, MutableValue<Holder<SoundEvent>> soundEvent, MutableValue<SoundSource> soundSource, DefaultedFloat soundVolume, DefaultedFloat soundPitch);
    }

    @FunctionalInterface
    public interface AtEntity {

        /**
         * Called when a sound event is played from a specific entity.
         *
         * @param level       the current level
         * @param entity      the entity the sound is playing from
         * @param soundEvent  the sound event, can be replaced
         * @param soundSource the sound source
         * @param soundVolume the sound volume
         * @param soundPitch  the sound pitch
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the sound from playing</li>
         *         <li>{@link EventResult#PASS PASS} to allow the sound to play normally</li>
         *         </ul>
         */
        EventResult onPlaySoundAtEntity(Level level, Entity entity, MutableValue<Holder<SoundEvent>> soundEvent, MutableValue<SoundSource> soundSource, DefaultedFloat soundVolume, DefaultedFloat soundPitch);
    }
}
