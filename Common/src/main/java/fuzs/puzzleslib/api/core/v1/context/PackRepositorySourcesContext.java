package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.server.packs.repository.RepositorySource;

@FunctionalInterface
public interface PackRepositorySourcesContext {

    /**
     * Register additional {@link RepositorySource}s when a new {@link net.minecraft.server.packs.repository.PackRepository} is created.
     * <p>Context can be used for registering both client (for resource packs) and server (for data packs) repository sources.
     *
     * @param repositorySources repository sources to add
     */
    void addRepositorySource(RepositorySource... repositorySources);
}
