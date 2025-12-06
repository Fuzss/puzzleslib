package fuzs.puzzleslib.api.client.data.v2;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractParticleProvider implements DataProvider {
    public static final Codec<ParticleDescription> CODEC = ResourceLocation.CODEC.listOf()
            .fieldOf("textures")
            .xmap(ParticleDescription::new, ParticleDescription::getTextures)
            .codec();

    private final Map<ResourceLocation, ParticleDescription> values = new LinkedHashMap<>();
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

    /**
     * Copied from Minecraft 1.21.10.
     */
    static <T> CompletableFuture<?> saveAll(CachedOutput output, Codec<T> codec, PackOutput.PathProvider pathProvider, Map<ResourceLocation, T> entries) {
        return saveAll(output, codec, pathProvider::json, entries);
    }

    /**
     * Copied from Minecraft 1.21.10.
     */
    static <T, E> CompletableFuture<?> saveAll(CachedOutput output, Codec<E> codec, Function<T, Path> pathGetter, Map<T, E> entries) {
        return saveAll(output, object -> codec.encodeStart(JsonOps.INSTANCE, object).getOrThrow(), pathGetter, entries);
    }

    /**
     * Copied from Minecraft 1.21.10.
     */
    static <T, E> CompletableFuture<?> saveAll(CachedOutput output, Function<E, JsonElement> serializer, Function<T, Path> pathGetter, Map<T, E> entries) {
        return CompletableFuture.allOf(entries.entrySet().stream().map(entry -> {
            Path path = pathGetter.apply(entry.getKey());
            JsonElement jsonElement = serializer.apply(entry.getValue());
            return DataProvider.saveStable(output, jsonElement, path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        this.addParticles();
        return saveAll(cachedOutput, CODEC, this.pathProvider, this.values);
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

    public void add(ParticleType<?> particleType, ResourceLocation resourceLocation, int indexEnd) {
        this.add(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), resourceLocation, indexEnd);
    }

    public void add(ParticleType<?> particleType, ResourceLocation resourceLocation, int indexStart, int indexEnd) {
        this.add(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType), resourceLocation, indexStart, indexEnd);
    }

    public void add(ResourceLocation id, ResourceLocation resourceLocation, int indexEnd) {
        this.add(id, resourceLocation, 0, indexEnd);
    }

    public void add(ResourceLocation id, ResourceLocation resourceLocation, int indexStart, int indexEnd) {
        if (indexEnd == -1) {
            this.add(id, new ParticleDescription(Collections.singletonList(resourceLocation)));
        } else {
            List<ResourceLocation> textures = IntStream.rangeClosed(Math.min(indexStart, indexEnd),
                            Math.max(indexStart, indexEnd))
                    .mapToObj((int index) -> ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(),
                            resourceLocation.getPath() + "_" + index))
                    .collect(Collectors.toList());
            if (indexEnd < indexStart) Collections.reverse(textures);
            this.add(id, new ParticleDescription(textures));
        }
    }

    public void add(ResourceLocation id, ParticleDescription particleDescription) {
        if (this.clientResourceManager != null) {
            this.validate(id, particleDescription, this.clientResourceManager);
        }
        if (this.values.putIfAbsent(id, particleDescription) != null) {
            throw new IllegalStateException("Duplicate particle description: " + id);
        }
    }

    protected void validate(ResourceLocation id, ParticleDescription particleDescription, ResourceManager resourceManager) {
        Objects.requireNonNull(resourceManager, "resource manager is null");
        List<String> missingTextures = particleDescription.getTextures()
                .stream()
                .filter((ResourceLocation resourceLocation) -> {
                    return resourceManager.getResource(resourceLocation.withPath((String string) -> "textures/particle/"
                            + string + ".png")).isEmpty();
                })
                .map(ResourceLocation::toString)
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
