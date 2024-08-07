package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.AddReloadListenersContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.fabric.api.core.v1.resources.FabricReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Objects;

public record AddReloadListenersContextFabricImpl(PackType packType, String modId) implements AddReloadListenersContext {

    @Override
    public void registerReloadListener(String id, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(id, "reload listener id is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        ResourceLocation resourceLocation = ResourceLocationHelper.fromNamespaceAndPath(this.modId, id);
        ResourceManagerHelper.get(this.packType).registerReloadListener(new FabricReloadListener(resourceLocation, reloadListener));
    }
}
