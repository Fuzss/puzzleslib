package fuzs.puzzleslib.impl.attachment;

import net.minecraft.resources.ResourceLocation;

public interface AttachmentTypeAdapter<T, A> {

    ResourceLocation resourceLocation();

    boolean hasData(T holder);

    A getData(T holder);

    A setData(T holder, A value);

    A removeData(T holder);
}
