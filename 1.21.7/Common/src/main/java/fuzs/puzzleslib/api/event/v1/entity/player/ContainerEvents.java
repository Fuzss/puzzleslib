package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class ContainerEvents {
    public static final EventInvoker<Open> OPEN = EventInvoker.lookup(Open.class);
    public static final EventInvoker<Close> CLOSE = EventInvoker.lookup(Close.class);

    private ContainerEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Open {

        /**
         * Called after the player has opened a container, which is when the new {@link AbstractContainerMenu} has been
         * set to {@link Player#containerMenu} in {@link Player#openMenu(MenuProvider)}.
         *
         * @param serverPlayer  the server player
         * @param containerMenu the container menu
         */
        void onContainerOpen(ServerPlayer serverPlayer, AbstractContainerMenu containerMenu);
    }

    @FunctionalInterface
    public interface Close {

        /**
         * Called when the player is closing an open container, where {@link Player#containerMenu} is reset to
         * {@link Player#inventoryMenu} in {@link Player#doCloseContainer()}.
         *
         * @param serverPlayer  the server player
         * @param containerMenu the container menu
         */
        void onContainerClose(ServerPlayer serverPlayer, AbstractContainerMenu containerMenu);
    }
}
