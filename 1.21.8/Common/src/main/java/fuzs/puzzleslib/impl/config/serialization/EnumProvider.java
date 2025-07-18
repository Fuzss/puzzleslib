package fuzs.puzzleslib.impl.config.serialization;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public final class EnumProvider<T extends Enum<T>> implements KeyedValueProvider<T> {
    private final Class<T> clazz;
    private final BiMap<ResourceLocation, T> values;

    public EnumProvider(Class<T> enumClazz, String modId) {
        this.clazz = enumClazz;
        this.values = Stream.of(enumClazz.getEnumConstants()).collect(ImmutableBiMap.toImmutableBiMap((T t) -> {
            return ResourceLocationHelper.fromNamespaceAndPath(modId, Util.sanitizeName(t.name(), ResourceLocation::validPathChar));
        }, Function.identity()));
    }

    @Override
    public Optional<T> getValue(ResourceLocation name) {
        return Optional.ofNullable(this.values.get(name));
    }

    @Override
    public ResourceLocation getKey(T value) {
        ResourceLocation resourceLocation = this.values.inverse().get(value);
        Objects.requireNonNull(resourceLocation, "resource location is null");
        return resourceLocation;
    }

    @Override
    public Stream<Map.Entry<ResourceLocation, T>> stream() {
        return this.values.entrySet().stream();
    }

    @Override
    public Stream<T> streamValues() {
        return this.values.values().stream();
    }

    @Override
    public String name() {
        return this.clazz.getSimpleName();
    }
}
