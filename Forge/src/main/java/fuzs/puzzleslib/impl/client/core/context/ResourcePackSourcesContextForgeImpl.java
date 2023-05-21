package fuzs.puzzleslib.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;
import java.util.function.Consumer;

public record ResourcePackSourcesContextForgeImpl(
        Consumer<RepositorySource> consumer) implements PackRepositorySourcesContext {

    @Override
    public void addRepositorySources(RepositorySource... repositorySources) {
        Objects.requireNonNull(repositorySources, "repository sources is null");
        Preconditions.checkPositionIndex(0, repositorySources.length, "repository sources is empty");
        for (RepositorySource repositorySource : repositorySources) {
            Objects.requireNonNull(repositorySource, "repository source is null");
            this.consumer.accept(repositorySource);
        }
    }
}
