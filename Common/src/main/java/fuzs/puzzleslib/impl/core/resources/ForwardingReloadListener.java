package fuzs.puzzleslib.impl.core.resources;

import fuzs.puzzleslib.api.core.v1.resources.NamedReloadListener;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class ForwardingReloadListener<T extends PreparableReloadListener> implements NamedReloadListener {
    private final ResourceLocation identifier;
    private final Supplier<Collection<T>> supplier;
    @Nullable
    private Collection<T> reloadListeners;


    public ForwardingReloadListener(ResourceLocation identifier, Supplier<Collection<T>> supplier) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(supplier, "supplier is null");
        this.identifier = identifier;
        this.supplier = supplier;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.completedFuture(null).thenCompose($ -> {
            return CompletableFuture.allOf(this.reloadListeners().stream().map(reloadListener -> {
                try {
                    return reloadListener.reload(preparationBarrier,
                            resourceManager,
                            preparationsProfiler,
                            reloadProfiler,
                            backgroundExecutor,
                            gameExecutor
                    );
                } catch (Exception exception) {
                    PuzzlesLib.LOGGER.error("Unable to reload listener {}", reloadListener.getName(), exception);
                }
                return CompletableFuture.completedFuture(null).thenCompose(preparationBarrier::wait);
            }).toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public final String toString() {
        return this.getName();
    }

    @Override
    public ResourceLocation identifier() {
        return this.identifier;
    }

    synchronized final Collection<T> reloadListeners() {
        if (this.reloadListeners == null) {
            Collection<T> collection = this.supplier.get();
            Objects.requireNonNull(collection, "collection is null");
            if (collection.isEmpty()) {
                PuzzlesLib.LOGGER.error("{} is empty", this.identifier);
                // don't throw for now, seems to happen occasionally, want to test if maybe a second reload is triggered allowing this to still work
                return collection;
            } else {
                return this.reloadListeners = collection;
            }
        } else {
            return this.reloadListeners;
        }
    }
}
