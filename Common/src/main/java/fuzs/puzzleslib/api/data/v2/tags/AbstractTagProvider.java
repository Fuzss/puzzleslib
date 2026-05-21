package fuzs.puzzleslib.api.data.v2.tags;

import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelper;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.impl.data.SortingTagBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractTagProvider<T> extends TagsProvider<T> {
    private static final TagBuilder STATIC_TAG_BUILDER = TagBuilder.create();

    protected final String modId;
    @Nullable
    private final Registry<T> registry;
    @Nullable
    private final Function<T, ResourceKey<T>> keyExtractor;

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
        this(registryKey, context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registryKey, registries, CompletableFuture.completedFuture((TagKey<T> tagKey) -> {
            return Objects.equals(tagKey.location().getNamespace(), modId) ? Optional.empty() :
                    Optional.of(STATIC_TAG_BUILDER);
        }));
        this.modId = modId;
        this.registry = RegistryHelper.findNullableBuiltInRegistry(registryKey);
        this.keyExtractor =
                this.registry != null ? (T t) -> RegistryHelper.getResourceKeyOrThrow(this.registry, t) : null;
    }

    @Override
    public abstract void addTags(HolderLookup.Provider registries);

    @Override
    protected TagBuilder getOrCreateRawBuilder(TagKey<T> tag) {
        return this.builders.computeIfAbsent(tag.location(), (ResourceLocation id) -> new SortingTagBuilder());
    }

    public fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> tag(String id) {
        return this.tag(ResourceLocation.parse(id));
    }

    public fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> tag(String id, boolean replace) {
        return this.tag(ResourceLocation.parse(id), replace);
    }

    public fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> tag(ResourceLocation id) {
        return this.tag(TagKey.create(this.registryKey, id));
    }

    public fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> tag(ResourceLocation id, boolean replace) {
        return this.tag(TagKey.create(this.registryKey, id), replace);
    }

    /**
     * @see net.minecraft.data.tags.KeyTagProvider#tag(TagKey)
     */
    public fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> tag(TagKey<T> tag) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        return KeyedValueProvider.tags(builder, this.registryKey);
    }

    /**
     * @see net.minecraft.data.tags.KeyTagProvider#tag(TagKey, boolean)
     */
    public fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> tag(TagKey<T> tag, boolean replace) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        ProxyImpl.get().setTagBuilderReplace(builder, replace);
        return KeyedValueProvider.tags(builder, this.registryKey);
    }

    @Deprecated
    public AbstractTagAppender<T> add(String string) {
        return this.add(ResourceLocation.parse(string));
    }

    @Deprecated
    public AbstractTagAppender<T> add(ResourceLocation resourceLocation) {
        return this.add(TagKey.create(this.registryKey, resourceLocation));
    }

    @Deprecated
    public AbstractTagAppender<T> add(TagKey<T> tagKey) {
        return ProxyImpl.get().getTagAppenderV2(this.getOrCreateRawBuilder(tagKey), this.keyExtractor);
    }

    @Deprecated
    protected Registry<T> registry() {
        Objects.requireNonNull(this.registry, "registry is null");
        return this.registry;
    }

    @Deprecated
    protected Function<T, ResourceKey<T>> keyExtractor() {
        Objects.requireNonNull(this.keyExtractor, "key extractor is null");
        return this.keyExtractor;
    }
}
