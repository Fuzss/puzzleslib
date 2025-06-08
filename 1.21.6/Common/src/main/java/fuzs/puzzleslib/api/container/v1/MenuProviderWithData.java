package fuzs.puzzleslib.api.container.v1;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

/**
 * A custom {@link MenuProvider} for menu types registered via
 * {@link fuzs.puzzleslib.api.init.v3.registry.MenuSupplierWithData}.
 *
 * @param <T> the menu data type
 */
public interface MenuProviderWithData<T> extends MenuProvider {

    /**
     * @param serverPlayer the player opening the menu, potentially {@code null} when unable to retrieve from inventory
     *                     slots
     * @return the menu data
     */
    T getMenuData(ServerPlayer serverPlayer);
}
