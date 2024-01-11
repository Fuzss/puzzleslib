package fuzs.puzzleslib.impl.client.event;

import fuzs.puzzleslib.api.client.event.v1.ResourcePackFinderRegistry;
import fuzs.puzzleslib.impl.event.DataPackFinderRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;

public final class ResourcePackFinderRegistryImpl implements ResourcePackFinderRegistry {

    @Override
    public void register(RepositorySource repositorySource) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        PackRepository resourcePackRepository = Minecraft.getInstance().getResourcePackRepository();
        DataPackFinderRegistryImpl.addRepositorySource(resourcePackRepository, repositorySource);
    }
}
