package fuzs.puzzleslib.common.api.data.v2.tags;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.common.api.config.v3.serialization.KeyedValueProvider;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.common.api.init.v3.family.BlockSetVariant;
import fuzs.puzzleslib.common.api.init.v3.tags.TagFactory;
import fuzs.puzzleslib.common.api.util.v1.CodecExtras;
import fuzs.puzzleslib.common.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.common.impl.data.SortingTagBuilder;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * TODO purge all the mod id fields from providers where not required
 */
public abstract class AbstractTagProvider<T> extends TagsProvider<T> {
    /**
     * A custom {@link Codec} for {@link TagFile} which adds both NeoForge and Fabric remove fields.
     * <p>
     * The respective codecs for those fields are directly copied from the corresponding loader.
     */
    public static final Codec<TagFile> TAG_FILE_CODEC = CodecExtras.encodeOnly(RecordCodecBuilder.create((RecordCodecBuilder.Instance<TagFile> instance) -> instance.group(
                    TagEntry.CODEC.listOf().fieldOf("values").forGetter(TagFile::entries),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(TagFile::replace),
                    TagEntry.CODEC.listOf().optionalFieldOf("remove", List.of()).forGetter(ProxyImpl.get()::getTagFileRemovals),
                    TagEntry.CODEC.listOf()
                            .lenientOptionalFieldOf("fabric:remove", Collections.emptyList())
                            .forGetter(ProxyImpl.get()::getTagFileRemovals))
            .apply(instance,
                    (List<TagEntry> entries, Boolean replace, List<TagEntry> _, List<TagEntry> _) -> new TagFile(entries,
                            replace))));
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
        super(packOutput, registryKey, registries, CompletableFuture.completedFuture((TagKey<T> tagKey) -> {
            return Objects.equals(tagKey.location().getNamespace(), modId) ? Optional.empty() :
                    Optional.of(TagBuilder.create());
        }));
        this.modId = modId;
    }

    @Override
    public abstract void addTags(HolderLookup.Provider context);

    @Override
    protected TagBuilder getOrCreateRawBuilder(TagKey<T> tag) {
        return this.builders.computeIfAbsent(tag.location(), (Identifier id) -> new SortingTagBuilder());
    }

    public AbstractTagAppender<T> tag(String id) {
        return this.tag(Identifier.parse(id));
    }

    public AbstractTagAppender<T> tag(String id, boolean replace) {
        return this.tag(Identifier.parse(id), replace);
    }

    public AbstractTagAppender<T> tag(Identifier id) {
        return this.tag(TagKey.create(this.registryKey, id));
    }

    public AbstractTagAppender<T> tag(Identifier id, boolean replace) {
        return this.tag(TagKey.create(this.registryKey, id), replace);
    }

    /**
     * @see net.minecraft.data.tags.KeyTagProvider#tag(TagKey)
     */
    public AbstractTagAppender<T> tag(TagKey<T> tag) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        return KeyedValueProvider.tags(builder, this.registryKey);
    }

    /**
     * @see net.minecraft.data.tags.KeyTagProvider#tag(TagKey, boolean)
     */
    public AbstractTagAppender<T> tag(TagKey<T> tag, boolean replace) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        builder.setReplace(replace);
        return KeyedValueProvider.tags(builder, this.registryKey);
    }

    public final void generateFor(Map<BlockSetVariant, Holder.Reference<T>> variants, Map<BlockSetVariant, TagKey<T>> variantTags) {
        variants.forEach((BlockSetVariant variant, Holder.Reference<T> holder) -> {
            TagKey<T> tag = variantTags.get(variant);
            if (tag != null) {
                this.tag(tag).add(holder);
            }
        });
    }
}
