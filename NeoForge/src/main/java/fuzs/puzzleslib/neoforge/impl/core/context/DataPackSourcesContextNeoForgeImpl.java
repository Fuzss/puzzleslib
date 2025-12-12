package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
    public void registerBuiltInPack(Identifier identifier, Component displayName, boolean shouldAddAutomatically) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(displayName, "display name is null");
        registerBuiltInPack(this.event, identifier, displayName, shouldAddAutomatically, PackType.SERVER_DATA);
    }

    public static void registerBuiltInPack(AddPackFindersEvent event, Identifier identifier, Component displayName, boolean shouldAddAutomatically, PackType packType) {
        event.addPackFinders(PackResourcesHelper.getBuiltInPack(identifier, packType),
                packType,
                displayName,
                shouldAddAutomatically ? PackSource.BUILT_IN : PackSource.FEATURE,
                false,
                Pack.Position.TOP);
    }
}
