package fuzs.puzzleslib.api.client.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public abstract class AbstractModelProvider implements DataProvider {
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
        // make all these mutable, so it is possibly to add our own entries
        BlockModelGenerators.NON_ORIENTABLE_TRAPDOOR = new ArrayList<>(BlockModelGenerators.NON_ORIENTABLE_TRAPDOOR);
        BlockModelGenerators.FULL_BLOCK_MODEL_CUSTOM_GENERATORS = new HashMap<>(BlockModelGenerators.FULL_BLOCK_MODEL_CUSTOM_GENERATORS);
        BlockModelGenerators.TEXTURED_MODELS = new HashMap<>(BlockModelGenerators.TEXTURED_MODELS);
        return blockModelGenerators;
    }

    private <T> boolean isValid(Holder.Reference<T> holder) {
        if (!this.skipAllValidation()) {
            if (holder.key().location().getNamespace().equals(this.modId)) {
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
    }

    static class BlockStateOutputImpl extends ModelProvider.BlockStateGeneratorCollector {
        private final Predicate<Holder.Reference<Block>> filter;

        public BlockStateOutputImpl(Predicate<Holder.Reference<Block>> filter) {
            this.filter = filter;
        }

        @Override
        public void validate() {
            List<ResourceLocation> list = BuiltInRegistries.BLOCK.listElements()
                    // apply a filter, so we only consider our own content
                    .filter(this.filter)
                    .filter((Holder.Reference<Block> holder) -> !this.generators.containsKey(holder.value()))
                    .map((Holder.Reference<Block> holder) -> holder.key().location())
                    .toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Missing blockstate definitions for: " + list);
            }
        }
    }

    static class ItemModelOutputImpl extends ModelProvider.ItemInfoCollector implements CustomItemModelOutput {
        private final Predicate<Holder.Reference<Item>> filter;

        public ItemModelOutputImpl(Predicate<Holder.Reference<Item>> filter) {
            this.filter = filter;
        }

        @Override
        public void accept(Item item, ItemModel.Unbaked model, ClientItem.Properties properties) {
            this.register(item, new ClientItem(model, properties));
        }

        @Override
        public void finalizeAndValidate() {
            // apply a filter, so we only consider our own content
            BuiltInRegistries.ITEM.listElements().filter(this.filter).forEach((Holder.Reference<Item> item) -> {
                if (!this.copies.containsKey(item.value())) {
                    if (item.value() instanceof BlockItem blockItem && !this.itemInfos.containsKey(blockItem)) {
                        ResourceLocation resourceLocation = ModelLocationUtils.getModelLocation(blockItem.getBlock());
                        this.accept(blockItem, ItemModelUtils.plainModel(resourceLocation));
                    }
                }
            });
            this.copies.forEach((Item item, Item item2) -> {
                ClientItem clientItem = this.itemInfos.get(item2);
                if (clientItem == null) {
                    throw new IllegalStateException("Missing donor: " + item2 + " -> " + item);
                } else {
                    this.register(item, clientItem);
                }
            });
            List<ResourceLocation> list = BuiltInRegistries.ITEM.listElements()
                    // apply a filter, so we only consider our own content
                    .filter(this.filter)
                    .filter((Holder.Reference<Item> item) -> !this.itemInfos.containsKey(item.value()))
                    .map((Holder.Reference<Item> item) -> item.key().location())
                    .toList();
            if (!list.isEmpty()) {
                throw new IllegalStateException("Missing item model definitions for: " + list);
            }
        }
    }
}
