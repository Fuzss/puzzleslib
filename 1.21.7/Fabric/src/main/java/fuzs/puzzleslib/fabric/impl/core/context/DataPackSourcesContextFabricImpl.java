package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.fabric.api.event.v1.registry.DataPackFinderRegistry;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;

public final class DataPackSourcesContextFabricImpl implements PackRepositorySourcesContext {

    @Override
    public void addRepositorySource(RepositorySource repositorySource) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        DataPackFinderRegistry.INSTANCE.register(repositorySource);
    }
}
