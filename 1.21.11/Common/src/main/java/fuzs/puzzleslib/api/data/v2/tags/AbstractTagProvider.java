package fuzs.puzzleslib.api.data.v2.tags;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.impl.data.SortingTagBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AbstractTagProvider<T> extends TagsProvider<T> {
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
            .put(BlockSetVariant.SHELF, BlockTags.WOODEN_SHELVES)
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
            .put(BlockSetVariant.SHELF, ItemTags.WOODEN_SHELVES)
            .buildKeepingLast();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<EntityType<?>>> VARIANT_ENTITY_TYPE_TAGS = ImmutableMap.<BlockSetVariant, TagKey<EntityType<?>>>builder()
            .put(BlockSetVariant.BOAT, EntityTypeTags.BOAT)
            .put(BlockSetVariant.CHEST_BOAT, TagFactory.COMMON.registerEntityTypeTag("boats"))
            .build();

    protected final String modId;

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
        this(registryKey, context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registryKey, registries);
        this.modId = modId;
    }

    @Override
    public abstract void addTags(HolderLookup.Provider registries);

    @Override
    protected TagBuilder getOrCreateRawBuilder(TagKey<T> tagKey) {
        // use our own tag builder implementation
        return this.builders.computeIfAbsent(tagKey.location(), (Identifier identifier) -> new SortingTagBuilder());
    }

    public AbstractTagAppender<T> tag(String string) {
        return this.tag(Identifier.parse(string));
    }

    public AbstractTagAppender<T> tag(Identifier identifier) {
        return this.tag(TagKey.create(this.registryKey, identifier));
    }

    public AbstractTagAppender<T> tag(TagKey<T> tagKey) {
        return createTagAppender(this.getOrCreateRawBuilder(tagKey), this.registryKey);
    }

    public final void generateFor(Map<BlockSetVariant, Holder.Reference<T>> variants, Map<BlockSetVariant, TagKey<T>> variantTags) {
        variants.forEach((BlockSetVariant variant, Holder.Reference<T> holder) -> {
            TagKey<T> tagKey = variantTags.get(variant);
            if (tagKey != null) {
                this.tag(tagKey).add(holder);
            }
        });
    }

    @ApiStatus.Internal
    public static <T> AbstractTagAppender<T> createTagAppender(TagBuilder tagBuilder, ResourceKey<? extends Registry<? super T>> registryKey) {
        Optional<Registry<T>> optional = LookupHelper.getRegistry(registryKey);
        Function<T, ResourceKey<T>> keyExtractor = optional.isPresent() ?
                (T t) -> optional.flatMap((Registry<T> registry) -> registry.getResourceKey(t)).orElseThrow(() -> {
                    return new IllegalStateException("Missing value in " + registryKey + ": " + t);
                }) : null;
        return ProxyImpl.get().getTagAppender(tagBuilder, keyExtractor);
    }
}
