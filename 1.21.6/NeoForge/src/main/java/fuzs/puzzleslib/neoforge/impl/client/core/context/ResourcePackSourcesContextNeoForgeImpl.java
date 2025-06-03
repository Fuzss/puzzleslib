package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.Objects;

public record ResourcePackSourcesContextNeoForgeImpl(AddPackFindersEvent evt) implements PackRepositorySourcesContext {

    @Override
    public void addRepositorySource(RepositorySource repositorySource) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        this.evt.addRepositorySource(repositorySource);
    }
}
