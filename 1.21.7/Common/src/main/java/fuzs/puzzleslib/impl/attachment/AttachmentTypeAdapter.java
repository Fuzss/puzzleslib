package fuzs.puzzleslib.impl.attachment;

import net.minecraft.resources.ResourceLocation;

public interface AttachmentTypeAdapter<T, V> {

    ResourceLocation resourceLocation();

    boolean hasData(T holder);

    V getData(T holder);

    V setData(T holder, V value);

    V removeData(T holder);
}
