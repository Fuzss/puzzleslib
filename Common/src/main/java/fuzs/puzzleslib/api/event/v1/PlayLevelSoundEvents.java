package fuzs.puzzleslib.api.event.v1;

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

    }

    @FunctionalInterface
    public interface AtPosition {

        /**
         * Called when a sound event is played at a specific position in the world, allows for cancelling the sound.
         *
         * @param level         the current level
         * @param position      the position the sound is to be played at
         * @param sound         the sound event, can be exchanged here
         * @param source        sound category
         * @param volume        volume
         * @param pitch         pitch
         * @return              if present the sound will be cancelled
         */
        EventResult onPlaySoundAtPosition(Level level, Vec3 position, MutableValue<Holder<SoundEvent>> sound, MutableValue<SoundSource> source, DefaultedFloat volume, DefaultedFloat pitch);
    }

    @FunctionalInterface
    public interface AtEntity {

        /**
         * Called when a sound event is played at a specific entity, allows for cancelling the sound.
         *
         * @param level         the current level
         * @param entity        the entity the sound is playing from
         * @param sound         the sound event, can be exchanged here
         * @param source        sound category
         * @param volume        volume
         * @param pitch         pitch
         * @return              if present the sound will be cancelled
         */
        EventResult onPlaySoundAtEntity(Level level, Entity entity, MutableValue<Holder<SoundEvent>> sound, MutableValue<SoundSource> source, DefaultedFloat volume, DefaultedFloat pitch);
    }
}
