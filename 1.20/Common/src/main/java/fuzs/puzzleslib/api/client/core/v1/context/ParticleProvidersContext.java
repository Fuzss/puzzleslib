package fuzs.puzzleslib.api.client.core.v1.context;

import fuzs.puzzleslib.impl.client.particle.ClientParticleTypesImpl;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;

/**
 * Register a particle provider for a particle type.
 */
public interface ParticleProvidersContext {

    /**
     * Register a client-side factory for a particle type.
     *
     * @param particleType     common particle type
     * @param particleProvider particle factory
     * @param <T>              type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider<T> particleProvider);

    /**
     * Register a client-side factory for a particle type.
     *
     * @param particleType     common particle type
     * @param particleProvider particle factory
     * @param <T>              type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider.Sprite<T> particleProvider);

    /**
     * Register a client-side sprite based factory for a particle type.
     *
     * @param particleType    common particle type
     * @param particleFactory particle factory
     * @param <T>             type of particle
     * @deprecated renamed to {@link #registerParticleProvider(ParticleType, ParticleEngine.SpriteParticleRegistration)}
     */
    @Deprecated(forRemoval = true)
    default <T extends ParticleOptions> void registerParticleFactory(ParticleType<T> particleType, ParticleEngine.SpriteParticleRegistration<T> particleFactory) {
        this.registerParticleProvider(particleType, particleFactory);
    }

    /**
     * Register a client-side sprite based factory for a particle type.
     *
     * @param particleType    common particle type
     * @param particleFactory particle factory
     * @param <T>             type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleEngine.SpriteParticleRegistration<T> particleFactory);

    /**
     * Register a client-side factory for a particle type that has not been registered in common and will not be present on dedicated servers.
     * <p>Intended for client-only mods to allow for custom particles, which would otherwise be unavailable on dedicated servers if registered normally due to registry sync (at least on Forge).
     *
     * @param type     common particle type
     * @param provider particle factory
     * @param <T>      type of particle
     */
    default <T extends ParticleOptions> void registerClientParticleProvider(ResourceLocation identifier, ParticleProvider<T> particleProvider) {
        ClientParticleTypesImpl.INSTANCE.getParticleTypesManager(identifier.getNamespace()).register(identifier, particleProvider);
    }

    /**
     * Register a client-side factory for a particle type that has not been registered in common and will not be present on dedicated servers.
     * <p>Intended for client-only mods to allow for custom particles, which would otherwise be unavailable on dedicated servers if registered normally due to registry sync (at least on Forge).
     *
     * @param particleType     common particle type
     * @param particleProvider particle factory
     * @param <T>              type of particle
     */
    default <T extends ParticleOptions> void registerClientParticleProvider(ResourceLocation identifier, ParticleProvider.Sprite<T> particleProvider) {
        ClientParticleTypesImpl.INSTANCE.getParticleTypesManager(identifier.getNamespace()).register(identifier, particleProvider);
    }

    /**
     * Register a client-side sprite based factory for a particle type that has not been registered in common and will not be present on dedicated servers.
     * <p>Intended for client-only mods to allow for custom particles, which would otherwise be unavailable on dedicated servers if registered normally due to registry sync (at least on Forge).
     *
     * @param type    common particle type
     * @param factory particle factory
     * @param <T>     type of particle
     */
    default <T extends ParticleOptions> void registerClientParticleProvider(ResourceLocation identifier, ParticleEngine.SpriteParticleRegistration<T> particleFactory) {
        ClientParticleTypesImpl.INSTANCE.getParticleTypesManager(identifier.getNamespace()).register(identifier, particleFactory);
    }
}
