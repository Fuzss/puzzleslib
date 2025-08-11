package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import java.util.Objects;

public record DataPackSourcesContextNeoForgeImpl(AddPackFindersEvent event) implements PackRepositorySourcesContext {

    @Override
    public void registerRepositorySource(RepositorySource repositorySource) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        this.event.addRepositorySource(repositorySource);
    }

    @Override
    public void registerBuiltInPack(ResourceLocation resourceLocation, Component displayName, boolean isRequired) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(displayName, "display name is null");
        registerBuiltInPack(this.event, resourceLocation, displayName, isRequired, PackType.SERVER_DATA);
    }

    public static void registerBuiltInPack(AddPackFindersEvent event, ResourceLocation resourceLocation, Component displayName, boolean isRequired, PackType packType) {
        event.addPackFinders(PackResourcesHelper.getBuiltInPack(resourceLocation, packType),
                packType,
                displayName,
                isRequired ? PackSource.BUILT_IN : PackSource.FEATURE,
                isRequired,
                Pack.Position.TOP);
    }
}
