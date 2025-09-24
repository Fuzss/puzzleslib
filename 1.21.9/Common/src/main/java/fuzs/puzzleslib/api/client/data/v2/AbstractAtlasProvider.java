package fuzs.puzzleslib.api.client.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.AtlasProvider;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractAtlasProvider extends AtlasProvider {
    private final Map<ResourceLocation, AtlasManager.AtlasConfig> atlasByTexture = AtlasManager.KNOWN_ATLASES.stream()
            .collect(Collectors.toMap(AtlasManager.AtlasConfig::textureId, Function.identity()));
    private final Map<ResourceLocation, List<SpriteSource>> values = new LinkedHashMap<>();

    public AbstractAtlasProvider(DataProviderContext context) {
        this(context.getPackOutput());
    }

    public AbstractAtlasProvider(PackOutput packOutput) {
        super(packOutput);
    }

    @Override
    public final CompletableFuture<?> run(CachedOutput output) {
        this.addAtlases();
        return CompletableFuture.allOf(this.values.entrySet()
                .stream()
                .map((Map.Entry<ResourceLocation, List<SpriteSource>> entry) -> {
                    return this.storeAtlas(output, entry.getKey(), entry.getValue());
                })
                .toArray(CompletableFuture[]::new));
    }

    public abstract void addAtlases();

    protected void addMaterial(Material material) {
        this.add(this.atlasByTexture.get(material.atlasLocation()).definitionLocation(), forMaterial(material));
    }

    protected void add(ResourceLocation resourceLocation, SpriteSource... spriteSources) {
        this.add(resourceLocation, Arrays.asList(spriteSources));
    }

    protected void add(ResourceLocation resourceLocation, List<SpriteSource> spriteSources) {
        this.values.computeIfAbsent(resourceLocation, (ResourceLocation resourceLocationX) -> new ArrayList<>())
                .addAll(spriteSources);
    }
}
