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

    protected SourceList sheet(ResourceLocation resourceLocation) {
        ResourceLocation atlasLocation = ModelManager.VANILLA_ATLASES.get(resourceLocation);
        Objects.requireNonNull(atlasLocation, "atlas for " + resourceLocation + " is null");
        return this.atlas(atlasLocation);
    }

    protected void material(Material material) {
        this.material(material.atlasLocation(), material.texture());
    }

    protected void material(ResourceLocation atlasLocation, ResourceLocation textureLocation) {
        this.sheet(atlasLocation).addSource(new SingleFile(textureLocation, Optional.empty()));
    }

    @Override
    public String getName() {
        return "Atlases";
    }
}
