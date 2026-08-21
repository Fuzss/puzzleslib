package fuzs.puzzleslib.common.api.client.data.v2;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.common.api.data.v2.core.DataProviderContext;
import net.minecraft.client.data.AtlasProvider;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractAtlasProvider extends AtlasProvider {
    private final Map<Identifier, AtlasManager.AtlasConfig> knownAtlases = AtlasManager.KNOWN_ATLASES.stream()
            .collect(Collectors.toMap(AtlasManager.AtlasConfig::textureId, Function.identity()));
    private final Map<Identifier, List<SpriteSource>> values = new LinkedHashMap<>();

    public AbstractAtlasProvider(DataProviderContext context) {
        this(context.getPackOutput());
    }

    public AbstractAtlasProvider(PackOutput packOutput) {
        super(packOutput);
    }

    /**
     * @see AtlasProvider#armorTrims()
     */
    public static TrimPatternBuilder armorTrims() {
        return new TrimPatternBuilder();
    }

    /**
     * @see AtlasProvider#armorTrims()
     */
    public static TrimPatternBuilder armorTrimPatterns() {
        return armorTrims().addVanillaLayers().addVanillaPermutations();
    }

    /**
     * @see AtlasProvider#armorTrims()
     */
    public static TrimPatternBuilder armorTrimPermutations() {
        return armorTrims().addVanillaPatterns().addVanillaLayers();
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

    protected void addMaterial(SpriteId sprite) {
        this.add(this.knownAtlases.get(sprite.atlasLocation()).definitionLocation(), forMaterial(sprite));
    }

    protected void add(Identifier id, SpriteSource... sources) {
        this.add(id, Arrays.asList(sources));
    }

    protected void add(Identifier id, List<SpriteSource> sources) {
        this.values.computeIfAbsent(id, (Identifier _) -> new ArrayList<>()).addAll(sources);
    }

    public static class TrimPatternBuilder {
        private final List<ResourceKey<TrimPattern>> patterns = new ArrayList<>();
        private final List<EquipmentClientInfo.LayerType> layers = new ArrayList<>();
        private final Map<String, Identifier> permutations = new TreeMap<>();
        private Identifier palette = AtlasProvider.TRIM_PALETTE_KEY;
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
            return this.addPatterns(AtlasProvider.VANILLA_PATTERNS);
        }

        public TrimPatternBuilder addLayer(EquipmentClientInfo.LayerType layer) {
            this.layers.add(layer);
            return this;
        }

        public TrimPatternBuilder addLayers(List<EquipmentClientInfo.LayerType> layers) {
            this.layers.addAll(layers);
            return this;
        }

        public TrimPatternBuilder addVanillaLayers() {
            return this.addLayers(AtlasProvider.HUMANOID_LAYERS);
        }

        public TrimPatternBuilder addPermutation(ItemModelGenerators.TrimMaterialData data) {
            return this.addPermutation(data.assets());
        }

        public TrimPatternBuilder addPermutations(List<ItemModelGenerators.TrimMaterialData> data) {
            data.forEach(this::addPermutation);
            return this;
        }

        /**
         * @see AtlasProvider#extractAllMaterialAssets()
         */
        public TrimPatternBuilder addPermutation(MaterialAssetGroup group) {
            group.overrides().values().forEach(this::addPermutation);
            return this.addPermutation(group.base());
        }

        public TrimPatternBuilder addPermutation(MaterialAssetGroup.AssetInfo asset) {
            Objects.requireNonNull(this.namespaceOverride, "namespace is null");
            return this.addPermutation(Identifier.fromNamespaceAndPath(this.namespaceOverride, asset.suffix()));
        }

        public TrimPatternBuilder addPermutation(Identifier base, Map<ResourceKey<EquipmentAsset>, Identifier> overrides) {
            overrides.values().forEach(this::addPermutation);
            return this.addPermutation(base);
        }

        /**
         * @see AtlasProvider#TRIM_PALETTE_VALUES
         */
        public TrimPatternBuilder addPermutation(Identifier asset) {
            this.permutations.put(asset.getPath(), asset.withPrefix("trims/color_palettes/"));
            return this;
        }

        public TrimPatternBuilder addPermutation(String suffix, Identifier palette) {
            this.permutations.put(suffix, palette);
            return this;
        }

        public TrimPatternBuilder addPermutations(Map<String, Identifier> permutations) {
            this.permutations.putAll(permutations);
            return this;
        }

        public TrimPatternBuilder addVanillaPermutations() {
            return this.addPermutations(AtlasProvider.TRIM_PALETTE_VALUES);
        }

        public TrimPatternBuilder setPalette(Identifier palette) {
            Objects.requireNonNull(palette, "palette is null");
            this.palette = palette;
            return this;
        }

        public TrimPatternBuilder setNamespaceOverride(@Nullable String namespaceOverride) {
            this.namespaceOverride = namespaceOverride;
            return this;
        }

        /**
         * @see AtlasProvider#armorTrims()
         */
        public List<SpriteSource> build() {
            Preconditions.checkArgument(!this.patterns.isEmpty(), "patterns is empty");
            Preconditions.checkArgument(!this.layers.isEmpty(), "layers is empty");
            Preconditions.checkArgument(!this.permutations.isEmpty(), "permutations is empty");
            List<Identifier> textures = patternTextures(this.patterns, this.layers);
            return List.of(new PalettedPermutations(textures, this.palette, this.permutations));
        }

        /**
         * @see AtlasProvider#patternTextures()
         */
        private static List<Identifier> patternTextures(List<ResourceKey<TrimPattern>> patterns, List<EquipmentClientInfo.LayerType> layers) {
            List<Identifier> result = new ArrayList<>(patterns.size() * layers.size());
            for (ResourceKey<TrimPattern> vanillaPattern : patterns) {
                Identifier assetId = TrimPatterns.defaultAssetId(vanillaPattern);
                for (EquipmentClientInfo.LayerType humanoidLayer : layers) {
                    result.add(assetId.withPath(patternPath -> humanoidLayer.trimAssetPrefix() + "/" + patternPath));
                }
            }

            return result;
        }
    }
}
