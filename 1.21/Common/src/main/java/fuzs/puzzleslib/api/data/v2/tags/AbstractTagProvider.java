package fuzs.puzzleslib.api.data.v2.tags;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelperV2;
import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractTagProvider<T> extends TagsProvider<T> {
    private final String modId;
    @Nullable
    private final Registry<T> registry;
    @Nullable
    private final Function<T, ResourceKey<T>> keyExtractor;

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
        this(registryKey, context.getModId(), context.getPackOutput(), context.getLookupProvider());
    }

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, registryKey, lookupProvider);
        this.modId = modId;
        this.registry = RegistryHelperV2.findNullableBuiltInRegistry(registryKey);
        this.keyExtractor = this.registry != null ? (T t) -> RegistryHelperV2.getResourceKeyOrThrow(this.registry, t) : null;
    }

    @Override
    public abstract void addTags(HolderLookup.Provider registries);

    @ApiStatus.Internal
    @Override
    public TagAppender<T> tag(TagKey<T> tagKey) {
        // we cannot extend TagAppender as the only constructor is replaced on Forge & NeoForge
        // so use differently named methods with our custom TagAppender implementation
        throw new UnsupportedOperationException();
    }

    public AbstractTagAppender<T> add(String string) {
        return this.add(new ResourceLocation(string));
    }

    public AbstractTagAppender<T> add(ResourceLocation resourceLocation) {
        return this.add(TagKey.create(this.registryKey, resourceLocation));
    }

    public AbstractTagAppender<T> add(TagKey<T> tagKey) {
        return CommonFactories.INSTANCE.getTagAppender(this.getOrCreateRawBuilder(tagKey),
                this.modId,
                this.keyExtractor
        );
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
