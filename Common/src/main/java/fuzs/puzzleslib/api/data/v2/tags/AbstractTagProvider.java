package fuzs.puzzleslib.api.data.v2.tags;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractTagProvider<T> extends TagsProvider<T> {
    protected final String modId;

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
        this(registryKey, context.getModId(), context.getPackOutput(), context.getLookupProvider());
    }

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registryKey, registries);
        this.modId = modId;
    }

    static <T> AbstractTagAppender<T> tagAppender(TagBuilder tagBuilder, ResourceKey<? extends Registry<? super T>> registryKey) {
        Optional<Registry<T>> optional = getRegistry(registryKey);
        Function<T, ResourceKey<T>> keyExtractor = optional.isPresent() ?
                (T t) -> optional.flatMap((Registry<T> registry) -> registry.getResourceKey(t)).orElseThrow(() -> {
                    return new IllegalStateException("Missing value in " + registryKey + ": " + t);
                }) : null;
        return CommonFactories.INSTANCE.getTagAppender(tagBuilder, keyExtractor);
    }

    @SuppressWarnings("unchecked")
    static <T> Optional<Registry<T>> getRegistry(ResourceKey<? extends Registry<? super T>> registryKey) {
        Objects.requireNonNull(registryKey, "registry key is null");
        return ((Registry<Registry<T>>) BuiltInRegistries.REGISTRY).getOptional((ResourceKey<Registry<T>>) registryKey);
    }

    @Override
    public abstract void addTags(HolderLookup.Provider registries);

    @ApiStatus.Internal
    @Override
    public TagAppender<T> tag(TagKey<T> tagKey) {
        // we do not extend TagAppender to work around some type parameter restrictions,
        // so use differently named methods with our custom TagAppender implementation
        throw new UnsupportedOperationException();
    }

    public AbstractTagAppender<T> add(String string) {
        return this.add(ResourceLocationHelper.parse(string));
    }

    public AbstractTagAppender<T> add(ResourceLocation resourceLocation) {
        return this.add(TagKey.create(this.registryKey, resourceLocation));
    }

    public AbstractTagAppender<T> add(TagKey<T> tagKey) {
        return tagAppender(this.getOrCreateRawBuilder(tagKey), this.registryKey);
    }
}
