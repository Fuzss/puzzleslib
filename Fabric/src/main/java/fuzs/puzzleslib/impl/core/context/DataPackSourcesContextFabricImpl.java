package fuzs.puzzleslib.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.event.v1.DataPackFinderRegistry;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;

public final class DataPackSourcesContextFabricImpl implements PackRepositorySourcesContext {

    @Override
    public void addRepositorySource(RepositorySource... repositorySources) {
        Objects.requireNonNull(repositorySources, "repository sources is null");
        Preconditions.checkPositionIndex(0, repositorySources.length, "repository sources is empty");
        for (RepositorySource repositorySource : repositorySources) {
            Objects.requireNonNull(repositorySource, "repository source is null");
            DataPackFinderRegistry.INSTANCE.register(repositorySource);
        }
    }
}
