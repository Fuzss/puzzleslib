package fuzs.puzzleslib.api.data.v2.client.model;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModItemModelProvider extends ModelProvider<ModItemModelBuilder> {
    private final BlockStateProvider provider;

    public ModItemModelProvider(PackOutput output, String modId, ExistingFileHelper fileHelper, BlockStateProvider provider) {
        super(output, modId, ITEM_FOLDER, ModItemModelBuilder::new, fileHelper);
        this.provider = provider;
    }

    public ResourceLocation key(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public String name(Item item) {
        return this.key(item).getPath();
    }

    public ModItemModelBuilder basicItem(Item item) {
        return this.basicItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    }

    public ModItemModelBuilder basicItem(ResourceLocation item) {
        return this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", new ResourceLocation(item.getNamespace(), "item/" + item.getPath()));
    }

    public ModItemModelBuilder builtInItem(Item item, Block texture) {
        return this.builtInItem(item, texture, this.mcLoc("builtin/entity"));
    }

    public ModItemModelBuilder builtInItem(Item item, Block texture, ResourceLocation parent) {
        return this.getBuilder(this.name(item)).parent(this.getExistingFile(parent)).texture("particle", this.provider.blockTexture(texture));
    }

    public ModItemModelBuilder spawnEgg(Item item) {
        return this.spawnEgg(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    }

    public ModItemModelBuilder spawnEgg(ResourceLocation item) {
        return this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("minecraft:item/template_spawn_egg"));
    }

    public ModItemModelBuilder handheldItem(Item item) {
        return this.handheldItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    }

    public ModItemModelBuilder handheldItem(ResourceLocation item) {
        return this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/handheld")).texture("layer0", new ResourceLocation(item.getNamespace(), "item/" + item.getPath()));
    }

    public ModItemModelBuilder basicItem(Item item, ResourceLocation texture) {
        return this.basicItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)), texture);
    }

    public ModItemModelBuilder basicItem(ResourceLocation item, Item texture) {
        return this.basicItem(item, Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(texture)));
    }

    public ModItemModelBuilder basicItem(ResourceLocation item, ResourceLocation texture) {
        return this.getBuilder(item.toString()).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", new ResourceLocation(texture.getNamespace(), "item/" + texture.getPath()));
    }

    @NotNull
    @Override
    public String getName() {
        return "Item Models: " + this.modid;
    }

    @Override
    protected void registerModels() {

    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.completedFuture(null);
    }
}
