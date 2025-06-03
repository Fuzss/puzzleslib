package fuzs.puzzleslib.impl.network;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * This implementation is heavily inspired by and largely based upon <a href="https://github.com/wisp-forest/owo-lib">Owo Lib</a> by <a href="https://github.com/gliscowo">Glisco</a>.
 */
@Deprecated
public final class RecordStreamCodec<R extends Record> implements StreamCodec<FriendlyByteBuf, R> {
    private final Class<R> recordType;
    private final List<RecordAccess<FriendlyByteBuf, ?, R>> recordAccess;
    private final Function<Object[], R> instanceFactory;

    private RecordStreamCodec(Class<R> recordType, List<RecordAccess<FriendlyByteBuf, ?, R>> recordAccess, Function<Object[], R> instanceFactory) {
        this.recordType = recordType;
        this.instanceFactory = instanceFactory;
        this.recordAccess = recordAccess;
    }

    public static <R extends Record> StreamCodec<FriendlyByteBuf, R> createRecordSerializer(Class<R> clazz) {
        if (!clazz.isRecord()) {
            throw new IllegalArgumentException("Message of type %s is not a record".formatted(clazz));
        }
        ImmutableList.Builder<RecordAccess<FriendlyByteBuf, ?, R>> builder = ImmutableList.builder();
        for (RecordComponent component : clazz.getRecordComponents()) {
            builder.add(RecordAccess.fromRecordComponent(component));
        }
        List<RecordAccess<FriendlyByteBuf, ?, R>> recordAccess = builder.build();
        Class<?>[] constructorArguments = recordAccess.stream().map(RecordAccess::type).toArray(Class[]::new);
        try {
            Constructor<R> constructor = clazz.getConstructor(constructorArguments);
            return new RecordStreamCodec<>(clazz, recordAccess, args -> {
                try {
                    return constructor.newInstance(args);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Unable to create new record instance of type %s".formatted(clazz), e);
                }
            });
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find constructor with arguments %s for record type %s".formatted(Arrays.toString(constructorArguments), clazz), e);
        }
    }

    public Class<R> getRecordType() {
        return this.recordType;
    }

    @Override
    public void encode(FriendlyByteBuf buf, R instance) {
        for (RecordAccess<FriendlyByteBuf, ?, R> access : this.recordAccess) {
            access.encode(buf, instance);
        }
    }

    @Override
    public R decode(FriendlyByteBuf buf) {
        Object[] values = this.recordAccess.stream().map(recordAccess -> recordAccess.decode(buf)).toArray();
        return this.instanceFactory.apply(values);
    }

    private record RecordAccess<B extends ByteBuf, V, R extends Record>(Class<? extends V> type, Function<R, V> fieldAccess,
                                                                        StreamCodec<B, V> streamCodec) implements StreamEncoder<B, R>, StreamDecoder<B, V> {

        @SuppressWarnings("unchecked")
        static <B extends ByteBuf, V, R extends Record> RecordAccess<B, V, R> fromRecordComponent(RecordComponent component) {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            Class<V> type = (Class<V>) component.getType();
            Function<R, V> fieldAccess = instance -> {
                try {
                    return (V) lookup.unreflect(component.getAccessor()).invoke(instance);
                } catch (Throwable e) {
                    throw new RuntimeException("Unable to get record value of type %s from record component from record type %s".formatted(type, component.getDeclaringRecord()), e);
                }
            };
            StreamCodec<B, V> streamCodec = StreamCodecRegistryImpl.fromGenericType(component.getGenericType());
            return new RecordAccess<>(type, fieldAccess, streamCodec);
        }

        @Override
        public void encode(B buf, R instance) {
            this.streamCodec.encode(buf, this.fieldAccess.apply(instance));
        }

        @Override
        public V decode(B buf) {
            return this.streamCodec.decode(buf);
        }
    }
}
