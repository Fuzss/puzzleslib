package fuzs.puzzleslib.api.client.init.v1;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public abstract class ReloadingBuiltInItemRenderer implements DynamicBuiltinItemRenderer, ResourceManagerReloadListener {

    @Override
    public abstract void onResourceManagerReload(ResourceManager resourceManager);
}
