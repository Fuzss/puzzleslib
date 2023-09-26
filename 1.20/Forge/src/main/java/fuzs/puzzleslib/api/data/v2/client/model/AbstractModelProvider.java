package fuzs.puzzleslib.api.data.v2.client.model;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;

public abstract class AbstractModelProvider extends BlockStateProvider {

    public AbstractModelProvider(GatherDataEvent evt, String modId) {
        this(evt.getGenerator().getPackOutput(), evt.getExistingFileHelper(), modId);
    }

    public AbstractModelProvider(PackOutput packOutput, ExistingFileHelper fileHelper, String modId) {
        super(packOutput, modId, fileHelper);
    }

    @Deprecated(forRemoval = true)
    public AbstractModelProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper) {
        this(packOutput, fileHelper, modId);
    }

    @Override
    protected abstract void registerStatesAndModels();

    /**
     * Creates a block states definition for a simple block with an already existing model, useful when the model has been created with an external tool like Blockbench.
     *
     * @param block the block whose id to use for both the block states file and the existing model reference
     */
    public void simpleExistingBlock(Block block) {
        this.simpleBlock(block, existingBlockModel(block));
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
