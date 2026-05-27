package fuzs.puzzleslib.api.data.v2.tags;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.config.v3.serialization.KeyedValueProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelper;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.impl.data.SortingTagBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractTagProvider<T> extends TagsProvider<T> {
    private static final TagBuilder STATIC_TAG_BUILDER = TagBuilder.create();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Block>> VARIANT_BLOCK_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Block>>builder()
            .put(BlockSetVariant.BUTTON, BlockTags.BUTTONS)
            .put(BlockSetVariant.DOOR, BlockTags.DOORS)
            .put(BlockSetVariant.FENCE, BlockTags.FENCES)
            .put(BlockSetVariant.FENCE_GATE, BlockTags.FENCE_GATES)
            .put(BlockSetVariant.SIGN, BlockTags.STANDING_SIGNS)
            .put(BlockSetVariant.SLAB, BlockTags.SLABS)
            .put(BlockSetVariant.STAIRS, BlockTags.STAIRS)
            .put(BlockSetVariant.PRESSURE_PLATE, BlockTags.PRESSURE_PLATES)
            .put(BlockSetVariant.TRAPDOOR, BlockTags.TRAPDOORS)
            .put(BlockSetVariant.WALL, BlockTags.WALLS)
            .put(BlockSetVariant.WALL_SIGN, BlockTags.WALL_SIGNS)
            .put(BlockSetVariant.HANGING_SIGN, BlockTags.CEILING_HANGING_SIGNS)
            .put(BlockSetVariant.WALL_HANGING_SIGN, BlockTags.WALL_HANGING_SIGNS)
            .build();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Block>> VARIANT_STONE_BLOCK_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Block>>builder()
            .putAll(VARIANT_BLOCK_TAGS)
            .put(BlockSetVariant.BUTTON, BlockTags.STONE_BUTTONS)
            .put(BlockSetVariant.PRESSURE_PLATE, BlockTags.STONE_PRESSURE_PLATES)
            .buildKeepingLast();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Block>> VARIANT_WOODEN_BLOCK_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Block>>builder()
            .putAll(VARIANT_BLOCK_TAGS)
            .put(BlockSetVariant.BUTTON, BlockTags.WOODEN_BUTTONS)
            .put(BlockSetVariant.DOOR, BlockTags.WOODEN_DOORS)
            .put(BlockSetVariant.FENCE, BlockTags.WOODEN_FENCES)
            .put(BlockSetVariant.SLAB, BlockTags.WOODEN_SLABS)
            .put(BlockSetVariant.STAIRS, BlockTags.WOODEN_STAIRS)
            .put(BlockSetVariant.PRESSURE_PLATE, BlockTags.WOODEN_PRESSURE_PLATES)
            .put(BlockSetVariant.TRAPDOOR, BlockTags.WOODEN_TRAPDOORS)
            .buildKeepingLast();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Item>> VARIANT_ITEM_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Item>>builder()
            .put(BlockSetVariant.BUTTON, ItemTags.BUTTONS)
            .put(BlockSetVariant.DOOR, ItemTags.DOORS)
            .put(BlockSetVariant.FENCE, ItemTags.FENCES)
            .put(BlockSetVariant.FENCE_GATE, ItemTags.FENCE_GATES)
            .put(BlockSetVariant.SLAB, ItemTags.SLABS)
            .put(BlockSetVariant.STAIRS, ItemTags.STAIRS)
            .put(BlockSetVariant.TRAPDOOR, ItemTags.TRAPDOORS)
            .put(BlockSetVariant.WALL, ItemTags.WALLS)
            .put(BlockSetVariant.SIGN, ItemTags.SIGNS)
            .put(BlockSetVariant.HANGING_SIGN, ItemTags.HANGING_SIGNS)
            .put(BlockSetVariant.BOAT, ItemTags.BOATS)
            .put(BlockSetVariant.CHEST_BOAT, ItemTags.CHEST_BOATS)
            .build();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Item>> VARIANT_STONE_ITEM_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Item>>builder()
            .putAll(VARIANT_ITEM_TAGS)
            .put(BlockSetVariant.BUTTON, ItemTags.STONE_BUTTONS)
            .buildKeepingLast();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Item>> VARIANT_WOODEN_ITEM_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Item>>builder()
            .putAll(VARIANT_ITEM_TAGS)
            .put(BlockSetVariant.BUTTON, ItemTags.WOODEN_BUTTONS)
            .put(BlockSetVariant.DOOR, ItemTags.WOODEN_DOORS)
            .put(BlockSetVariant.FENCE, ItemTags.WOODEN_FENCES)
            .put(BlockSetVariant.SLAB, ItemTags.WOODEN_SLABS)
            .put(BlockSetVariant.STAIRS, ItemTags.WOODEN_STAIRS)
            .put(BlockSetVariant.PRESSURE_PLATE, ItemTags.WOODEN_PRESSURE_PLATES)
            .put(BlockSetVariant.TRAPDOOR, ItemTags.WOODEN_TRAPDOORS)
            .buildKeepingLast();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<EntityType<?>>> VARIANT_ENTITY_TYPE_TAGS = ImmutableMap.<BlockSetVariant, TagKey<EntityType<?>>>builder()
            .put(BlockSetVariant.BOAT, TagFactory.COMMON.registerEntityTypeTag("boats"))
            .put(BlockSetVariant.CHEST_BOAT, TagFactory.COMMON.registerEntityTypeTag("boats"))
            .build();

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
        this.registry = LookupHelper.getRegistry(registryKey).orElse(null);
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

    @Override
    public fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> tag(TagKey<T> tag) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        return KeyedValueProvider.tags(builder, this.registryKey);
    }

    public fuzs.puzzleslib.api.data.v3.tags.AbstractTagAppender<T> tag(TagKey<T> tag, boolean replace) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        ProxyImpl.get().setTagBuilderReplace(builder, replace);
        return KeyedValueProvider.tags(builder, this.registryKey);
    }

    public final void generateFor(Map<BlockSetVariant, Holder.Reference<T>> variants, Map<BlockSetVariant, TagKey<T>> variantTags) {
        variants.forEach((BlockSetVariant variant, Holder.Reference<T> holder) -> {
            TagKey<T> tagKey = variantTags.get(variant);
            if (tagKey != null) {
                this.tag(tagKey).add(holder);
            }
        });
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
