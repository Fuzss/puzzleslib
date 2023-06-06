package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.Minecraft;

public final class ClientTickEvents {
    public static final EventInvoker<Start> START = EventInvoker.lookup(Start.class);
    public static final EventInvoker<End> END = EventInvoker.lookup(End.class);

    private ClientTickEvents() {

    }

    @FunctionalInterface
    public interface Start {

        /**
         * Fires at the beginning of {@link Minecraft#tick()}.
         * <p>TODO rename to onStartClientTick
         *
         * @param minecraft minecraft singleton instance
         */
        void onStartTick(Minecraft minecraft);
    }

    @FunctionalInterface
    public interface End {

        /**
         * Fires at the end of {@link Minecraft#tick()}.
         * <p>TODO rename to onEndClientTick
         *
         * @param minecraft minecraft singleton instance
         */
        void onEndTick(Minecraft minecraft);
    }
}
