package fuzs.puzzleslib.api.client.data.v2;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.init.v3.family.BlockSetFamily;
import fuzs.puzzleslib.api.init.v3.family.BlockSetVariant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class AbstractModelProvider implements DataProvider {
    /**
     * @see #generateForItems(ItemModelGenerators, BlockSetFamily, Map)
     */
    public static final Map<BlockSetVariant, BiConsumer<ItemModelGenerators, Item>> VARIANT_WOOD_ITEM_PROVIDERS = ImmutableMap.<BlockSetVariant, BiConsumer<ItemModelGenerators, Item>>builder()
            .put(BlockSetVariant.BOAT, (ItemModelGenerators itemModelGenerators, Item item) -> {
                itemModelGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            })
            .put(BlockSetVariant.CHEST_BOAT, (ItemModelGenerators itemModelGenerators, Item item) -> {
                itemModelGenerators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            })
            .build();

    private final String modId;
    private final PackOutput.PathProvider blockStatePathProvider;
    private final PackOutput.PathProvider itemInfoPathProvider;
    private final PackOutput.PathProvider modelPathProvider;
    private final Set<Object> skipValidation = new HashSet<>();

    public AbstractModelProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    public AbstractModelProvider(String modId, PackOutput packOutput) {
        this.modId = modId;
        this.blockStatePathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.itemInfoPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        this.modelPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    /**
     * @see #generateForBlocks(BlockModelGenerators, BlockSetFamily, Map)
     */
    public static Map<BlockSetVariant, BiConsumer<BlockModelGenerators, Block>> createVariantWoodBlockProviders(BlockSetFamily blockSetFamily, Block strippedBlock) {
        return ImmutableMap.<BlockSetVariant, BiConsumer<BlockModelGenerators, Block>>builder()
                .put(BlockSetVariant.HANGING_SIGN, (BlockModelGenerators blockModelGenerators, Block block) -> {
                    Holder.Reference<Block> wallHangingSign = blockSetFamily.getBlock(BlockSetVariant.WALL_HANGING_SIGN);
                    Objects.requireNonNull(wallHangingSign, "wall hanging sign is null");
                    blockModelGenerators.createHangingSign(strippedBlock, block, wallHangingSign.value());
                })
                .put(BlockSetVariant.SHELF, (BlockModelGenerators blockModelGenerators, Block block) -> {
                    blockModelGenerators.createShelf(block, strippedBlock);
                })
                .build();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        BlockStateOutputImpl blockStateOutput = new BlockStateOutputImpl(this::isValid);
        ItemModelOutputImpl itemModelOutput = new ItemModelOutputImpl(this::isValid);
        ModelProvider.SimpleModelCollector modelOutput = new ModelProvider.SimpleModelCollector();
        this.addBlockModels(this.setupBlockModelGenerators(new BlockModelGenerators(blockStateOutput,
                itemModelOutput,
                modelOutput)));
        this.addItemModels(new ItemModelGenerators(itemModelOutput, modelOutput));
        blockStateOutput.validate();
        itemModelOutput.finalizeAndValidate();
        return CompletableFuture.allOf(blockStateOutput.save(output, this.blockStatePathProvider),
                modelOutput.save(output, this.modelPathProvider),
                itemModelOutput.save(output, this.itemInfoPathProvider));
    }

    private BlockModelGenerators setupBlockModelGenerators(BlockModelGenerators blockModelGenerators) {
        // Make all these mutable, so it is possible to add our own entries.
        BlockModelGenerators.NON_ORIENTABLE_TRAPDOOR = new ArrayList<>(BlockModelGenerators.NON_ORIENTABLE_TRAPDOOR);
        BlockModelGenerators.FULL_BLOCK_MODEL_CUSTOM_GENERATORS = new HashMap<>(BlockModelGenerators.FULL_BLOCK_MODEL_CUSTOM_GENERATORS);
        BlockModelGenerators.TEXTURED_MODELS = new HashMap<>(BlockModelGenerators.TEXTURED_MODELS);
        return blockModelGenerators;
    }

    private <T> boolean isValid(Holder.Reference<T> holder) {
        if (!this.skipAllValidation()) {
            if (holder.key().identifier().getNamespace().equals(this.modId)) {
                return !this.skipValidation.contains(holder.value());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void addBlockModels(BlockModelGenerators blockModelGenerators) {
        // NO-OP
    }

    public void addItemModels(ItemModelGenerators itemModelGenerators) {
        // NO-OP
    }

    /**
     * @see BlockModelGenerators#family(Block)
     */
    public void generateForBlocks(BlockModelGenerators blockModelGenerators, BlockSetFamily blockSetFamily, Map<BlockSetVariant, BiConsumer<BlockModelGenerators, Block>> variants) {
        this.generateForBlocks(blockModelGenerators,
                blockSetFamily,
                variants,
                TexturedModel.CUBE.get(blockSetFamily.getBaseBlock().value()));
    }

    /**
     * @see BlockModelGenerators#family(Block)
     */
    public void generateForBlocks(BlockModelGenerators blockModelGenerators, BlockSetFamily blockSetFamily, Map<BlockSetVariant, BiConsumer<BlockModelGenerators, Block>> variants, TexturedModel texturedModel) {
        BlockFamily blockFamily = blockSetFamily.getBlockFamily();
        if (blockFamily.shouldGenerateModel()) {
            BlockModelGenerators.BlockFamilyProvider familyProvider = blockModelGenerators.new BlockFamilyProvider(
                    texturedModel.getMapping());
            familyProvider.fullBlock = BlockModelGenerators.plainModel(texturedModel.getTemplate()
                    .getDefaultModelLocation(blockFamily.getBaseBlock()));
            familyProvider.generateFor(blockFamily);
            blockSetFamily.getBlockVariants().forEach((BlockSetVariant variant, Holder.Reference<Block> holder) -> {
                BiConsumer<BlockModelGenerators, Block> modelProvider = variants.get(variant);
                if (modelProvider != null) {
                    modelProvider.accept(blockModelGenerators, holder.value());
                }
            });
        }
    }

    public void generateForItems(ItemModelGenerators itemModelGenerators, BlockSetFamily blockSetFamily, Map<BlockSetVariant, BiConsumer<ItemModelGenerators, Item>> variants) {
        BlockFamily blockFamily = blockSetFamily.getBlockFamily();
        if (blockFamily.shouldGenerateModel()) {
            blockSetFamily.getItemVariants().forEach((BlockSetVariant variant, Holder.Reference<Item> holder) -> {
                BiConsumer<ItemModelGenerators, Item> modelProvider = variants.get(variant);
                if (modelProvider != null) {
                    modelProvider.accept(itemModelGenerators, holder.value());
                }
            });
        }
    }

    protected boolean skipAllValidation() {
        return false;
    }

    protected final void skipBlockValidation(Block block) {
        this.skipValidation.add(block);
    }

    protected final void skipItemValidation(Item item) {
        this.skipValidation.add(item);
    }

    @Override
    public final String getName() {
        return "Model Definitions";
    }

    public interface CustomItemModelOutput extends ItemModelOutput {

        void accept(Item item, ItemModel.Unbaked model, ClientItem.Properties properties);

        default void accept(Identifier identifier, ItemModel.Unbaked model) {
            this.accept(identifier, model, ClientItem.Properties.DEFAULT);
        }

        void accept(Identifier identifier, ItemModel.Unbaked model, ClientItem.Properties properties);
    }

    static class BlockStateOutputImpl extends ModelProvider.BlockStateGeneratorCollector {
        private final Predicate<Holder.Reference<Block>> filter;

        public BlockStateOutputImpl(Predicate<Holder.Reference<Block>> filter) {
            this.filter = filter;
        }

        @Override
        public void validate() {
            List<Identifier> list = BuiltInRegistries.BLOCK.listElements()
                    // apply a filter, so we only consider our own content
                    .filter(this.filter)
                    .filter((Holder.Reference<Block> holder) -> !this.generators.containsKey(holder.value()))
                    .map((Holder.Reference<Block> holder) -> holder.key().identifier())
                    .toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Missing blockstate definitions for: " + list);
            }
        }
    }

    private static class ItemModelOutputImpl extends ModelProvider.ItemInfoCollector implements CustomItemModelOutput {
        private final Map<Identifier, ClientItem> additionalItemInfos = new HashMap<>();
        private final Predicate<Holder.Reference<Item>> filter;

        public ItemModelOutputImpl(Predicate<Holder.Reference<Item>> filter) {
            this.filter = filter;
        }

        @Override
        public void accept(Item item, ItemModel.Unbaked model, ClientItem.Properties properties) {
            this.register(item, new ClientItem(model, properties));
        }

        @Override
        public void accept(Identifier identifier, ItemModel.Unbaked model, ClientItem.Properties properties) {
            ClientItem clientItem = this.additionalItemInfos.put(identifier, new ClientItem(model, properties));
            if (clientItem != null) {
                throw new IllegalStateException("Duplicate item model definition for " + identifier);
            }
        }

        @Override
        public void finalizeAndValidate() {
            // apply a filter, so we only consider our own content
            BuiltInRegistries.ITEM.listElements().filter(this.filter).forEach((Holder.Reference<Item> item) -> {
                if (!this.copies.containsKey(item.value())) {
                    if (item.value() instanceof BlockItem blockItem && !this.itemInfos.containsKey(blockItem)) {
                        Identifier identifier = ModelLocationUtils.getModelLocation(blockItem.getBlock());
                        this.accept(blockItem, ItemModelUtils.plainModel(identifier));
                    }
                }
            });
            this.copies.forEach((Item item, Item other) -> {
                ClientItem clientItem = this.itemInfos.get(other);
                if (clientItem == null) {
                    throw new IllegalStateException("Missing donor: " + other + " -> " + item);
                } else {
                    this.register(item, clientItem);
                }
            });
            List<Identifier> list = BuiltInRegistries.ITEM.listElements()
                    // apply a filter, so we only consider our own content
                    .filter(this.filter)
                    .filter((Holder.Reference<Item> item) -> !this.itemInfos.containsKey(item.value()))
                    .map((Holder.Reference<Item> item) -> item.key().identifier())
                    .toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Missing item model definitions for: " + list);
            }
        }

        @Override
        public CompletableFuture<?> save(CachedOutput output, PackOutput.PathProvider pathProvider) {
            return CompletableFuture.allOf(super.save(output, pathProvider),
                    DataProvider.saveAll(output, ClientItem.CODEC, pathProvider::json, this.additionalItemInfos));
        }
    }
}
