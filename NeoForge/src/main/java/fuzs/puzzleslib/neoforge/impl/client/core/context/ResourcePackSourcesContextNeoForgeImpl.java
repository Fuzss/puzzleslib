package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.neoforge.impl.core.context.DataPackSourcesContextNeoForgeImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.Objects;

public record ResourcePackSourcesContextNeoForgeImpl(AddPackFindersEvent event) implements PackRepositorySourcesContext {

    @Override
    public void registerRepositorySource(RepositorySource repositorySource) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        this.event.addRepositorySource(repositorySource);
    }

    @Override
    public void registerBuiltInPack(ResourceLocation resourceLocation, Component displayName, boolean isRequired) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(displayName, "display name is null");
        DataPackSourcesContextNeoForgeImpl.registerBuiltInPack(this.event,
                resourceLocation,
                displayName,
                isRequired,
                PackType.CLIENT_RESOURCES);
    }
}
