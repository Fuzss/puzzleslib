package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.event.v1.DataPackFinderRegistry;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;

public final class DataPackSourcesContextFabricImpl implements PackRepositorySourcesContext {

    @Override
    public void addRepositorySources(RepositorySource repositorySource, RepositorySource... repositorySources) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        DataPackFinderRegistry.INSTANCE.register(repositorySource);
        Objects.requireNonNull(repositorySources, "repository sources is null");
        for (RepositorySource source : repositorySources) {
            Objects.requireNonNull(source, "repository source is null");
            DataPackFinderRegistry.INSTANCE.register(source);
        }
    }
}
