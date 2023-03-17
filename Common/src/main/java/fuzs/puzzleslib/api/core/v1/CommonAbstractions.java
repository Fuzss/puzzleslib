package fuzs.puzzleslib.api.core.v1;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

import java.util.function.BiConsumer;

/**
 * useful methods for gameplay related things that require mod loader specific abstractions
 */
public interface CommonAbstractions {
    /**
     * instance of the common abstractions SPI
     */
    CommonAbstractions INSTANCE = ServiceProviderHelper.loadServiceProvider(CommonAbstractions.class);

    /**
     * opens a menu on both client and server
     *
     * @param player       player to open menu for
     * @param menuProvider menu factory
     */
    default void openMenu(ServerPlayer player, MenuProvider menuProvider) {
        this.openMenu(player, menuProvider, (ServerPlayer serverPlayer, FriendlyByteBuf buf) -> {

        });
    }

    /**
     * opens a menu on both client and server while also providing additional data
     *
     * @param player                  player to open menu for
     * @param menuProvider            menu factory
     * @param screenOpeningDataWriter additional data added via {@link FriendlyByteBuf}
     */
    void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, FriendlyByteBuf> screenOpeningDataWriter);
}
