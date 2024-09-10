package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.core.v1.ObjectShareAccess;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

public final class FabricObjectShareAccess implements ObjectShareAccess {
    public static final ObjectShareAccess INSTANCE = new FabricObjectShareAccess();

    private FabricObjectShareAccess() {
        // NO-OP
    }

    @Override
    public @Nullable Object get(String key) {
        return FabricLoader.getInstance().getObjectShare().get(key);
    }

    @Override
    public @Nullable Object put(String key, Object value) {
        return FabricLoader.getInstance().getObjectShare().put(key, value);
    }

    @Override
    public @Nullable Object putIfAbsent(String key, Object value) {
        return FabricLoader.getInstance().getObjectShare().putIfAbsent(key, value);
    }

    @Override
    public @Nullable Object remove(String key) {
        return FabricLoader.getInstance().getObjectShare().remove(key);
    }
}
