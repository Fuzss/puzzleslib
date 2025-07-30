package fuzs.puzzleslib.api.client.packs.v1;

import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A helper for registering resource packs bundled within the {@code resources/resourcepacks} directory of a mod jar.
 */
@Deprecated(forRemoval = true)
public final class BuiltinResourcePackHelper {

    private BuiltinResourcePackHelper() {
        // NO-OP
    }

    /**
     * Registers a built-in resource pack bundled in the mod jar.
     *
     * @param resourceLocation the name of the pack in {@code resources/resourcepacks}
     */
    public static void registerBuiltinResourcePack(ResourceLocation resourceLocation) {
        registerBuiltinResourcePack(resourceLocation, false);
    }

    /**
     * Registers a built-in resource pack bundled in the mod jar.
     *
     * @param resourceLocation the name of the pack in {@code resources/resourcepacks}
     * @param required         is this pack always enabled and cannot be turned off
     */
    public static void registerBuiltinResourcePack(ResourceLocation resourceLocation, boolean required) {
        registerBuiltinResourcePack(resourceLocation, Component.literal(resourceLocation.toString()), required);
    }

    /**
     * Registers a built-in resource pack bundled in the mod jar.
     *
     * @param resourceLocation the name of the pack in {@code resources/resourcepacks}
     * @param displayName      the name component for the created pack
     * @param required         is this pack always enabled and cannot be turned off
     */
    public static void registerBuiltinResourcePack(ResourceLocation resourceLocation, Component displayName, boolean required) {
        ClientProxyImpl.get().registerBuiltinResourcePack(resourceLocation, displayName, required);
    }
}
