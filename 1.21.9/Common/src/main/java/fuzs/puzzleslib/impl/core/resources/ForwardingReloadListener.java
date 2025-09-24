package fuzs.puzzleslib.impl.core.resources;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.core.v1.resources.NamedReloadListener;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public class ForwardingReloadListener<T extends PreparableReloadListener> implements NamedReloadListener {
    private final ResourceLocation resourceLocation;
    private final Supplier<Collection<T>> supplier;
    @Nullable
    private Collection<T> reloadListeners;

    public ForwardingReloadListener(ResourceLocation resourceLocation, Supplier<Collection<T>> reloadListenersSupplier) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        Objects.requireNonNull(reloadListenersSupplier, "reload listeners supplier is null");
        this.resourceLocation = resourceLocation;
        this.supplier = reloadListenersSupplier;
    }

    @Override
    public CompletableFuture<Void> reload(SharedState sharedState, Executor backgroundExecutor, PreparationBarrier preparationBarrier, Executor gameExecutor) {
        return CompletableFuture.completedFuture(null).thenCompose((Object o) -> {
            return CompletableFuture.allOf(this.reloadListeners().stream().map(reloadListener -> {
                try {
                    return reloadListener.reload(sharedState, backgroundExecutor, preparationBarrier, gameExecutor);
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
    public ResourceLocation resourceLocation() {
        return this.resourceLocation;
    }

    synchronized final Collection<T> reloadListeners() {
        if (this.reloadListeners == null) {
            Collection<T> collection = this.supplier.get();
            Objects.requireNonNull(collection, "collection is null");
            if (collection.isEmpty()) {
                PuzzlesLib.LOGGER.error("{} is empty", this.resourceLocation);
                // don't throw for now, seems to happen occasionally, want to test if maybe a second reload is triggered allowing this to still work
                return collection;
            } else {
                return this.reloadListeners = ImmutableList.copyOf(collection);
            }
        } else {
            return this.reloadListeners;
        }
    }
}
