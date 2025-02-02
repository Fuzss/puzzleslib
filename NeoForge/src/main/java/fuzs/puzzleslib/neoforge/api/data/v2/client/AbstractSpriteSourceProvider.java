package fuzs.puzzleslib.neoforge.api.data.v2.client;

import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * TODO rename to {@code AbstractAtlasProvider} and also rename {@link #addSpriteSources()}
 */
public abstract class AbstractSpriteSourceProvider extends SpriteSourceProvider {

    public AbstractSpriteSourceProvider(NeoForgeDataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries(), context.getFileHelper());
    }

    public AbstractSpriteSourceProvider(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
        super(packOutput, lookupProvider, modId, fileHelper);
    }

    @Override
    protected final void gather() {
        this.addSpriteSources();
    }

    public abstract void addSpriteSources();

    public void addMaterial(Material material) {
        ResourceLocation resourceLocation = material.atlasLocation().withPath((String s) -> {
            return s.replace("textures/atlas/", "").replace(".png", "");
        });
        this.atlas(resourceLocation).addSource(new SingleFile(material.texture(), Optional.empty()));
    }

    @Override
    public String getName() {
        return "Sprite Sources";
    }
}
