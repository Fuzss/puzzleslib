package fuzs.puzzleslib.neoforge.impl.client.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;
import java.util.function.Consumer;

public record ResourcePackSourcesContextNeoForgeImpl(
        Consumer<RepositorySource> consumer) implements PackRepositorySourcesContext {

    @Override
    public void addRepositorySource(RepositorySource... repositorySources) {
        Objects.requireNonNull(repositorySources, "repository sources is null");
        Preconditions.checkState(repositorySources.length > 0, "repository sources is empty");
        for (RepositorySource repositorySource : repositorySources) {
            Objects.requireNonNull(repositorySource, "repository source is null");
            this.consumer.accept(repositorySource);
        }
    }
}
