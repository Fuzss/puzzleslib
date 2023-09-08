package fuzs.puzzleslib.impl.client.particle;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.particle.v1.ClientParticleTypes;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public final class ClientParticleTypesImpl implements ClientParticleTypes {
    public static final ClientParticleTypesImpl INSTANCE = new ClientParticleTypesImpl();

    private final Map<String, ClientParticleTypesManager> clientParticleTypeManagers = Maps.newConcurrentMap();

    @Override
    public @Nullable Particle createParticle(ResourceLocation identifier, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        ClientParticleTypesManager clientParticleTypesManager = this.clientParticleTypeManagers.get(identifier.getNamespace());
        Objects.requireNonNull(clientParticleTypesManager, "client particle types manager is null");
        return clientParticleTypesManager.createParticle(identifier, particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    public ClientParticleTypesManager getParticleTypesManager(String modId) {
        return this.clientParticleTypeManagers.computeIfAbsent(modId, $ -> new ClientParticleTypesManager());
    }
}
