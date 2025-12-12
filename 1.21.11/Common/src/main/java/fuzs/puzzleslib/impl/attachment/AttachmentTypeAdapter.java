package fuzs.puzzleslib.impl.attachment;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public interface AttachmentTypeAdapter<T, V> {

    Identifier identifier();

    boolean hasData(T holder);

    @Nullable V getData(T holder);

    void setData(T holder, V value);

    void removeData(T holder);
}
