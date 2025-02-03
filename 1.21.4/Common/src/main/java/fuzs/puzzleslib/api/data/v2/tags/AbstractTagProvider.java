package fuzs.puzzleslib.api.data.v2.tags;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelper;
import fuzs.puzzleslib.impl.core.CommonFactories;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractTagProvider<T> extends TagsProvider<T> {
    protected final String modId;
    @Nullable
    private final Registry<T> registry;
    @Nullable
    private final Function<T, ResourceKey<T>> keyExtractor;

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
        this(registryKey, context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registryKey, registries);
        this.modId = modId;
        this.registry = RegistryHelper.findNullableBuiltInRegistry(registryKey);
        this.keyExtractor =
                this.registry != null ? (T t) -> RegistryHelper.getResourceKeyOrThrow(this.registry, t) : null;
    }

    @Override
    public abstract void addTags(HolderLookup.Provider registries);

    public AbstractTagAppender<T> tag(String string) {
        return this.tag(ResourceLocation.parse(string));
    }

    public AbstractTagAppender<T> tag(ResourceLocation resourceLocation) {
        return this.tag(TagKey.create(this.registryKey, resourceLocation));
    }

    @Override
    public AbstractTagAppender<T> tag(TagKey<T> tagKey) {
        TagBuilder tagBuilder = this.getOrCreateRawBuilder(tagKey);
        return CommonFactories.INSTANCE.getTagAppender(tagBuilder, this.keyExtractor);
    }

    protected Registry<T> registry() {
        Objects.requireNonNull(this.registry, "registry is null");
        return this.registry;
    }

    protected Function<T, ResourceKey<T>> keyExtractor() {
        Objects.requireNonNull(this.keyExtractor, "key extractor is null");
        return this.keyExtractor;
    }
}
