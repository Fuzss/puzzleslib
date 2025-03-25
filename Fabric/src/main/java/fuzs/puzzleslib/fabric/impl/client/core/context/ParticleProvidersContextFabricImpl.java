package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ParticleProvidersContext;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.Objects;

public final class ParticleProvidersContextFabricImpl implements ParticleProvidersContext {

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider<T> particleProvider) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleProvider, "particle provider is null");
        ParticleFactoryRegistry.getInstance().register(particleType, particleProvider);
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleProvider.Sprite<T> particleProvider) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleProvider, "particle provider is null");
        this.registerParticleProvider(particleType, (SpriteSet spriteSet) -> {
            return (T particleOptions, ClientLevel clientLevel, double x, double y, double z, double xd, double yd, double zd) -> {
                TextureSheetParticle textureSheetParticle = particleProvider.createParticle(particleOptions,
                        clientLevel,
                        x,
                        y,
                        z,
                        xd,
                        yd,
                        zd);
                if (textureSheetParticle != null) textureSheetParticle.pickSprite(spriteSet);
                return textureSheetParticle;
            };
        });
    }

    @Override
    public <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> particleType, ParticleEngine.SpriteParticleRegistration<T> particleFactory) {
        Objects.requireNonNull(particleType, "particle type is null");
        Objects.requireNonNull(particleFactory, "particle provider factory is null");
        ParticleFactoryRegistry.getInstance().register(particleType, particleFactory::create);
    }
}
