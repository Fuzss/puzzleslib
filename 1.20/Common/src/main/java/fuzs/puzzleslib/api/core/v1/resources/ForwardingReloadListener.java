package fuzs.puzzleslib.api.core.v1.resources;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A simple forwarding reload listener implementation.
 * <p>Intended to allow adding an identifier via {@link PreparableReloadListener#getName()} in further implementations.
 */
public interface ForwardingReloadListener extends PreparableReloadListener {

    /**
     * @return the wrapped reload listener
     */
    PreparableReloadListener reloadListener();

    @Override
    default String getName() {
        return this.reloadListener().getName();
    }

    @Override
    default CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return this.reloadListener().reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
    }
}
