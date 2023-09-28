package fuzs.puzzleslib.api.client.data.v2;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import fuzs.puzzlesaccessapi.api.client.data.v2.BlockModelBuilder;
import fuzs.puzzlesaccessapi.api.client.data.v2.ItemModelBuilder;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractModelProvider extends ModelProvider {
    private final String modId;
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider modelPathProvider;

    public AbstractModelProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    public AbstractModelProvider(String modId, PackOutput packOutput) {
        super(packOutput);
        this.modId = modId;
        this.blockStatePathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.modelPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    public abstract void addBlockModels(BlockModelBuilder builder);

    public abstract void addItemModels(ItemModelBuilder builder);

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
        this.addBlockModels(new BlockModelBuilder(blockStateOutput, modelOutput, skippedAutoModels::add));
        this.addItemModels(new ItemModelBuilder(modelOutput));
        List<Block> missingBlocks = BuiltInRegistries.BLOCK.entrySet().stream().filter(entry -> {
            return entry.getKey().location().getNamespace().equals(this.modId) && !generators.containsKey(entry.getValue());
        }).map(Map.Entry::getValue).toList();
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
            return CompletableFuture.allOf(saveCollection(output, generators, block -> {
                return this.blockStatePathProvider.json(block.builtInRegistryHolder().key().location());
            }), saveCollection(output, models, this.modelPathProvider::json));
        }
    }

    private static <T> CompletableFuture<?> saveCollection(CachedOutput output, Map<T, ? extends Supplier<JsonElement>> map, Function<T, Path> pathExtractor) {
        return CompletableFuture.allOf(map.entrySet().stream().map((entry) -> {
            Path path = pathExtractor.apply(entry.getKey());
            JsonElement jsonElement = entry.getValue().get();
            return DataProvider.saveStable(output, jsonElement, path);
        }).toArray(CompletableFuture[]::new));
    }
}
