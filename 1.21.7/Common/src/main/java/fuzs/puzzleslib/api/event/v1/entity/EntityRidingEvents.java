package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class EntityRidingEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<Stop> STOP = EventInvoker.lookup(Stop.class);

    private EntityRidingEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Start {

        /**
         * Runs when an entity starts riding another entity in {@link Entity#startRiding(Entity, boolean)}, allows for
         * preventing that.
         *
         * @param level           the level both entities are in
         * @param passengerEntity the rider trying to start riding the vehicle
         * @param vehicleEntity   the vehicle the rider is trying start riding
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the rider from starting to ride on the vehicle</li>
         *         <li>{@link EventResult#PASS PASS} to allow vanilla behavior to continue and for riding to begin</li>
         *         </ul>
         */
        EventResult onStartRiding(Level level, Entity passengerEntity, Entity vehicleEntity);
    }

    @FunctionalInterface
    public interface Stop {

        /**
         * Runs when an entity stops riding another entity in {@link Entity#removeVehicle()}, allows for preventing
         * that.
         *
         * @param level           the level both entities are in
         * @param passengerEntity the rider trying to stop riding the vehicle
         * @param vehicleEntity   the vehicle the rider is trying stop riding
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the rider from dismounting</li>
         *         <li>{@link EventResult#PASS PASS} to allow vanilla behavior to continue and for riding to stop</li>
         *         </ul>
         */
        EventResult onStopRiding(Level level, Entity passengerEntity, Entity vehicleEntity);
    }
}
