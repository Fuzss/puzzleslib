package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.fabric.api.client.event.v1.registry.ResourcePackFinderRegistry;
import fuzs.puzzleslib.fabric.impl.core.context.DataPackSourcesContextFabricImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;

public final class ResourcePackSourcesContextFabricImpl implements PackRepositorySourcesContext {

    @Override
    public void registerRepositorySource(RepositorySource repositorySource) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        ResourcePackFinderRegistry.INSTANCE.register(repositorySource);
    }

    @Override
    public void registerBuiltInPack(ResourceLocation resourceLocation, Component displayName, boolean isRequired) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(displayName, "display name is null");
        DataPackSourcesContextFabricImpl.registerBuiltInPack(resourceLocation,
                displayName,
                isRequired,
                PackType.CLIENT_RESOURCES);
    }
}
