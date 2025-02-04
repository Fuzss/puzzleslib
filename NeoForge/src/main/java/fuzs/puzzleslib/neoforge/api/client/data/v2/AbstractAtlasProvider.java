package fuzs.puzzleslib.neoforge.api.client.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractAtlasProvider extends SpriteSourceProvider {

    public AbstractAtlasProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractAtlasProvider(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, modId);
    }

    @Override
    protected final void gather() {
        this.addAtlases();
    }

    public abstract void addAtlases();

    public void addMaterial(Material material) {
        ResourceLocation resourceLocation = ModelManager.VANILLA_ATLASES.get(material.atlasLocation());
        Objects.requireNonNull(resourceLocation, "atlas for " + material.atlasLocation() + " is null");
        this.atlas(resourceLocation).addSource(new SingleFile(material.texture(), Optional.empty()));
    }

    @Override
    public String getName() {
        return "Atlases";
    }
}
