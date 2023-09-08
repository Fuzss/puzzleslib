package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.AddReloadListenersContext;
import fuzs.puzzleslib.api.core.v1.resources.FabricReloadListenerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Objects;

public record AddReloadListenersContextFabricImpl(PackType packType, String modId) implements AddReloadListenersContext {

    @Override
    public void registerReloadListener(String id, PreparableReloadListener reloadListener) {
        Objects.requireNonNull(id, "reload listener id is null");
        Objects.requireNonNull(reloadListener, "reload listener is null");
        FabricReloadListenerHelper.registerReloadListener(this.packType, this.modId, id, reloadListener);
    }
}
