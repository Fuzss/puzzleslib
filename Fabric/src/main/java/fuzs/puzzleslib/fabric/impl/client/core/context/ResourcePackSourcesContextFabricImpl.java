package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.fabric.impl.core.context.DataPackSourcesContextFabricImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;

public final class ResourcePackSourcesContextFabricImpl implements PackRepositorySourcesContext {

    @Override
    public void registerRepositorySource(RepositorySource repositorySource) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        PackRepository packRepository = Minecraft.getInstance().getResourcePackRepository();
        DataPackSourcesContextFabricImpl.getRepositorySources(packRepository).add(repositorySource);
    }

    @Override
    public void registerBuiltInPack(Identifier identifier, Component displayName, boolean shouldAddAutomatically) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(displayName, "display name is null");
        DataPackSourcesContextFabricImpl.registerBuiltInPack(identifier,
                displayName, shouldAddAutomatically,
                PackType.CLIENT_RESOURCES);
    }
}
