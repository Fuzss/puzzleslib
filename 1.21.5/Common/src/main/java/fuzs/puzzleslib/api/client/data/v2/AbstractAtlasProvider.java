package fuzs.puzzleslib.api.client.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.AtlasProvider;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractAtlasProvider extends AtlasProvider {
    private final Map<ResourceLocation, List<SpriteSource>> atlases = new HashMap<>();
    protected final String modId;

    public AbstractAtlasProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput());
    }

    public AbstractAtlasProvider(String modId, PackOutput packOutput) {
        super(packOutput);
        this.modId = modId;
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput output) {
        this.addAtlases();
        return CompletableFuture.allOf(this.atlases.entrySet()
                .stream()
                .map((Map.Entry<ResourceLocation, List<SpriteSource>> entry) -> {
                    return this.storeAtlas(output, entry.getKey(), entry.getValue());
                })
                .toArray(CompletableFuture[]::new));
    }

    public abstract void addAtlases();

    protected void addMaterial(Material material) {
        this.add(ModelManager.VANILLA_ATLASES.get(material.atlasLocation()), forMaterial(material));
    }

    protected void add(ResourceLocation resourceLocation, SpriteSource... spriteSources) {
        this.add(resourceLocation, Arrays.asList(spriteSources));
    }

    protected void add(ResourceLocation resourceLocation, List<SpriteSource> spriteSources) {
        this.atlases.computeIfAbsent(resourceLocation, (ResourceLocation resourceLocationX) -> new ArrayList<>())
                .addAll(spriteSources);
    }
}
