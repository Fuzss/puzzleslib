package fuzs.puzzleslib.api.core.v1;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.BiConsumer;

/**
 * useful methods for gameplay related things that require mod loader specific abstractions
 */
public interface CommonAbstractions {
    /**
     * instance of the common abstractions SPI
     */
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

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

    /**
     * Is <code>entityType</code> considered a boss mob like {@link EntityType#ENDER_DRAGON} and {@link EntityType#WITHER} in vanilla.
     * <p>A common property of such entities usually is {@link LivingEntity#canChangeDimensions()} returns <code>false</code>.
     *
     * @param type the entity type
     * @return is it a boss mob
     */
    boolean isBossMob(EntityType<?> type);
}
