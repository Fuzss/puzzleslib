package fuzs.puzzleslib.neoforge.api.client.data.v2;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractParticleProviderV2 extends JsonCodecProvider<ParticleDescription> {
    public static final Codec<ParticleDescription> CODEC = ResourceLocation.CODEC.listOf()
            .fieldOf("textures")
            .xmap(ParticleDescription::new, ParticleDescription::getTextures)
            .codec();

    private final ResourceManager clientResourceManager;


    public AbstractParticleProviderV2(NeoForgeDataProviderContext context) {
        this(context.getModId(), context.getPackOutput(), context.getRegistries(), context.getClientResourceManager());
    }

    public AbstractParticleProviderV2(String modId, PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ResourceManager clientResourceManager) {
        super(packOutput, PackOutput.Target.RESOURCE_PACK, "particles", CODEC, lookupProvider, modId);
        this.clientResourceManager = clientResourceManager;
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
            return this.clientResourceManager.getResource(resourceLocation.withPath((String string) ->
                    "textures/particle/" + string + ".png")).isEmpty();
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
