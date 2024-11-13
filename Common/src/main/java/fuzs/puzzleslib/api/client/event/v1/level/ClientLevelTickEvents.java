package fuzs.puzzleslib.api.client.event.v1.level;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ClientLevelTickEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<End> END = EventInvoker.lookup(End.class);

    private ClientLevelTickEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Start {

        /**
         * Fires before ticking the server level in {@link Minecraft#tick()}.
         *
         * @param minecraft   minecraft singleton instance
         * @param clientLevel the client level that is being ticked
         */
        void onStartLevelTick(Minecraft minecraft, ClientLevel clientLevel);
    }

    @FunctionalInterface
    public interface End {

        /**
         * Fires after ticking the server level in {@link Minecraft#tick()}.
         *
         * @param minecraft   minecraft singleton instance
         * @param clientLevel the client level that is being ticked
         */
        void onEndLevelTick(Minecraft minecraft, ClientLevel clientLevel);
    }
}
