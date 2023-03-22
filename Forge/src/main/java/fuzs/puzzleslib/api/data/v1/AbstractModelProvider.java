package fuzs.puzzleslib.api.data.v1;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;

public abstract class AbstractModelProvider extends BlockStateProvider {

    public AbstractModelProvider(PackOutput packOutput, String modId, ExistingFileHelper fileHelper) {
        super(packOutput, modId, fileHelper);
    }

    @Override
    protected abstract void registerStatesAndModels();

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

    public ItemModelBuilder basicItem(Item item) {
        return this.itemModels().basicItem(item);
    }

    public ItemModelBuilder basicItem(ResourceLocation item) {
        return this.itemModels().basicItem(item);
    }

    public ItemModelBuilder spawnEgg(Item item) {
        return this.spawnEgg(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    }

    public ItemModelBuilder spawnEgg(ResourceLocation item) {
        return this.itemModels().getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("minecraft:item/template_spawn_egg"));
    }
}
