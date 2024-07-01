package fuzs.puzzleslib.api.client.init.v1;

import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * {@link BuiltinItemRenderer} that additionally implements {@link ResourceManagerReloadListener} allowing for listening to resource reloads.
 */
public interface ReloadingBuiltInItemRenderer extends BuiltinItemRenderer, ResourceManagerReloadListener {

}
