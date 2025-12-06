package fuzs.puzzleslib.impl.attachment;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface AttachmentTypeAdapter<T, V> {

    ResourceLocation resourceLocation();

    boolean hasData(T holder);

    @Nullable V getData(T holder);

    void setData(T holder, V value);

    void removeData(T holder);
}
