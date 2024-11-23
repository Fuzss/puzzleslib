package fuzs.puzzleslib.impl.client.util;

import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import org.jetbrains.annotations.Nullable;

public interface EntityRenderStateExtension {

    @Nullable
    <T> T mobplaques$getRenderProperty(RenderPropertyKey<T> key);

    <T> void mobplaques$setRenderProperty(RenderPropertyKey<T> key, @Nullable T t);

    void mobplaques$clearRenderProperties();
}
