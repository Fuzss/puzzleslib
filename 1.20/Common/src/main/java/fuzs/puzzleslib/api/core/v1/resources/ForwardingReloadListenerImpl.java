package fuzs.puzzleslib.api.core.v1.resources;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

/**
 * Allows for providing an identifier for a reload listener by wrapping it in an effort to help with debugging.
 *
 * @param identifier         the identifier for this reload listener
 * @param reloadListener     the wrapped reload listener
 */
public record ForwardingReloadListenerImpl(ResourceLocation identifier,
                                           PreparableReloadListener reloadListener) implements ForwardingReloadListener {

    public ForwardingReloadListenerImpl(String modId, String id, PreparableReloadListener reloadListener) {
        this(new ResourceLocation(modId, id), reloadListener);
    }

    @Override
    public String getName() {
        return this.identifier.toString();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
