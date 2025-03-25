package fuzs.puzzleslib.neoforge.api.client.data.v2;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Deprecated(forRemoval = true)
public abstract class AbstractParticleProvider extends JsonCodecProvider<ParticleDescription> {
    public static final Codec<ParticleDescription> CODEC = ResourceLocation.CODEC.listOf()
            .fieldOf("textures")
            .xmap(ParticleDescription::new, ParticleDescription::getTextures)
            .codec();

    @Nullable
    private CloseableResourceManager resourceManager;


    public AbstractParticleProvider(DataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries());
    }

    public AbstractParticleProvider(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, PackOutput.Target.RESOURCE_PACK, "particles", CODEC, lookupProvider, modId);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return CompletableFuture.supplyAsync(() -> {
            return this.resourceManager = ExistingFilesHelper.createResourceManager(this.modid);
        }).thenComposeAsync((CloseableResourceManager resourceManager) -> {
            return super.run(output).thenRun(() -> {
                resourceManager.close();
                this.resourceManager = null;
            });
        });
    }

    @Override
    protected final void gather() {
        this.addParticles();
    }

    public abstract void addParticles();

    protected void add(ParticleType<?> particleType) {
        this.add(particleType, -1);
    }

    protected void add(ParticleType<?> particleType, int indexEnd) {
        this.add(particleType, BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), indexEnd);
    }

    protected void add(ParticleType<?> particleType, int indexStart, int indexEnd) {
        this.add(particleType, BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), indexStart, indexEnd);
    }

    protected void add(ParticleType<?> particleType, ResourceLocation resourceLocation, int indexEnd) {
        this.add(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), resourceLocation, indexEnd);
    }

    protected void add(ParticleType<?> particleType, ResourceLocation resourceLocation, int indexStart, int indexEnd) {
        this.add(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), resourceLocation, indexStart, indexEnd);
    }

    protected void add(ResourceLocation id, ResourceLocation resourceLocation, int indexEnd) {
        this.add(id, resourceLocation, 0, indexEnd);
    }

    protected void add(ResourceLocation id, ResourceLocation resourceLocation, int indexStart, int indexEnd) {
        if (indexEnd == -1) {
            this.unconditional(id, new ParticleDescription(Collections.singletonList(resourceLocation)));
        } else {
            List<ResourceLocation> textures = IntStream.rangeClosed(Math.min(indexStart, indexEnd),
                            Math.max(indexStart, indexEnd))
                    .mapToObj(index -> ResourceLocationHelper.fromNamespaceAndPath(resourceLocation.getNamespace(),
                            resourceLocation.getPath() + "_" + index))
                    .collect(Collectors.toList());
            if (indexEnd < indexStart) Collections.reverse(textures);
            this.unconditional(id, new ParticleDescription(textures));
        }
    }

    @Override
    public void unconditional(ResourceLocation id, ParticleDescription value) {
        List<String> missing = value.getTextures().stream().filter((ResourceLocation resourceLocation) -> {
            Objects.requireNonNull(this.resourceManager, "resource manager is null");
            return this.resourceManager.getResource(resourceLocation.withPath((String string) -> "textures/particle/" +
                    string + ".png")).isEmpty();
        }).map(ResourceLocation::toString).toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Couldn't define particle description %s as it is missing following texture(s): %s".formatted(id,
                            String.join(",", missing)));
        }
        super.unconditional(id, value);
    }

    @Override
    public String getName() {
        return "Particles";
    }
}
