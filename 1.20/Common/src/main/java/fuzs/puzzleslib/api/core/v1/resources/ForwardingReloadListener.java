package fuzs.puzzleslib.api.core.v1.resources;

import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * A simple forwarding reload listener implementation.
 * <p>Intended to allow adding an identifier via {@link PreparableReloadListener#getName()} in further implementations.
 */
public interface ForwardingReloadListener extends PreparableReloadListener {

    /**
     * @return the wrapped reload listener
     */
    Collection<PreparableReloadListener> getReloadListeners();

    @Override
    default String getName() {
        return this.getReloadListeners().stream().map(PreparableReloadListener::getName).collect(Collectors.joining(", ", "[", "]"));
    }

    @Override
    default CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return this.getReloadListeners().stream().map(reloadListener -> {
            try {
                return reloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            } catch (Exception e) {
                PuzzlesLib.LOGGER.error("Unable to reload listener {}", reloadListener.getName(), e);
            }
            return CompletableFuture.<Void>completedFuture(null);
        }).reduce((o1, o2) -> o1.thenCompose($ -> o2)).orElseGet(() -> CompletableFuture.completedFuture(null));
    }
}
