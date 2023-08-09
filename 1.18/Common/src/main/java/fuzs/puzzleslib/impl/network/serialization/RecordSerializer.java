package fuzs.puzzleslib.impl.network.serialization;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializer;
import fuzs.puzzleslib.api.network.v3.serialization.MessageSerializers;
import net.minecraft.network.FriendlyByteBuf;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * This implementation is heavily inspired by and largely based upon <a href="https://github.com/wisp-forest/owo-lib">Owo Lib</a> by <a href="https://github.com/gliscowo">Glisco</a>.
 */
public final class RecordSerializer<T extends Record> implements MessageSerializer<T> {
    private final Class<T> recordType;
    private final List<RecordAccess<?, T>> recordAccess;
    private final Function<Object[], T> instanceFactory;

    private RecordSerializer(Class<T> recordType, List<RecordAccess<?, T>> recordAccess, Function<Object[], T> instanceFactory) {
        this.recordType = recordType;
        this.instanceFactory = instanceFactory;
        this.recordAccess = recordAccess;
    }

    public static <T extends Record> MessageSerializer<T> createRecordSerializer(Class<T> clazz) {
        if (!clazz.isRecord()) {
            throw new IllegalArgumentException("Message of type %s is not a record".formatted(clazz));
        }
        ImmutableList.Builder<RecordAccess<?, T>> builder = ImmutableList.builder();
        for (RecordComponent component : clazz.getRecordComponents()) {
            builder.add(RecordAccess.fromRecordComponent(component));
        }
        List<RecordAccess<?, T>> recordAccess = builder.build();
        Class<?>[] constructorArguments = recordAccess.stream().map(RecordAccess::type).toArray(Class[]::new);
        try {
            Constructor<T> constructor = clazz.getConstructor(constructorArguments);
            return new RecordSerializer<>(clazz, recordAccess, args -> {
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

    public Class<T> getRecordType() {
        return this.recordType;
    }

    @Override
    public void write(FriendlyByteBuf buf, T instance) {
        for (RecordAccess<?, T> access : this.recordAccess) {
            access.write(buf, instance);
        }
    }

    @Override
    public T read(FriendlyByteBuf buf) {
        Object[] values = this.recordAccess.stream().map(recordAccess -> recordAccess.read(buf)).toArray();
        return this.instanceFactory.apply(values);
    }

    private record RecordAccess<T, R extends Record>(Class<? extends T> type, Function<R, T> fieldAccess,
                                                     MessageSerializer<T> serializer) {

        @SuppressWarnings("unchecked")
        static <T, R extends Record> RecordAccess<T, R> fromRecordComponent(RecordComponent component) {
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            Class<T> type = (Class<T>) component.getType();
            Function<R, T> fieldAccess = instance -> {
                try {
                    return (T) lookup.unreflect(component.getAccessor()).invoke(instance);
                } catch (Throwable e) {
                    throw new RuntimeException("Unable to get record value of type %s from record component from record type %s".formatted(type, component.getDeclaringRecord()), e);
                }
            };
            MessageSerializer<T> serializer = (MessageSerializer<T>) MessageSerializers.findByGenericType(component.getGenericType());
            return new RecordAccess<>(type, fieldAccess, serializer);
        }

        public void write(FriendlyByteBuf buf, R instance) {
            this.serializer.write(buf, this.fieldAccess.apply(instance));
        }

        public T read(FriendlyByteBuf buf) {
            return this.serializer.read(buf);
        }
    }
}
