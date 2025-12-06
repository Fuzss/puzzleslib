package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.resources.ResourceLocation;

/**
 * This has been separated from {@link PuzzlesLib} to prevent issues with static initialization when accessing constants
 * in {@link PuzzlesLib} early.
 */
public class PuzzlesLibMod extends PuzzlesLib implements ModConstructor {

    @Override
    public void onConstructMod() {
        ProxyImpl.get().registerEventHandlers();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
