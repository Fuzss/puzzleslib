package fuzs.puzzleslib.api.client.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.AtlasProvider;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.Material;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractAtlasProvider extends AtlasProvider {
    private final Map<Identifier, AtlasManager.AtlasConfig> atlasByTexture = AtlasManager.KNOWN_ATLASES.stream()
            .collect(Collectors.toMap(AtlasManager.AtlasConfig::textureId, Function.identity()));
    private final Map<Identifier, List<SpriteSource>> values = new LinkedHashMap<>();

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
                .map((Map.Entry<Identifier, List<SpriteSource>> entry) -> {
                    return this.storeAtlas(output, entry.getKey(), entry.getValue());
                })
                .toArray(CompletableFuture[]::new));
    }

    public abstract void addAtlases();

    protected void addMaterial(Material material) {
        this.add(this.atlasByTexture.get(material.atlasLocation()).definitionLocation(), forMaterial(material));
    }

    protected void add(Identifier identifier, SpriteSource... spriteSources) {
        this.add(identifier, Arrays.asList(spriteSources));
    }

    protected void add(Identifier identifier, List<SpriteSource> spriteSources) {
        this.values.computeIfAbsent(identifier, (Identifier identifierX) -> new ArrayList<>())
                .addAll(spriteSources);
    }
}
