package fuzs.puzzleslib.fabric.impl.client.util;

import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import org.jetbrains.annotations.Nullable;

public interface EntityRenderStateExtension {

    @Nullable
    <T> T puzzleslib$getRenderProperty(RenderPropertyKey<T> key);

    <T> void puzzleslib$setRenderProperty(RenderPropertyKey<T> key, @Nullable T t);

    void puzzleslib$clearRenderProperties();
}
