package fuzs.puzzleslib.api.client.data.v2;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Stream;

public abstract class AbstractModelProvider implements DataProvider {
    public static final String BLOCK_PATH = "block";
    public static final String ITEM_PATH = "item";
    public static final ModelTemplate SPAWN_EGG = ModelTemplates.createItem("template_spawn_egg");

    private final String modId;
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider modelPathProvider;
    private final Set<Object> skipped = Sets.newHashSet();

    public AbstractModelProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    public AbstractModelProvider(String modId, PackOutput packOutput) {
        this.modId = modId;
        this.blockStatePathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.modelPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    public void addBlockModels(BlockModelGenerators builder) {

    }

    public void addItemModels(ItemModelGenerators builder) {

    }

    @Deprecated(forRemoval = true)
    protected boolean throwForMissingBlocks() {
        return true;
    }

    protected boolean skipValidation() {
        return !this.throwForMissingBlocks();
    }

    protected void skipBlock(Block block) {
        this.skipped.add(block);
    }

    protected void skipItem(Item item) {
        this.skipped.add(item);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<Block, BlockStateGenerator> generators = Maps.newHashMap();
        Consumer<BlockStateGenerator> blockStateOutput = generator -> {
            Block block = generator.getBlock();
            BlockStateGenerator blockstategenerator = generators.put(block, generator);
            if (blockstategenerator != null) {
                throw new IllegalStateException("Duplicate block state definition for " + block);
            }
        };
        Map<ResourceLocation, Supplier<JsonElement>> models = Maps.newHashMap();
        Set<Item> skippedAutoModels = Sets.newHashSet();
        BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = (resourceLocation, supplier) -> {
            if (models.put(resourceLocation, supplier) != null) {
                throw new IllegalStateException("Duplicate model definition for " + resourceLocation);
            }
        };
        this.addBlockModels(new BlockModelGenerators(blockStateOutput, modelOutput, skippedAutoModels::add));
        this.addItemModels(new ItemModelGenerators(modelOutput));
        List<Block> missingBlocks;
        if (!this.skipValidation()) {
            missingBlocks = BuiltInRegistries.BLOCK.entrySet().stream().filter(entry -> {
                return entry.getKey().location().getNamespace().equals(this.modId) && !generators.containsKey(entry.getValue());
            }).map(Map.Entry::getValue).filter(Predicate.not(this.skipped::contains)).toList();
        } else {
            missingBlocks = Collections.emptyList();
        }
        if (!missingBlocks.isEmpty()) {
            throw new IllegalStateException("Missing block state definitions for " + missingBlocks);
        } else {
            BuiltInRegistries.BLOCK.entrySet().forEach(entry -> {
                Item item = Item.BY_BLOCK.get(entry.getValue());
                if (item != null) {
                    if (!entry.getKey().location().getNamespace().equals(this.modId) || skippedAutoModels.contains(item)) {
                        return;
                    }

                    ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(item);
                    if (!models.containsKey(resourcelocation)) {
                        models.put(resourcelocation, new DelegatedModel(ModelLocationUtils.getModelLocation(entry.getValue())));
                    }
                }
            });
            List<Item> missingItems;
            if (!this.skipValidation()) {
                missingItems = BuiltInRegistries.ITEM.entrySet().stream().filter(entry -> {
                    return entry.getKey().location().getNamespace().equals(this.modId) && !models.containsKey(decorateItemModelLocation(entry.getKey().location()));
                }).map(Map.Entry::getValue).filter(Predicate.not(this.skipped::contains)).toList();
            } else {
                missingItems = Collections.emptyList();
            }
            if (!missingItems.isEmpty()) {
                throw new IllegalStateException("Missing item models for " + missingItems);
            } else {
                return CompletableFuture.allOf(saveCollection(output, generators, block -> {
                    return this.blockStatePathProvider.json(block.builtInRegistryHolder().key().location());
                }), saveCollection(output, models, this.modelPathProvider::json));
            }
        }
    }

    @Override
    public final String getName() {
        return "Model Definitions";
    }

    private static <T> CompletableFuture<?> saveCollection(CachedOutput output, Map<T, ? extends Supplier<JsonElement>> map, Function<T, Path> pathExtractor) {
        return CompletableFuture.allOf(map.entrySet().stream().map((entry) -> {
            Path path = pathExtractor.apply(entry.getKey());
            JsonElement jsonElement = entry.getValue().get();
            return DataProvider.saveStable(output, jsonElement, path);
        }).toArray(CompletableFuture[]::new));
    }

    public static ResourceLocation getModelLocation(Block block) {
        return decorateBlockModelLocation(getLocation(block));
    }

    public static ResourceLocation decorateBlockModelLocation(ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(BLOCK_PATH + "/");
    }

    public static ResourceLocation getLocation(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static String getName(Block block) {
        return getLocation(block).getPath();
    }

    public static ResourceLocation getModelLocation(Item item) {
        return decorateItemModelLocation(getLocation(item));
    }

    public static ResourceLocation decorateItemModelLocation(ResourceLocation resourceLocation) {
        return resourceLocation.withPrefix(ITEM_PATH + "/");
    }

    public static ResourceLocation getLocation(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static String getName(Item item) {
        return getLocation(item).getPath();
    }

    /**
     * Removes everything from the beginning of a resource location path up until and including the last occurrence of the specified string.
     * <p>Useful for e.g. removing directories from a path, like <code>minecraft:item/stick</code> -> <code>minecraft:stick</code> by passing <code>/</code>.
     */
    public static ResourceLocation stripUntil(ResourceLocation resourceLocation, String s) {
        String path = resourceLocation.getPath();
        if (path.contains(s)) {
            path = path.substring(path.lastIndexOf(s) + 1);
            return new ResourceLocation(resourceLocation.getNamespace(), path);
        } else {
            return resourceLocation;
        }
    }

    public static ModelTemplate createBlockModelTemplate(ResourceLocation blockModelLocation, TextureSlot... requiredSlots) {
        return createItemModelTemplate(blockModelLocation, "", requiredSlots);
    }

    public static ModelTemplate createItemModelTemplate(ResourceLocation itemModelLocation, TextureSlot... requiredSlots) {
        return createItemModelTemplate(itemModelLocation, "", requiredSlots);
    }

    public static ModelTemplate createBlockModelTemplate(ResourceLocation blockModelLocation, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(decorateBlockModelLocation(blockModelLocation)), Optional.of(suffix), requiredSlots);
    }

    public static ModelTemplate createItemModelTemplate(ResourceLocation itemModelLocation, String suffix, TextureSlot... requiredSlots) {
        return new ModelTemplate(Optional.of(decorateItemModelLocation(itemModelLocation)), Optional.of(suffix), requiredSlots);
    }
    
    public static ResourceLocation generateFlatItem(ResourceLocation resourceLocation, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        return modelTemplate.create(decorateItemModelLocation(resourceLocation),
                TextureMapping.layer0(decorateItemModelLocation(resourceLocation)),
                modelOutput
        );
    }

    public static ResourceLocation generateFlatItem(Item item, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, ItemOverride... itemOverrides) {
        return generateFlatItem(item,
                modelTemplate,
                modelOutput,
                overrides(modelTemplate,
                        Stream.of(itemOverrides)
                                .map(override -> (ItemOverride.Factory) (ResourceLocation resourceLocation) -> override)
                                .toArray(ItemOverride.Factory[]::new)
                )
        );
    }

    public static ResourceLocation generateFlatItem(Item item, ModelTemplate modelTemplate, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, ModelTemplate.JsonFactory factory) {
        return modelTemplate.create(ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(item),
                modelOutput,
                factory
        );
    }

    /**
     * Use in conjunction with {@link ModelTemplate#create(ResourceLocation, TextureMapping, BiConsumer, ModelTemplate.JsonFactory)}.
     *
     * @deprecated replaced with {@link ItemModelProperties#overridesFactory(ModelTemplate, ItemModelProperties...)}
     */
    @Deprecated(forRemoval = true)
    public static ModelTemplate.JsonFactory overrides(ModelTemplate modelTemplate, ItemOverride.Factory... factories) {
        return (ResourceLocation resourceLocation, Map<TextureSlot, ResourceLocation> map) -> {
            JsonObject jsonObject = modelTemplate.createBaseTemplate(resourceLocation, map);
            JsonArray jsonArray = new JsonArray();
            for (ItemOverride.Factory factory : factories) {
                jsonArray.add(factory.apply(resourceLocation).toJson());
            }
            jsonObject.add("overrides", jsonArray);
            return jsonObject;
        };
    }

    /**
     * @deprecated replaced with {@link ItemModelProperties}
     */
    @Deprecated(forRemoval = true)
    public record ItemOverride(ResourceLocation model, Map<ResourceLocation, Float> predicates) {

        public static ItemOverride of(ResourceLocation model, ResourceLocation p1, float f1) {
            return new ItemOverride(model, Map.of(p1, f1));
        }

        public static ItemOverride of(ResourceLocation model, ResourceLocation p1, float f1, ResourceLocation p2, float f2) {
            return new ItemOverride(model, Map.of(p1, f1, p2, f2));
        }

        public static ItemOverride of(ResourceLocation model, ResourceLocation p1, float f1, ResourceLocation p2, float f2, ResourceLocation p3, float f3) {
            return new ItemOverride(model, Map.of(p1, f1, p2, f2, p3, f3));
        }

        JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            JsonObject predicates = new JsonObject();
            for (Map.Entry<ResourceLocation, Float> entry : this.predicates.entrySet()) {
                predicates.addProperty(entry.getKey().toString(), entry.getValue());
            }
            jsonObject.add("predicate", predicates);
            jsonObject.addProperty("model", this.model.toString());
            return jsonObject;
        }

        @Deprecated(forRemoval = true)
        @FunctionalInterface
        public interface Factory extends Function<ResourceLocation, ItemOverride> {

        }
    }
}
