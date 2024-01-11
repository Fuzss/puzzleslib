package fuzs.puzzleslib.fabric.impl.event;

import com.google.common.collect.Sets;
import fuzs.puzzleslib.fabric.api.event.v1.DataPackFinderRegistry;
import fuzs.puzzleslib.fabric.mixin.accessor.PackRepositoryFabricAccessor;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class DataPackFinderRegistryImpl implements DataPackFinderRegistry {
    private static final Set<RepositorySource> SOURCES = Sets.newHashSet();

    public static void addAllRepositorySources(PackRepository packRepository) {
        SOURCES.forEach(repositorySource -> addRepositorySource(packRepository, repositorySource));
    }

    public static synchronized void addRepositorySource(PackRepository packRepository, RepositorySource repositorySource) {
        Set<RepositorySource> repositorySources = ((PackRepositoryFabricAccessor) packRepository).puzzleslib$getSources();
        // Fabric Api internally replaces the immutable set already and leaves it like that, just verify in case internal implementation changes
        if (!(repositorySources instanceof LinkedHashSet<RepositorySource>)) {
            repositorySources = new LinkedHashSet<>(repositorySources);
            ((PackRepositoryFabricAccessor) packRepository).puzzleslib$setSources(repositorySources);
        }
        repositorySources.add(repositorySource);
    }

    @Override
    public void register(RepositorySource repositorySource) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        SOURCES.add(repositorySource);
    }
}
