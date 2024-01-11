package fuzs.puzzleslib.api.data.v1;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractParticleDescriptionProvider extends JsonCodecProvider<List<ResourceLocation>> {
    private static final Codec<List<ResourceLocation>> CODEC = ResourceLocation.CODEC.listOf().fieldOf("textures").codec();

    private final Map<ResourceLocation, List<ResourceLocation>> entries;
    private final ExistingFileHelper.ResourceType resourceType;

    public AbstractParticleDescriptionProvider(GatherDataEvent evt, String modId) {
        this(evt.getGenerator(), evt.getExistingFileHelper(), modId);
    }

    public AbstractParticleDescriptionProvider(DataGenerator packOutput, ExistingFileHelper fileHelper, String modId) {
        this(packOutput, fileHelper, modId, Maps.newHashMap());
    }

    private AbstractParticleDescriptionProvider(DataGenerator packOutput, ExistingFileHelper fileHelper, String modId, Map<ResourceLocation, List<ResourceLocation>> entries) {
        super(packOutput, fileHelper, modId, JsonOps.INSTANCE, PackType.CLIENT_RESOURCES, "particles", CODEC, entries);
        this.entries = entries;
        this.resourceType = new ExistingFileHelper.ResourceType(this.packType, ".json", this.directory);
    }

    @Override
    protected final void gather(BiConsumer<ResourceLocation, List<ResourceLocation>> consumer) {
        this.addParticleDescriptions();
        super.gather(consumer);
    }

    protected abstract void addParticleDescriptions();

    protected void add(ParticleType<?> particleType) {
        this.add(particleType, -1);
    }

    protected void add(ParticleType<?> particleType, int indexEnd) {
        this.add(particleType, ForgeRegistries.PARTICLE_TYPES.getKey(particleType), indexEnd);
    }

    protected void add(ParticleType<?> particleType, int indexStart, int indexEnd) {
        this.add(particleType, ForgeRegistries.PARTICLE_TYPES.getKey(particleType), indexStart, indexEnd);
    }

    protected void add(ParticleType<?> particleType, ResourceLocation resourceLocation, int indexEnd) {
        this.add(ForgeRegistries.PARTICLE_TYPES.getKey(particleType), resourceLocation, indexEnd);
    }

    protected void add(ParticleType<?> particleType, ResourceLocation resourceLocation, int indexStart, int indexEnd) {
        this.add(ForgeRegistries.PARTICLE_TYPES.getKey(particleType), resourceLocation, indexStart, indexEnd);
    }

    protected void add(ResourceLocation id, ResourceLocation resourceLocation, int indexEnd) {
        this.add(id, resourceLocation, 0, indexEnd);
    }

    protected void add(ResourceLocation id, ResourceLocation resourceLocation, int indexStart, int indexEnd) {
        if (indexEnd == -1) {
            this.add(id, List.of(resourceLocation));
        } else {
            List<ResourceLocation> textures = IntStream.rangeClosed(Math.min(indexStart, indexEnd), Math.max(indexStart, indexEnd)).mapToObj(t -> new ResourceLocation(resourceLocation.getNamespace(), resourceLocation.getPath() + "_" + t)).collect(Collectors.toList());
            if (indexEnd < indexStart) Collections.reverse(textures);
            this.add(id, textures);
        }
    }

    private void add(ResourceLocation id, List<ResourceLocation> textures) {
        this.existingFileHelper.trackGenerated(id, this.resourceType);
        if (this.entries.put(id, textures) != null) {
            throw new IllegalStateException("Textures for " + id + " already registered!");
        }
    }

    @Override
    public String getName() {
        return "Particle Descriptions";
    }
}
