package fuzs.puzzleslib.impl.core.resources;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.core.v1.resources.NamedReloadListener;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public record ForwardingReloadListener(ResourceLocation identifier, Supplier<Collection<PreparableReloadListener>> reloadListeners) implements NamedReloadListener {

    public ForwardingReloadListener(ResourceLocation identifier, Supplier<Collection<PreparableReloadListener>> reloadListeners) {
        this.identifier = identifier;
        this.reloadListeners = Suppliers.memoize(() -> ImmutableList.copyOf(reloadListeners.get()));
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.completedFuture(null).thenCompose($ -> {
            Collection<PreparableReloadListener> reloadListeners = this.reloadListeners.get();
            Objects.checkIndex(0, reloadListeners.size());
            return CompletableFuture.allOf(reloadListeners.stream().map(reloadListener -> {
                try {
                    return reloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
                } catch (Exception e) {
                    PuzzlesLib.LOGGER.error("Unable to reload listener {}", reloadListener.getName(), e);
                }
                return CompletableFuture.completedFuture(null).thenCompose(preparationBarrier::wait);
            }).toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
