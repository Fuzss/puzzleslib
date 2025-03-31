package fuzs.puzzleslib.api.client.init.v1;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Objects;

/**
 * A helper class containing wood type related methods for the client.
 */
public final class ClientWoodTypeRegistry {

    private ClientWoodTypeRegistry() {
        // NO-OP
    }

    /**
     * Registers a {@link WoodType} to the {@link net.minecraft.client.renderer.Sheets} class for creating sign
     * materials.
     * <p>
     * Note that underlying maps are not concurrent, so this must be called from synchronized code.
     *
     * @param woodType the wood type
     */
    public static void registerWoodType(WoodType woodType) {
        Objects.requireNonNull(woodType, "wood type is null");
        ClientProxyImpl.get().registerWoodType(woodType);
    }
}
