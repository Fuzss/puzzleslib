package fuzs.puzzleslib.api.core.v1.resources;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Collection;
import java.util.List;

/**
 * Allows for providing an identifier for a reload listener by wrapping it in an effort to help with debugging.
 */
public class ForwardingReloadListenerImpl implements ForwardingReloadListener {
    /**
     * The identifier for this reload listener.
     */
    protected final ResourceLocation identifier;
    /**
     * The wrapped reload listeners.
     */
    private final Collection<PreparableReloadListener> reloadListeners;

    public ForwardingReloadListenerImpl(ResourceLocation identifier, Collection<PreparableReloadListener> reloadListeners) {
        this.identifier = identifier;
        // do not make a copy or check if the collection is empty,
        // we sometimes need to register a reload listener before collection contents are available / added (like for built-in item renderers)
        this.reloadListeners = reloadListeners;
    }

    public ForwardingReloadListenerImpl(String modId, String id, PreparableReloadListener reloadListener) {
        this(new ResourceLocation(modId, id), reloadListener);
    }

    public ForwardingReloadListenerImpl(String modId, String id, Collection<PreparableReloadListener> reloadListeners) {
        this(new ResourceLocation(modId, id), reloadListeners);
    }

    public ForwardingReloadListenerImpl(ResourceLocation identifier, PreparableReloadListener reloadListener) {
        this(identifier, List.of(reloadListener));
    }

    @Override
    public String getName() {
        return this.identifier.toString();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public Collection<PreparableReloadListener> getReloadListeners() {
        return this.reloadListeners;
    }
}
