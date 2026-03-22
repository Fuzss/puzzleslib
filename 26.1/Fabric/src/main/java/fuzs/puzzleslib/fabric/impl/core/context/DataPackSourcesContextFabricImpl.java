package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.fabric.impl.resource.ResourceLoaderImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class DataPackSourcesContextFabricImpl implements PackRepositorySourcesContext {
    private static final Set<RepositorySource> REPOSITORY_SOURCES_REGISTRY = new LinkedHashSet<>();

    @Override
    public void registerRepositorySource(RepositorySource repositorySource) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        REPOSITORY_SOURCES_REGISTRY.add(repositorySource);
    }

    @Override
    public void registerBuiltInPack(Identifier identifier, Component displayName, boolean shouldAddAutomatically) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(displayName, "display name is null");
        registerBuiltInPack(identifier, displayName, shouldAddAutomatically, PackType.SERVER_DATA);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void registerBuiltInPack(Identifier identifier, Component displayName, boolean shouldAddAutomatically, PackType packType) {
        ModContainer modContainer = FabricLoader.getInstance().getModContainer(identifier.getNamespace()).orElseThrow();
        ResourceLoaderImpl.registerBuiltinPack(identifier,
                PackResourcesHelper.getBuiltInPack(identifier, packType).getPath(),
                modContainer,
                displayName,
                shouldAddAutomatically ? PackActivationType.DEFAULT_ENABLED : PackActivationType.NORMAL);
    }

    public static void addAll(PackRepository packRepository) {
        Set<RepositorySource> repositorySources = getRepositorySources(packRepository);
        repositorySources.addAll(REPOSITORY_SOURCES_REGISTRY);
    }

    public static Set<RepositorySource> getRepositorySources(PackRepository packRepository) {
        Set<RepositorySource> repositorySources = packRepository.sources;
        // Fabric Api internally replaces the immutable set already and leaves it like that, just verify in case internal implementation changes
        if (!(repositorySources instanceof LinkedHashSet<RepositorySource>)) {
            return packRepository.sources = new LinkedHashSet<>(repositorySources);
        } else {
            return repositorySources;
        }
    }
}
