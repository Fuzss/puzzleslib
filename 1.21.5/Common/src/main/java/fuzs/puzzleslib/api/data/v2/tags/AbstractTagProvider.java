package fuzs.puzzleslib.api.data.v2.tags;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractTagProvider<T> extends TagsProvider<T> {
    protected final String modId;

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
        this(registryKey, context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registryKey, registries);
        this.modId = modId;
    }

    @ApiStatus.Internal
    public static <T> AbstractTagAppender<T> tagAppender(TagBuilder tagBuilder, ResourceKey<? extends Registry<? super T>> registryKey) {
        Optional<Registry<T>> optional = LookupHelper.getRegistry(registryKey);
        Function<T, ResourceKey<T>> keyExtractor = optional.isPresent() ?
                (T t) -> optional.flatMap((Registry<T> registry) -> registry.getResourceKey(t)).orElseThrow(() -> {
                    return new IllegalStateException("Missing value in " + registryKey + ": " + t);
                }) : null;
        return ProxyImpl.get().getTagAppender(tagBuilder, keyExtractor);
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
        return tagAppender(this.getOrCreateRawBuilder(tagKey), this.registryKey);
    }
}
