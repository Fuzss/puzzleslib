package fuzs.puzzleslib.api.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.List;

public final class ExplosionEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<Detonate> DETONATE = EventInvoker.lookup(Detonate.class);

    private ExplosionEvents() {

    }

    @FunctionalInterface
    public interface Start {

        /**
         * Called just before an {@link Explosion} is about to be executed for a level, allows for preventing that explosion.
         *
         * @param level     the level the explosion is happening in
         * @param explosion the explosion that is about to start
         * @return {@link EventResult#INTERRUPT} prevents to explosion from happening,
         * {@link EventResult#PASS} allows the explosion to continue forward
         */
        EventResult onExplosionStart(Level level, Explosion explosion);
    }

    @FunctionalInterface
    public interface Detonate {

        /**
         * Called just before entities affected by an ongoing explosion are processed, meaning before they are hurt and knocked back.
         *
         * @param level            the level the explosion is happening in
         * @param explosion        the explosion that is about to detonate
         * @param affectedBlocks   the block positions affected by this explosion, modify the list to change the effects on entities
         * @param affectedEntities the entities affected by this explosion, modify the list to change the effects on entities
         */
        void onExplosionDetonate(Level level, Explosion explosion, List<BlockPos> affectedBlocks, List<Entity> affectedEntities);
    }
}
