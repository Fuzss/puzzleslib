package fuzs.puzzleslib.neoforge.api.data.v2.client;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractParticleDescriptionProvider extends JsonCodecProvider<List<ResourceLocation>> {
    private static final Codec<List<ResourceLocation>> CODEC = ResourceLocation.CODEC.listOf()
            .fieldOf("textures")
            .codec();

    private final ExistingFileHelper.ResourceType textureResourceType;

    public AbstractParticleDescriptionProvider(NeoForgeDataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries(), context.getFileHelper());
    }

    public AbstractParticleDescriptionProvider(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
        super(packOutput,
                PackOutput.Target.RESOURCE_PACK,
                "particles",
                PackType.CLIENT_RESOURCES,
                CODEC,
                lookupProvider,
                modId,
                fileHelper
        );
        this.textureResourceType = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES,
                ".png",
                "textures/particle"
        );
    }

    @Override
    protected final void gather() {
        this.addParticleDescriptions();
    }

    public abstract void addParticleDescriptions();

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
            this.unconditional(id, List.of(resourceLocation));
        } else {
            List<ResourceLocation> textures = IntStream.rangeClosed(Math.min(indexStart, indexEnd),
                            Math.max(indexStart, indexEnd)
                    )
                    .mapToObj(index -> ResourceLocationHelper.fromNamespaceAndPath(resourceLocation.getNamespace(),
                            resourceLocation.getPath() + "_" + index
                    ))
                    .collect(Collectors.toList());
            if (indexEnd < indexStart) Collections.reverse(textures);
            this.unconditional(id, textures);
        }
    }

    @Override
    public void unconditional(ResourceLocation id, List<ResourceLocation> value) {
        List<String> missing = value.stream()
                .filter(resourceLocation -> !this.existingFileHelper.exists(resourceLocation, this.textureResourceType))
                .map(ResourceLocation::toString)
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Couldn't define particle description %s as it is missing following texture(s): %s".formatted(id,
                            String.join(",", missing)
                    ));
        }

        super.unconditional(id, value);
    }

    @Override
    public String getName() {
        return "Particle Descriptions";
    }
}
