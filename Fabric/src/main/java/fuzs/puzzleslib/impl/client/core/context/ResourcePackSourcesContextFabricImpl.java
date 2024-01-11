package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.client.event.v1.ResourcePackFinderRegistry;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;

public final class ResourcePackSourcesContextFabricImpl implements PackRepositorySourcesContext {

    @Override
    public void addRepositorySource(RepositorySource... repositorySources) {
        Objects.requireNonNull(repositorySources, "repository sources is null");
        Preconditions.checkPositionIndex(1, repositorySources.length, "repository sources is empty");
        for (RepositorySource repositorySource : repositorySources) {
            Objects.requireNonNull(repositorySource, "repository source is null");
            ResourcePackFinderRegistry.INSTANCE.register(repositorySource);
        }
    }
}
