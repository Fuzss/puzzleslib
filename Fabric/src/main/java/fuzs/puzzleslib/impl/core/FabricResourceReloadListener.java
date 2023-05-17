package fuzs.puzzleslib.impl.core;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public record FabricResourceReloadListener(ResourceLocation identifier, Collection<ResourceLocation> fabricDependencies,
                                    PreparableReloadListener reloadListener) implements IdentifiableResourceReloadListener {

    public FabricResourceReloadListener(String modId, String id, PreparableReloadListener reloadListener, ResourceLocation... dependencies) {
        this(new ResourceLocation(modId, id), reloadListener, dependencies);
    }

    public FabricResourceReloadListener(ResourceLocation id, PreparableReloadListener reloadListener, ResourceLocation... dependencies) {
        this(id, ImmutableSet.copyOf(dependencies), reloadListener);
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
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
        return this.reloadListener.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
    }
}
