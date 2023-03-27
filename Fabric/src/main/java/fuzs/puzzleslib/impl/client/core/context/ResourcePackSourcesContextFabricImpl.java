package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.event.v1.ResourcePackFinderRegistry;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;

public final class ResourcePackSourcesContextFabricImpl implements PackRepositorySourcesContext {

    @Override
    public void addRepositorySources(RepositorySource repositorySource, RepositorySource... repositorySources) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        ResourcePackFinderRegistry.INSTANCE.register(repositorySource);
        Objects.requireNonNull(repositorySources, "repository sources is null");
        for (RepositorySource source : repositorySources) {
            Objects.requireNonNull(source, "repository source is null");
            ResourcePackFinderRegistry.INSTANCE.register(source);
        }
    }
}
