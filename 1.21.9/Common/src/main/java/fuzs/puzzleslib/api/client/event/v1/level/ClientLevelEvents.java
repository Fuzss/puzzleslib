package fuzs.puzzleslib.api.client.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ClientLevelEvents {
    public static final EventInvoker<Load> LOAD = EventInvoker.lookup(Load.class);
    public static final EventInvoker<Unload> UNLOAD = EventInvoker.lookup(Unload.class);

    private ClientLevelEvents() {

    }

    @FunctionalInterface
    public interface Load {

        /**
         * Fires before a client level is loaded.
         *
         * @param minecraft minecraft singleton instance
         * @param level  the client level that is being loaded
         */
        void onLevelLoad(Minecraft minecraft, ClientLevel level);
    }

    @FunctionalInterface
    public interface Unload {

        /**
         * Fires before a client level is unloaded.
         *
         * @param minecraft minecraft singleton instance
         * @param level  the client level that is being unloaded
         */
        void onLevelUnload(Minecraft minecraft, ClientLevel level);
    }
}
