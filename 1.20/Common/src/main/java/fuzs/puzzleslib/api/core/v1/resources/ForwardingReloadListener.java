package fuzs.puzzleslib.api.core.v1.resources;

import com.google.common.collect.Sets;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
//        if (true) return preparationBarrier.wait(Unit.INSTANCE).thenCompose($ -> CompletableFuture.completedFuture(null));
//        if (true) return preparationBarrier.wait(null);
//        return CompletableFuture.allOf(this.getReloadListeners().stream().map(reloadListener -> {
//            try {
//                return reloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
//            } catch (Exception e) {
//                PuzzlesLib.LOGGER.error("Unable to reload listener {}", reloadListener.getName(), e);
//            }
//            return CompletableFuture.completedFuture(null);
//        }).toArray(CompletableFuture[]::new));

        Set<PreparableReloadListener> handled = Sets.newIdentityHashSet();

        return CompletableFuture.completedFuture(null).thenCompose($ -> {
            Collection<PreparableReloadListener> reloadListeners = this.getReloadListeners();
            PuzzlesLib.LOGGER.info("Reloading {} listeners during preparation stage: {}", reloadListeners.size(), reloadListeners);
            System.out.printf("Reloading %s listeners during preparation stage: %s%n", reloadListeners.size(), reloadListeners);
            if (true) throw new RuntimeException();
            handled.addAll(reloadListeners);
            return reloadListeners.isEmpty() ? preparationBarrier.wait(null) : CompletableFuture.allOf(reloadListeners.stream().map(reloadListener -> {
                try {
                    return reloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
                } catch (Exception e) {
                    PuzzlesLib.LOGGER.error("Unable to reload listener {}", reloadListener.getName(), e);
                }
                return CompletableFuture.completedFuture(null).thenCompose(preparationBarrier::wait);
            }).toArray(CompletableFuture[]::new));
        }).thenCompose(preparationBarrier::wait).thenComposeAsync($ -> {
            Collection<PreparableReloadListener> reloadListeners = this.getReloadListeners();
            PuzzlesLib.LOGGER.info("Reloading {} listeners during reload stage: {}", reloadListeners.size(), reloadListeners);
            Objects.checkIndex(0, reloadListeners.size());
            return CompletableFuture.allOf(reloadListeners.stream().filter(Predicate.not(handled::contains)).map(reloadListener -> {
                try {
                    return reloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
                } catch (Exception e) {
                    PuzzlesLib.LOGGER.error("Unable to reload listener {}", reloadListener.getName(), e);
                }
                return CompletableFuture.completedFuture(null);
            }).toArray(CompletableFuture[]::new));
        }, gameExecutor);

//        return CompletableFuture.runAsync(() -> {
//            Collection<PreparableReloadListener> reloadListeners = this.getReloadListeners();
//            Objects.checkIndex(0, reloadListeners.size());
//            CompletableFuture.allOf(reloadListeners.stream().map(reloadListener -> {
//                try {
//                    return reloadListener.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
//                } catch (Exception e) {
//                    PuzzlesLib.LOGGER.error("Unable to reload listener {}", reloadListener.getName(), e);
//                }
//                return CompletableFuture.completedFuture(null);
//            }).toArray(CompletableFuture[]::new));
//        });
    }
}
