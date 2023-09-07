package fuzs.puzzleslib.api.core.v1.resources;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.core.v1.resources.ForwardingReloadListener;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Collection;

/**
 * Allows for wrapping a reload listener to be compatible with the Fabric implementation which requires an additional identifier to be provided.
 * <p>Also supports Fabric listener dependencies.
 *
 * @param identifier         the identifier required by Fabric for this listener
 * @param fabricDependencies some possible dependency keys found in {@link net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys}
 * @param reloadListener     the wrapped reload listener
 */
public record FabricReloadListener(ResourceLocation identifier, Collection<ResourceLocation> fabricDependencies,
                                   PreparableReloadListener reloadListener) implements ForwardingReloadListener, IdentifiableResourceReloadListener {

    public FabricReloadListener(String modId, String id, PreparableReloadListener reloadListener, ResourceLocation... dependencies) {
        this(new ResourceLocation(modId, id), reloadListener, dependencies);
    }

    public FabricReloadListener(ResourceLocation identifier, PreparableReloadListener reloadListener, ResourceLocation... dependencies) {
        this(identifier, ImmutableSet.copyOf(dependencies), reloadListener);
    }

    @Override
    public ResourceLocation getFabricId() {
        return this.identifier;
    }

    @Override
    public Collection<ResourceLocation> getFabricDependencies() {
        return this.fabricDependencies;
    }

    @Override
    public String getName() {
        return this.getFabricId().toString();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
