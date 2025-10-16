package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ParticleProvidersContext;
import fuzs.puzzleslib.neoforge.impl.core.context.AbstractNeoForgeContext;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleGroupsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.Objects;
import java.util.function.Function;

public final class ParticleProvidersContextNeoForgeImpl extends AbstractNeoForgeContext implements ParticleProvidersContext {

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider<T> particleProvider) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleProvider, "particle provider is null");
        this.registerForEvent(RegisterParticleProvidersEvent.class, (RegisterParticleProvidersEvent event) -> {
            event.registerSpecial(particleType, particleProvider);
        });
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleResources.SpriteParticleRegistration<T> particleFactory) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleFactory, "particle provider factory is null");
        this.registerForEvent(RegisterParticleProvidersEvent.class, (RegisterParticleProvidersEvent event) -> {
            event.registerSpriteSet(particleType, particleFactory);
        });
    }

    @Override
    public void registerParticleRenderType(ParticleRenderType particleRenderType, Function<ParticleEngine, ParticleGroup<?>> particleGroupFactory) {
        Objects.requireNonNull(particleRenderType, "particle render type is null");
        Objects.requireNonNull(particleGroupFactory, "particle group factory is null");
        this.registerForEvent(RegisterParticleGroupsEvent.class, (RegisterParticleGroupsEvent event) -> {
            event.register(particleRenderType, particleGroupFactory);
        });
    }
}
