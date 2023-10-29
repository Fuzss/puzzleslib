package fuzs.puzzleslib.api.core.v1.resources;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A helper class for registering a {@link PreparableReloadListener} on Fabric without the need for it to implement {@link IdentifiableResourceReloadListener}.
 *
 * @param identifier     identifier for this reload listener
 * @param reloadListener the reload listener
 */
public record FabricReloadListener(ResourceLocation identifier,
                                   PreparableReloadListener reloadListener, Collection<ResourceLocation> dependencies) implements NamedReloadListener, IdentifiableResourceReloadListener {

    public FabricReloadListener(ResourceLocation identifier, PreparableReloadListener reloadListener) {
        this(identifier, reloadListener, new ResourceLocation[0]);
    }

    public FabricReloadListener(ResourceLocation identifier, ResourceManagerReloadListener reloadListener) {
        this(identifier, reloadListener, new ResourceLocation[0]);
    }

    public <T> FabricReloadListener(ResourceLocation identifier, SimplePreparableReloadListener<T> reloadListener) {
        this(identifier, reloadListener, new ResourceLocation[0]);
    }

    public FabricReloadListener(NamedReloadListener reloadListener) {
        this(reloadListener, new ResourceLocation[0]);
    }

    public FabricReloadListener(ResourceLocation identifier, PreparableReloadListener reloadListener, ResourceLocation... dependencies) {
        this(identifier, reloadListener, ImmutableSet.copyOf(dependencies));
    }

    public FabricReloadListener(ResourceLocation identifier, ResourceManagerReloadListener reloadListener, ResourceLocation... dependencies) {
        this(identifier, reloadListener, ImmutableSet.copyOf(dependencies));
    }

    public <T> FabricReloadListener(ResourceLocation identifier, SimplePreparableReloadListener<T> reloadListener, ResourceLocation... dependencies) {
        this(identifier, reloadListener, ImmutableSet.copyOf(dependencies));
    }

    public FabricReloadListener(NamedReloadListener reloadListener, ResourceLocation... dependencies) {
        this(reloadListener.identifier(), reloadListener, ImmutableSet.copyOf(dependencies));
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return this.reloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
    }

    @Override
    public ResourceLocation getFabricId() {
        return this.identifier;
    }
}
