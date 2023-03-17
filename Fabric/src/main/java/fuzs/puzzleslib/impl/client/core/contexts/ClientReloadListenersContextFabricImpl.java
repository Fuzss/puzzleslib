package fuzs.puzzleslib.impl.client.core.contexts;

import fuzs.puzzleslib.api.client.core.v1.contexts.ClientReloadListenersContext;
import fuzs.puzzleslib.impl.client.core.FabricResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Objects;

public record ClientReloadListenersContextFabricImpl(String modId) implements ClientReloadListenersContext {

    @Override
    public void registerReloadListener(String id, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(id, "reload listener id is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricResourceReloadListener(this.modId, id, reloadListener));
    }
}
