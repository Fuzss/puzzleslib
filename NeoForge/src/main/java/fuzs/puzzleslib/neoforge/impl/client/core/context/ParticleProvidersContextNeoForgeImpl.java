package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ParticleProvidersContext;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public record ParticleProvidersContextNeoForgeImpl(RegisterParticleProvidersEvent event) implements ParticleProvidersContext {
    private static final Map<ParticleRenderType, Function<ParticleEngine, ParticleGroup<?>>> PARTICLE_GROUP_FACTORIES = new IdentityHashMap<>();

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider<T> particleProvider) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleProvider, "particle provider is null");
        this.event.registerSpecial(particleType, particleProvider);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleResources.SpriteParticleRegistration<T> particleFactory) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleFactory, "particle provider factory is null");
        this.event.registerSpriteSet(particleType, particleFactory);
    }

    @Override
    public void registerParticleRenderType(ParticleRenderType particleRenderType, Function<ParticleEngine, ParticleGroup<?>> particleGroupFactory) {
        Objects.requireNonNull(particleRenderType, "particle render type is null");
        Objects.requireNonNull(particleGroupFactory, "particle group factory is null");
        if (!(ParticleEngine.RENDER_ORDER instanceof ArrayList<ParticleRenderType>)) {
            ParticleEngine.RENDER_ORDER = new ArrayList<>(ParticleEngine.RENDER_ORDER);
        }

        ParticleEngine.RENDER_ORDER.add(particleRenderType);
        PARTICLE_GROUP_FACTORIES.put(particleRenderType, particleGroupFactory);
    }

    @Nullable
    public static ParticleGroup<?> createParticleGroup(ParticleRenderType particleRenderType, ParticleEngine particleEngine) {
        Function<ParticleEngine, ParticleGroup<?>> particleGroupFactory = PARTICLE_GROUP_FACTORIES.get(
                particleRenderType);
        return particleGroupFactory != null ? particleGroupFactory.apply(particleEngine) : null;
    }
}
