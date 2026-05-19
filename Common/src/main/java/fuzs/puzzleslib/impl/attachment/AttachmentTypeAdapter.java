package fuzs.puzzleslib.impl.attachment;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface AttachmentTypeAdapter<T, V> {

    ResourceLocation id();

    boolean hasData(T holder);

    @Nullable V getData(T holder);

    @Nullable V setData(T holder, V value);

    @Nullable V removeData(T holder);
}
