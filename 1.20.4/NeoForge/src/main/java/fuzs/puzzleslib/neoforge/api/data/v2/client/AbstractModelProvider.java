package fuzs.puzzleslib.neoforge.api.data.v2.client;

import fuzs.puzzleslib.neoforge.api.data.v2.client.model.ModItemModelProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.ForgeDataProviderContext;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @deprecated replaced by {@link fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider}
 */
@Deprecated
public abstract class AbstractModelProvider extends BlockStateProvider {
    private final ModItemModelProvider itemModels;

    public AbstractModelProvider(ForgeDataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getFileHelper());
    }

    public AbstractModelProvider(String modId, PackOutput packOutput, ExistingFileHelper fileHelper) {
        super(packOutput, modId, fileHelper);
        this.itemModels = new ModItemModelProvider(packOutput, modId, fileHelper, this);
    }

    @Override
    public final ModItemModelProvider itemModels() {
        return this.itemModels;
    }

    @Override
    protected abstract void registerStatesAndModels();

    /**
     * Creates a block states definition for a simple block with an already existing model, useful when the model has been created with an external tool like Blockbench.
     *
     * @param block the block whose id to use for both the block states file and the existing model reference
     */
    public void simpleExistingBlock(Block block) {
        this.simpleBlock(block, this.existingBlockModel(block));
    }

    public void simpleExistingBlockWithItem(Block block) {
        ModelFile.ExistingModelFile model = this.existingBlockModel(block);
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    public ModelFile.ExistingModelFile existingBlockModel(Block block) {
        return new ModelFile.ExistingModelFile(this.blockTexture(block), this.models().existingFileHelper);
    }

    /**
     * Creates a simple block states definition for a block entity block that is rendered via a built-in block, only defines a particle texture by providing another block.
     *
     * @param block           the block to generate the block states definition for
     * @param particleTexture the block use for the particle texture
     */
    public void builtInBlock(Block block, Block particleTexture) {
        this.builtInBlock(block, this.blockTexture(particleTexture));
    }

    /**
     * Creates a simple block states definition for a block entity block that is rendered via a built-in block, only defines a particle texture.
     *
     * @param block           the block to generate the block states definition for
     * @param particleTexture the particle texture
     */
    public void builtInBlock(Block block, ResourceLocation particleTexture) {
        this.simpleBlock(block, this.models().getBuilder(this.name(block)).texture("particle", particleTexture));
    }

    public void cubeBottomTopBlock(Block block) {
        this.cubeBottomTopBlock(block, this.extend(this.blockTexture(block), "_side"), this.extend(this.blockTexture(block), "_bottom"), this.extend(this.blockTexture(block), "_top"));
        this.itemModels().withExistingParent(this.name(block), this.extendKey(block, ModelProvider.BLOCK_FOLDER));
    }

    public void cubeBottomTopBlock(Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        this.simpleBlock(block, this.models().cubeBottomTop(this.name(block), side, bottom, top));
    }

    public ResourceLocation key(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block);
    }

    public String name(Block block) {
        return this.key(block).getPath();
    }

    public ResourceLocation extendKey(Block block, String... extensions) {
        ResourceLocation loc = this.key(block);
        extensions = ArrayUtils.add(extensions, loc.getPath());
        return new ResourceLocation(loc.getNamespace(), String.join("/", extensions));
    }

    public ResourceLocation extend(ResourceLocation rl, String suffix) {
        return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
    }
}
