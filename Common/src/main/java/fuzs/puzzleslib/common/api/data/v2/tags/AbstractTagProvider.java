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
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractTagProvider<T> extends TagsProvider<T> {
    private static final TagBuilder STATIC_TAG_BUILDER = TagBuilder.create();
    /**
     * A custom {@link Codec} for {@link TagFile} which adds both NeoForge and Fabric remove fields.
     * <p>
     * The respective codecs for those fields are directly copied from the corresponding loader.
     */
    @ApiStatus.Internal
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
            .put(BlockSetVariant.BUTTON, BlockItemTags.BUTTONS.block())
            .put(BlockSetVariant.DOOR, BlockItemTags.DOORS.block())
            .put(BlockSetVariant.FENCE, BlockItemTags.FENCES.block())
            .put(BlockSetVariant.FENCE_GATE, BlockItemTags.FENCE_GATES.block())
            .put(BlockSetVariant.SIGN, BlockTags.STANDING_SIGNS)
            .put(BlockSetVariant.SLAB, BlockItemTags.SLABS.block())
            .put(BlockSetVariant.STAIRS, BlockItemTags.STAIRS.block())
            .put(BlockSetVariant.PRESSURE_PLATE, BlockTags.PRESSURE_PLATES)
            .put(BlockSetVariant.TRAPDOOR, BlockItemTags.TRAPDOORS.block())
            .put(BlockSetVariant.WALL, BlockItemTags.WALLS.block())
            .put(BlockSetVariant.WALL_SIGN, BlockTags.WALL_SIGNS)
            .put(BlockSetVariant.HANGING_SIGN, BlockTags.CEILING_HANGING_SIGNS)
            .put(BlockSetVariant.WALL_HANGING_SIGN, BlockTags.WALL_HANGING_SIGNS)
            .build();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Block>> VARIANT_STONE_BLOCK_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Block>>builder()
            .putAll(VARIANT_BLOCK_TAGS)
            .put(BlockSetVariant.BUTTON, BlockItemTags.STONE_BUTTONS.block())
            .put(BlockSetVariant.PRESSURE_PLATE, BlockTags.STONE_PRESSURE_PLATES)
            .buildKeepingLast();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Block>> VARIANT_WOODEN_BLOCK_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Block>>builder()
            .putAll(VARIANT_BLOCK_TAGS)
            .put(BlockSetVariant.LOG, TagFactory.COMMON.registerBlockTag("natural_logs"))
            .put(BlockSetVariant.WOOD, TagFactory.COMMON.registerBlockTag("natural_woods"))
            .put(BlockSetVariant.STRIPPED_LOG, TagFactory.COMMON.registerBlockTag("stripped_logs"))
            .put(BlockSetVariant.STRIPPED_WOOD, TagFactory.COMMON.registerBlockTag("stripped_woods"))
            .put(BlockSetVariant.BUTTON, BlockItemTags.WOODEN_BUTTONS.block())
            .put(BlockSetVariant.DOOR, BlockItemTags.WOODEN_DOORS.block())
            .put(BlockSetVariant.FENCE, BlockItemTags.WOODEN_FENCES.block())
            .put(BlockSetVariant.SLAB, BlockItemTags.WOODEN_SLABS.block())
            .put(BlockSetVariant.STAIRS, BlockItemTags.WOODEN_STAIRS.block())
            .put(BlockSetVariant.PRESSURE_PLATE, BlockItemTags.WOODEN_PRESSURE_PLATES.block())
            .put(BlockSetVariant.TRAPDOOR, BlockItemTags.WOODEN_TRAPDOORS.block())
            .put(BlockSetVariant.SHELF, BlockItemTags.WOODEN_SHELVES.block())
            .buildKeepingLast();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Item>> VARIANT_ITEM_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Item>>builder()
            .put(BlockSetVariant.BUTTON, BlockItemTags.BUTTONS.item())
            .put(BlockSetVariant.DOOR, BlockItemTags.DOORS.item())
            .put(BlockSetVariant.FENCE, BlockItemTags.FENCES.item())
            .put(BlockSetVariant.FENCE_GATE, BlockItemTags.FENCE_GATES.item())
            .put(BlockSetVariant.SLAB, BlockItemTags.SLABS.item())
            .put(BlockSetVariant.STAIRS, BlockItemTags.STAIRS.item())
            .put(BlockSetVariant.TRAPDOOR, BlockItemTags.TRAPDOORS.item())
            .put(BlockSetVariant.WALL, BlockItemTags.WALLS.item())
            .put(BlockSetVariant.SIGN, BlockItemTags.SIGNS.item())
            .put(BlockSetVariant.HANGING_SIGN, BlockItemTags.HANGING_SIGNS.item())
            .put(BlockSetVariant.BOAT, ItemTags.BOATS)
            .put(BlockSetVariant.CHEST_BOAT, ItemTags.CHEST_BOATS)
            .build();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Item>> VARIANT_STONE_ITEM_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Item>>builder()
            .putAll(VARIANT_ITEM_TAGS)
            .put(BlockSetVariant.BUTTON, BlockItemTags.STONE_BUTTONS.item())
            .buildKeepingLast();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<Item>> VARIANT_WOODEN_ITEM_TAGS = ImmutableMap.<BlockSetVariant, TagKey<Item>>builder()
            .putAll(VARIANT_ITEM_TAGS)
            .put(BlockSetVariant.LOG, TagFactory.COMMON.registerItemTag("natural_logs"))
            .put(BlockSetVariant.WOOD, TagFactory.COMMON.registerItemTag("natural_woods"))
            .put(BlockSetVariant.STRIPPED_LOG, TagFactory.COMMON.registerItemTag("stripped_logs"))
            .put(BlockSetVariant.STRIPPED_WOOD, TagFactory.COMMON.registerItemTag("stripped_woods"))
            .put(BlockSetVariant.BUTTON, BlockItemTags.WOODEN_BUTTONS.item())
            .put(BlockSetVariant.DOOR, BlockItemTags.WOODEN_DOORS.item())
            .put(BlockSetVariant.FENCE, BlockItemTags.WOODEN_FENCES.item())
            .put(BlockSetVariant.SLAB, BlockItemTags.WOODEN_SLABS.item())
            .put(BlockSetVariant.STAIRS, BlockItemTags.WOODEN_STAIRS.item())
            .put(BlockSetVariant.PRESSURE_PLATE, BlockItemTags.WOODEN_PRESSURE_PLATES.item())
            .put(BlockSetVariant.TRAPDOOR, BlockItemTags.WOODEN_TRAPDOORS.item())
            .put(BlockSetVariant.SHELF, BlockItemTags.WOODEN_SHELVES.item())
            .buildKeepingLast();
    /**
     * @see #generateFor(Map, Map)
     */
    public static final Map<BlockSetVariant, TagKey<EntityType<?>>> VARIANT_ENTITY_TYPE_TAGS = ImmutableMap.<BlockSetVariant, TagKey<EntityType<?>>>builder()
            .put(BlockSetVariant.BOAT, EntityTypeTags.BOAT)
            .put(BlockSetVariant.CHEST_BOAT, TagFactory.COMMON.registerEntityTypeTag("boats"))
            .build();

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
        this(registryKey, context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractTagProvider(ResourceKey<? extends Registry<T>> registryKey, String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
        super(packOutput, registryKey, registries, CompletableFuture.completedFuture((TagKey<T> tagKey) -> {
            return Objects.equals(tagKey.location().getNamespace(), modId) ? Optional.empty() :
                    Optional.of(STATIC_TAG_BUILDER);
        }));
    }

    @Override
    public abstract void addTags(HolderLookup.Provider registries);

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

    @Override
    public AbstractTagAppender<T> tag(TagKey<T> tag) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        return KeyedValueProvider.tags(builder);
    }

    @Override
    public AbstractTagAppender<T> tag(TagKey<T> tag, boolean replace) {
        TagBuilder builder = this.getOrCreateRawBuilder(tag);
        builder.setReplace(replace);
        return KeyedValueProvider.tags(builder);
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
