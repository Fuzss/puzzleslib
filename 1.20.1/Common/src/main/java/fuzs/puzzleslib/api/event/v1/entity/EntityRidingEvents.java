package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public final class EntityRidingEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<Stop> STOP = EventInvoker.lookup(Stop.class);

    private EntityRidingEvents() {

    }

    @FunctionalInterface
    public interface Start {

        /**
         * Runs when an entity starts riding another entity in {@link Entity#startRiding(Entity, boolean)}, allows for preventing that.
         *
         * @param level   the level both entities are in
         * @param rider   the rider trying to start riding the vehicle
         * @param vehicle the vehicle the rider is trying start riding
         * @return {@link EventResult#INTERRUPT} to prevent the rider from starting to ride on the vehicle,
         * {@link EventResult#PASS} to allow vanilla behavior to continue and for riding to begin
         */
        EventResult onStartRiding(Level level, Entity rider, Entity vehicle);
    }

    @FunctionalInterface
    public interface Stop {

        /**
         * Runs when an entity stops riding another entity in {@link Entity#removeVehicle()}, allows for preventing that.
         *
         * @param level   the level both entities are in
         * @param rider   the rider trying to stop riding the vehicle
         * @param vehicle the vehicle the rider is trying stop riding
         * @return {@link EventResult#INTERRUPT} to prevent the rider from dismounting,
         * {@link EventResult#PASS} to allow vanilla behavior to continue and for riding to stop
         */
        EventResult onStopRiding(Level level, Entity rider, Entity vehicle);
    }
}
