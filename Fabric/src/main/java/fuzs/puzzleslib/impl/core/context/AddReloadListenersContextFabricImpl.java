package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.AddReloadListenersContext;
import fuzs.puzzleslib.impl.core.FabricResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Objects;

public record AddReloadListenersContextFabricImpl(PackType packType, String modId) implements AddReloadListenersContext {

    @Override
    public void registerReloadListener(String id, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(id, "reload listener id is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        ResourceManagerHelper.get(this.packType).registerReloadListener(new FabricResourceReloadListener(this.modId, id, reloadListener));
    }
}
