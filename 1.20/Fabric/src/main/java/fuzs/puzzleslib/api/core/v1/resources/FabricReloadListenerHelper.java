package fuzs.puzzleslib.api.core.v1.resources;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Collection;

/**
 * A helper class for registering a {@link PreparableReloadListener} on Fabric without the need for it to implement {@link IdentifiableResourceReloadListener}.
 */
public final class FabricReloadListenerHelper {

    private FabricReloadListenerHelper() {

    }

    /**
     * Registers a reload listener to the {@link net.minecraft.server.packs.resources.ResourceManager} of a certain {@link PackType}.
     *
     * @param packType       the resource type the reload listener belongs to
     * @param modId          the mod namespace registering this reload listener
     * @param id             a name for this reload listener
     * @param reloadListener the reload listener
     */
    public static void registerReloadListener(PackType packType, String modId, String id, PreparableReloadListener reloadListener) {
        ResourceManagerHelper.get(packType).registerReloadListener(new FabricReloadListenerImpl(modId, id, reloadListener));
    }

    /**
     * Registers a reload listener to the {@link net.minecraft.server.packs.resources.ResourceManager} of a certain {@link PackType}.
     *
     * @param packType       the resource type the reload listener belongs to
     * @param identifier     a name for this reload listener
     * @param reloadListener the reload listener
     */
    public static void registerReloadListener(PackType packType, ResourceLocation identifier, PreparableReloadListener reloadListener) {
        ResourceManagerHelper.get(packType).registerReloadListener(new FabricReloadListenerImpl(identifier, reloadListener));
    }

    /**
     * Registers a reload listener to the {@link net.minecraft.server.packs.resources.ResourceManager} of a certain {@link PackType}.
     *
     * @param packType        the resource type the reload listener belongs to
     * @param modId           the mod namespace registering this reload listener
     * @param id              a name for this reload listener
     * @param reloadListeners the reload listeners
     */
    public static void registerReloadListener(PackType packType, String modId, String id, Collection<PreparableReloadListener> reloadListeners) {
        ResourceManagerHelper.get(packType).registerReloadListener(new FabricReloadListenerImpl(modId, id, reloadListeners));
    }

    /**
     * Registers a reload listener to the {@link net.minecraft.server.packs.resources.ResourceManager} of a certain {@link PackType}.
     *
     * @param packType       the resource type the reload listener belongs to
     * @param identifier     a name for this reload listener
     * @param reloadListener the reload listeners
     */
    public static void registerReloadListener(PackType packType, ResourceLocation identifier, Collection<PreparableReloadListener> reloadListeners) {
        ResourceManagerHelper.get(packType).registerReloadListener(new FabricReloadListenerImpl(identifier, reloadListeners));
    }

    private static class FabricReloadListenerImpl extends ForwardingReloadListenerImpl implements IdentifiableResourceReloadListener {

        public FabricReloadListenerImpl(ResourceLocation identifier, Collection<PreparableReloadListener> reloadListeners) {
            super(identifier, reloadListeners);
        }

        public FabricReloadListenerImpl(String modId, String id, PreparableReloadListener reloadListener) {
            super(modId, id, reloadListener);
        }

        public FabricReloadListenerImpl(String modId, String id, Collection<PreparableReloadListener> reloadListeners) {
            super(modId, id, reloadListeners);
        }

        public FabricReloadListenerImpl(ResourceLocation identifier, PreparableReloadListener reloadListener) {
            super(identifier, reloadListener);
        }

        @Override
        public ResourceLocation getFabricId() {
            return this.identifier;
        }
    }
}
