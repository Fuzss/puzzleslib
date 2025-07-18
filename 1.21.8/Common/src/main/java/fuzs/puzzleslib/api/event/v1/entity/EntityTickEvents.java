package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.Entity;

public final class EntityTickEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<End> END = EventInvoker.lookup(End.class);

    private EntityTickEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Start {

        /**
         * Called before {@link Entity#tick()}, allows cancelling ticking the entity.
         *
         * @param entity the entity being ticked
         * @return {@link EventResult#INTERRUPT} to prevent the <code>entity</code> from ticking,
         * {@link EventResult#PASS} to allow vanilla logic to continue
         */
        EventResult onStartEntityTick(Entity entity);
    }

    @FunctionalInterface
    public interface End {

        /**
         * Called after {@link Entity#tick()}.
         *
         * @param entity the entity being ticked
         */
        void onEndEntityTick(Entity entity);
    }
}
