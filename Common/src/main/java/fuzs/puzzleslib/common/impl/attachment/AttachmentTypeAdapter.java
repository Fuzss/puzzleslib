package fuzs.puzzleslib.common.impl.attachment;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public interface AttachmentTypeAdapter<T, V> {

    Identifier id();

    boolean hasData(T holder);

    @Nullable V getData(T holder);

    @Nullable V setData(T holder, V value);

    @Nullable V removeData(T holder);
}
