package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import fuzs.puzzleslib.fabric.api.event.v1.registry.DataPackFinderRegistry;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;

public final class DataPackSourcesContextFabricImpl implements PackRepositorySourcesContext {

    @Override
    public void registerRepositorySource(RepositorySource repositorySource) {
        Objects.requireNonNull(repositorySource, "repository source is null");
        DataPackFinderRegistry.INSTANCE.register(repositorySource);
    }

    @Override
    public void registerBuiltInPack(ResourceLocation resourceLocation, Component displayName, boolean shouldAddAutomatically) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(displayName, "display name is null");
        registerBuiltInPack(resourceLocation, displayName, shouldAddAutomatically, PackType.SERVER_DATA);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void registerBuiltInPack(ResourceLocation resourceLocation, Component displayName, boolean shouldAddAutomatically, PackType packType) {
        ModContainer modContainer = FabricLoader.getInstance()
                .getModContainer(resourceLocation.getNamespace())
                .orElseThrow();
        ResourceManagerHelperImpl.registerBuiltinResourcePack(resourceLocation,
                PackResourcesHelper.getBuiltInPack(resourceLocation, packType).getPath(),
                modContainer,
                displayName,
                shouldAddAutomatically ? ResourcePackActivationType.DEFAULT_ENABLED :
                        ResourcePackActivationType.NORMAL);
    }
}
