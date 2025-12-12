package fuzs.puzzleslib.api.client.data.v2;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractParticleProvider implements DataProvider {
    public static final Codec<ParticleDescription> CODEC = Identifier.CODEC.listOf()
            .fieldOf("textures")
            .xmap(ParticleDescription::new, ParticleDescription::getTextures)
            .codec();

    private final Map<Identifier, ParticleDescription> values = new LinkedHashMap<>();
    private final PackOutput.PathProvider pathProvider;
    @Nullable
    private final ResourceManager clientResourceManager;

    public AbstractParticleProvider(DataProviderContext context) {
        this(context.getPackOutput(), context.getClientResourceManager());
    }

    public AbstractParticleProvider(PackOutput packOutput, @Nullable ResourceManager clientResourceManager) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "particles");
        this.clientResourceManager = clientResourceManager;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        this.addParticles();
        return DataProvider.saveAll(cachedOutput, CODEC, this.pathProvider, this.values);
    }

    public abstract void addParticles();

    public void add(ParticleType<?> particleType) {
        this.add(particleType, -1);
    }

    public void add(ParticleType<?> particleType, int indexEnd) {
        this.add(particleType, BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), indexEnd);
    }

    public void add(ParticleType<?> particleType, int indexStart, int indexEnd) {
        this.add(particleType, BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), indexStart, indexEnd);
    }

    public void add(ParticleType<?> particleType, Identifier identifier, int indexEnd) {
        this.add(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), identifier, indexEnd);
    }

    public void add(ParticleType<?> particleType, Identifier identifier, int indexStart, int indexEnd) {
        this.add(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), identifier, indexStart, indexEnd);
    }

    public void add(Identifier id, Identifier identifier, int indexEnd) {
        this.add(id, identifier, 0, indexEnd);
    }

    public void add(Identifier id, Identifier identifier, int indexStart, int indexEnd) {
        if (indexEnd == -1) {
            this.add(id, new ParticleDescription(Collections.singletonList(identifier)));
        } else {
            List<Identifier> textures = IntStream.rangeClosed(Math.min(indexStart, indexEnd),
                            Math.max(indexStart, indexEnd))
                    .mapToObj((int index) -> Identifier.fromNamespaceAndPath(identifier.getNamespace(),
                            identifier.getPath() + "_" + index))
                    .collect(Collectors.toList());
            if (indexEnd < indexStart) {
                Collections.reverse(textures);
            }

            this.add(id, new ParticleDescription(textures));
        }
    }

    public void add(Identifier id, ParticleDescription particleDescription) {
        if (this.clientResourceManager != null) {
            this.validate(id, particleDescription, this.clientResourceManager);
        }

        if (this.values.putIfAbsent(id, particleDescription) != null) {
            throw new IllegalStateException("Duplicate particle description: " + id);
        }
    }

    protected void validate(Identifier id, ParticleDescription particleDescription, ResourceManager resourceManager) {
        Objects.requireNonNull(resourceManager, "resource manager is null");
        List<String> missingTextures = particleDescription.getTextures()
                .stream()
                .filter((Identifier identifier) -> {
                    return resourceManager.getResource(identifier.withPath((String string) -> "textures/particle/"
                            + string + ".png")).isEmpty();
                })
                .map(Identifier::toString)
                .toList();
        if (!missingTextures.isEmpty()) {
            throw new IllegalArgumentException(
                    "Couldn't define particle description %s as it is missing following texture(s): %s".formatted(id,
                            String.join(",", missingTextures)));
        }
    }

    @Override
    public String getName() {
        return "Particle Descriptions";
    }
}
