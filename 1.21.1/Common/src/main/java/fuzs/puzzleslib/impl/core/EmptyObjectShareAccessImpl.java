package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.ObjectShareAccess;
import org.jetbrains.annotations.Nullable;

public final class EmptyObjectShareAccessImpl implements ObjectShareAccess {
    public static final ObjectShareAccess INSTANCE = new EmptyObjectShareAccessImpl();

    private EmptyObjectShareAccessImpl() {
        // NO-OP
    }

    @Override
    public @Nullable Object get(String key) {
        return null;
    }

    @Override
    public @Nullable Object put(String key, Object value) {
        return null;
    }

    @Override
    public @Nullable Object putIfAbsent(String key, Object value) {
        return null;
    }

    @Override
    public @Nullable Object remove(String key) {
        return null;
    }
}
