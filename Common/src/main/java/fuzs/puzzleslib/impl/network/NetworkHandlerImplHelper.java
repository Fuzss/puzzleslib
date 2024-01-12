package fuzs.puzzleslib.impl.network;

import fuzs.puzzleslib.api.core.v1.ReflectionHelper;
import fuzs.puzzleslib.api.network.v2.MessageV2;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.reflect.Constructor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class NetworkHandlerImplHelper {

    private NetworkHandlerImplHelper() {

    }

    @SuppressWarnings("unchecked")
    public static <T extends MessageV2<T>> Function<FriendlyByteBuf, T> getMessageDecoder(Class<T> clazz) {
        Supplier<T> supplier = () -> ReflectionHelper.newDefaultInstanceFactory(clazz).get().orElseThrow();
        return Stream.of(clazz.getConstructors())
                .filter(currentConstructor -> currentConstructor.getParameterCount() == 1)
                .filter(currentConstructor -> currentConstructor.getParameterTypes()[0] == FriendlyByteBuf.class)
                .findAny()
                .<Function<FriendlyByteBuf, T>>map(constructor -> buf -> ReflectionHelper.newInstance((Constructor<T>) constructor, buf).orElseThrow())
                .orElseGet(() -> getDirectMessageDecoder(supplier));
    }

    public static <T extends MessageV2<T>> Function<FriendlyByteBuf, T> getDirectMessageDecoder(Supplier<T> supplier) {
        return buf -> Util.make(supplier.get(), message -> message.read(buf));
    }
}
