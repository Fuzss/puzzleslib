package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public final class ClientEntityLevelEvents {
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<Unload> UNLOAD = EventInvoker.lookup(Unload.class);

    private ClientEntityLevelEvents() {

    }

    @FunctionalInterface
    public interface Load {

        /**
         * Fired when an entity is added to the level on the client.
         *
         * @param entity    the entity that is being loaded
         * @param level     the level the entity is loaded in
         * @return {@link EventResult#INTERRUPT} to prevent the entity from being added to the level (on Fabric the entity will instead just immediately be removed again),
         * {@link EventResult#PASS} for the entity to be added normally
         */
        EventResult onEntityLoad(Entity entity, ClientLevel level);
    }

    @FunctionalInterface
    public interface Unload {

        /**
         * Fired when an entity is removed from the level on the client.
         *
         * @param entity    the entity that is being unloaded
         * @param level     the level the entity is unloaded in
         */
        void onEntityUnload(Entity entity, ClientLevel level);
    }
}
