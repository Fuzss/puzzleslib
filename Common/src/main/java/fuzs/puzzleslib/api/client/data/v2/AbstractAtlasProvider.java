package fuzs.puzzleslib.api.client.data.v2;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.data.v2.models.MaterialMapper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public abstract class AbstractAtlasProvider implements DataProvider {
    /**
     * Copied from Minecraft 26.2.
     */
    public static final ResourceLocation TRIM_PALETTE_KEY = ResourceLocation.withDefaultNamespace(
            "trims/color_palettes/trim_palette");
    /**
     * Copied from Minecraft 26.2.
     */
    public static final List<ResourceKey<TrimPattern>> VANILLA_PATTERNS = List.of(TrimPatterns.SENTRY,
            TrimPatterns.DUNE,
            TrimPatterns.COAST,
            TrimPatterns.WILD,
            TrimPatterns.WARD,
            TrimPatterns.EYE,
            TrimPatterns.VEX,
            TrimPatterns.TIDE,
            TrimPatterns.SNOUT,
            TrimPatterns.RIB,
            TrimPatterns.SPIRE,
            TrimPatterns.WAYFINDER,
            TrimPatterns.SHAPER,
            TrimPatterns.SILENCE,
            TrimPatterns.RAISER,
            TrimPatterns.HOST,
            TrimPatterns.FLOW,
            TrimPatterns.BOLT);

    private final Map<ResourceLocation, List<SpriteSource>> values = new LinkedHashMap<>();
    private final PackOutput.PathProvider pathProvider;

    public AbstractAtlasProvider(DataProviderContext context) {
        this(context.getPackOutput());
    }

    public AbstractAtlasProvider(PackOutput packOutput) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "atlases");
    }

    public static SpriteSource forMaterial(Material material) {
        return new SingleFile(material.texture(), Optional.empty());
    }

    public static SpriteSource forMapper(MaterialMapper mapper) {
        return new DirectoryLister(mapper.prefix(), mapper.prefix() + "/");
    }

    public static List<SpriteSource> simpleMapper(MaterialMapper mapper) {
        return List.of(forMapper(mapper));
    }

    public static List<SpriteSource> noPrefixMapper(String path) {
        return List.of(new DirectoryLister(path, ""));
    }

    public static TrimPatternBuilder armorTrims() {
        return new TrimPatternBuilder();
    }

    public static TrimPatternBuilder armorTrimPatterns() {
        return armorTrims().addVanillaLayers().addVanillaPermutations();
    }

    public static TrimPatternBuilder armorTrimPermutations() {
        return armorTrims().addVanillaPatterns().addVanillaLayers();
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

    public final CompletableFuture<?> storeAtlas(CachedOutput output, ResourceLocation atlasId, List<SpriteSource> sources) {
        return DataProvider.saveStable(output,
                RegistryAccess.EMPTY,
                SpriteSources.FILE_CODEC,
                sources,
                this.pathProvider.json(atlasId));
    }

    public abstract void addAtlases();

    protected void addMaterial(Material material) {
        this.add(ModelManager.VANILLA_ATLASES.get(material.atlasLocation()), forMaterial(material));
    }

    protected void add(ResourceLocation id, SpriteSource... spriteSources) {
        this.add(id, Arrays.asList(spriteSources));
    }

    protected void add(ResourceLocation id, List<SpriteSource> spriteSources) {
        this.values.computeIfAbsent(id, (ResourceLocation location) -> new ArrayList<>()).addAll(spriteSources);
    }

    @Override
    public String getName() {
        return "Atlas Definitions";
    }

    public static class TrimPatternBuilder {
        private final List<ResourceKey<TrimPattern>> patterns = new ArrayList<>();
        private final List<UnaryOperator<String>> layers = new ArrayList<>();
        private final Map<String, ResourceLocation> permutations = new TreeMap<>();
        private ResourceLocation palette = TRIM_PALETTE_KEY;
        private @Nullable String namespaceOverride;

        TrimPatternBuilder() {
            // NO-OP
        }

        public TrimPatternBuilder addPattern(ResourceKey<TrimPattern> pattern) {
            this.patterns.add(pattern);
            return this;
        }

        public TrimPatternBuilder addPatterns(List<ResourceKey<TrimPattern>> patterns) {
            this.patterns.addAll(patterns);
            return this;
        }

        public TrimPatternBuilder addVanillaPatterns() {
            return this.addPatterns(VANILLA_PATTERNS);
        }

        public TrimPatternBuilder addLayer(UnaryOperator<String> layer) {
            this.layers.add(layer);
            return this;
        }

        public TrimPatternBuilder addLayers(List<UnaryOperator<String>> layers) {
            this.layers.addAll(layers);
            return this;
        }

        public TrimPatternBuilder addVanillaLayers() {
            return this.addLayer((String name) -> "trims/models/armor/" + name)
                    .addLayer((String name) -> "trims/models/armor/" + name + "_leggings");
        }

        public TrimPatternBuilder addPermutation(ItemModelGenerators.TrimModelData data) {
            data.overrideArmorMaterials().values().forEach(this::addPermutation);
            return this.addPermutation(data.name());
        }

        public TrimPatternBuilder addPermutations(List<ItemModelGenerators.TrimModelData> data) {
            data.forEach(this::addPermutation);
            return this;
        }

        public TrimPatternBuilder addPermutation(String asset) {
            Objects.requireNonNull(this.namespaceOverride, "namespace is null");
            return this.addPermutation(ResourceLocation.fromNamespaceAndPath(this.namespaceOverride, asset));
        }

        public TrimPatternBuilder addPermutation(ResourceLocation base, Map<Holder<ArmorMaterial>, ResourceLocation> overrides) {
            overrides.values().forEach(this::addPermutation);
            return this.addPermutation(base);
        }

        public TrimPatternBuilder addPermutation(ResourceLocation asset) {
            this.permutations.put(asset.getPath(), asset.withPrefix("trims/color_palettes/"));
            return this;
        }

        public TrimPatternBuilder addPermutation(String suffix, ResourceLocation palette) {
            this.permutations.put(suffix, palette);
            return this;
        }

        public TrimPatternBuilder addPermutations(Map<String, ResourceLocation> permutations) {
            this.permutations.putAll(permutations);
            return this;
        }

        public TrimPatternBuilder addVanillaPermutations() {
            return this.setNamespaceOverride(ResourceLocation.DEFAULT_NAMESPACE)
                    .addPermutations(ItemModelGenerators.GENERATED_TRIM_MODELS)
                    .setNamespaceOverride(null);
        }

        public TrimPatternBuilder setPalette(ResourceLocation palette) {
            Objects.requireNonNull(palette, "palette is null");
            this.palette = palette;
            return this;
        }

        public TrimPatternBuilder setNamespaceOverride(@Nullable String namespaceOverride) {
            this.namespaceOverride = namespaceOverride;
            return this;
        }

        public List<SpriteSource> build() {
            Preconditions.checkArgument(!this.patterns.isEmpty(), "patterns is empty");
            Preconditions.checkArgument(!this.layers.isEmpty(), "layers is empty");
            Preconditions.checkArgument(!this.permutations.isEmpty(), "permutations is empty");
            List<ResourceLocation> textures = patternTextures(this.patterns, this.layers);
            return List.of(new PalettedPermutations(textures, this.palette, this.permutations));
        }

        private static List<ResourceLocation> patternTextures(List<ResourceKey<TrimPattern>> patterns, List<UnaryOperator<String>> layers) {
            List<ResourceLocation> result = new ArrayList<>(patterns.size() * layers.size());
            for (ResourceKey<TrimPattern> vanillaPattern : patterns) {
                ResourceLocation assetId = vanillaPattern.location();
                for (UnaryOperator<String> humanoidLayer : layers) {
                    result.add(assetId.withPath(humanoidLayer));
                }
            }

            return result;
        }
    }
}
