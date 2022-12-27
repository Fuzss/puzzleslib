package fuzs.puzzleslib.network.v2.serialization;

import net.minecraft.network.FriendlyByteBuf;

public interface MessageSerializer<T> {

    void write(final FriendlyByteBuf buf, T instance);

    T read(final FriendlyByteBuf buf);
}
