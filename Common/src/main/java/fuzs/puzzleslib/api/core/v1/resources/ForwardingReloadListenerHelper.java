package fuzs.puzzleslib.api.core.v1.resources;

import fuzs.puzzleslib.impl.core.resources.ForwardingReloadListener;
import fuzs.puzzleslib.impl.core.resources.ForwardingResourceManagerReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * A helper class allowing for making {@link PreparableReloadListener} instances identifiable via the addition of a {@link ResourceLocation} in an effort to help with debugging.
 * <p>Also supports combining multiple reload listeners into one.
 */
@Deprecated
@SuppressWarnings("unchecked")
public final class ForwardingReloadListenerHelper {

    private ForwardingReloadListenerHelper() {

    }

    /**
     * Creates a new reload listener wrapping the provided instance.
     * <p>Note that the internal supplier is resolved as late as possible, specifically during the beginning of the preparation stage, as opposed to reload listener construction.
     * <p>This allows for new reload listeners to still be provided via the supplier implementation up to that stage.
     *
     * @param identifier     identifier for the new reload listener
     * @param reloadListener the reload listener to wrap
     * @param <T>            reload listener compound type
     * @return new reload listener instance
     */
    public static <T extends PreparableReloadListener & NamedReloadListener> T fromReloadListener(ResourceLocation identifier, PreparableReloadListener reloadListener) {
        return fromReloadListener(identifier, () -> reloadListener);
    }

    /**
     * Creates a new reload listener wrapping the provided instance.
     * <p>Note that the internal supplier is resolved as late as possible, specifically during the beginning of the preparation stage, as opposed to reload listener construction.
     * <p>This allows for new reload listeners to still be provided via the supplier implementation up to that stage.
     *
     * @param identifier identifier for the new reload listener
     * @param supplier   the reload listener to wrap
     * @param <T>        reload listener compound type
     * @return new reload listener instance
     */
    public static <T extends PreparableReloadListener & NamedReloadListener> T fromReloadListener(ResourceLocation identifier, Supplier<PreparableReloadListener> supplier) {
        return fromReloadListeners(identifier, () -> Collections.singletonList(supplier.get()));
    }

    /**
     * Creates a new reload listener wrapping the provided instance.
     * <p>Note that the internal supplier is resolved as late as possible, specifically during the beginning of the preparation stage, as opposed to reload listener construction.
     * <p>This allows for new reload listeners to still be provided via the supplier implementation up to that stage.
     *
     * @param identifier      identifier for the new reload listener
     * @param reloadListeners the reload listeners to wrap
     * @param <T>             reload listener compound type
     * @return new reload listener instance
     */
    public static <T extends PreparableReloadListener & NamedReloadListener> T fromReloadListeners(ResourceLocation identifier, Collection<PreparableReloadListener> reloadListeners) {
        return fromReloadListeners(identifier, () -> reloadListeners);
    }

    /**
     * Creates a new reload listener wrapping the provided instance.
     * <p>Note that the internal supplier is resolved as late as possible, specifically during the beginning of the preparation stage, as opposed to reload listener construction.
     * <p>This allows for new reload listeners to still be provided via the supplier implementation up to that stage.
     *
     * @param identifier identifier for the new reload listener
     * @param supplier   the reload listeners to wrap
     * @param <T>        reload listener compound type
     * @return new reload listener instance
     */
    public static <T extends PreparableReloadListener & NamedReloadListener> T fromReloadListeners(ResourceLocation identifier, Supplier<Collection<PreparableReloadListener>> supplier) {
        return (T) new ForwardingReloadListener<>(identifier, supplier);
    }

    /**
     * Creates a new reload listener wrapping the provided instance.
     * <p>Note that this implementation is specifically designed for instances of {@link ResourceManagerReloadListener},
     * as the internal supplier is only resolved during the application stage after the preparation stage has concluded,
     * which is when a {@link ResourceManagerReloadListener} first begins execution.
     * <p>This allows for new reload listeners to still be provided via the supplier implementation up to that stage.
     * This is required for some cases on Forge, namely built-in item renderers,
     * which are constructed during sided setup after the resource manager has already begun the initial resource reload.
     *
     * @param identifier     identifier for the new reload listener
     * @param reloadListener the reload listener to wrap
     * @param <T>            reload listener compound type
     * @return new reload listener instance
     */
    public static <T extends ResourceManagerReloadListener & NamedReloadListener> T fromResourceManagerReloadListener(ResourceLocation identifier, ResourceManagerReloadListener reloadListener) {
        return fromResourceManagerReloadListener(identifier, () -> reloadListener);
    }

    /**
     * Creates a new reload listener wrapping the provided instance.
     * <p>Note that this implementation is specifically designed for instances of {@link ResourceManagerReloadListener},
     * as the internal supplier is only resolved during the application stage after the preparation stage has concluded,
     * which is when a {@link ResourceManagerReloadListener} first begins execution.
     * <p>This allows for new reload listeners to still be provided via the supplier implementation up to that stage.
     * This is required for some cases on Forge, namely built-in item renderers,
     * which are constructed during sided setup after the resource manager has already begun the initial resource reload.
     *
     * @param identifier identifier for the new reload listener
     * @param supplier   the reload listener to wrap
     * @param <T>        reload listener compound type
     * @return new reload listener instance
     */
    public static <T extends ResourceManagerReloadListener & NamedReloadListener> T fromResourceManagerReloadListener(ResourceLocation identifier, Supplier<ResourceManagerReloadListener> supplier) {
        return fromResourceManagerReloadListeners(identifier, () -> Collections.singletonList(supplier.get()));
    }

    /**
     * Creates a new reload listener wrapping the provided instance.
     * <p>Note that this implementation is specifically designed for instances of {@link ResourceManagerReloadListener},
     * as the internal supplier is only resolved during the application stage after the preparation stage has concluded,
     * which is when a {@link ResourceManagerReloadListener} first begins execution.
     * <p>This allows for new reload listeners to still be provided via the supplier implementation up to that stage.
     * This is required for some cases on Forge, namely built-in item renderers,
     * which are constructed during sided setup after the resource manager has already begun the initial resource reload.
     *
     * @param identifier      identifier for the new reload listener
     * @param reloadListeners the reload listeners to wrap
     * @param <T>             reload listener compound type
     * @return new reload listener instance
     */
    public static <T extends ResourceManagerReloadListener & NamedReloadListener> T fromResourceManagerReloadListeners(ResourceLocation identifier, Collection<ResourceManagerReloadListener> reloadListeners) {
        return fromResourceManagerReloadListeners(identifier, () -> reloadListeners);
    }

    /**
     * Creates a new reload listener wrapping the provided instance.
     * <p>Note that this implementation is specifically designed for instances of {@link ResourceManagerReloadListener},
     * as the internal supplier is only resolved during the application stage after the preparation stage has concluded,
     * which is when a {@link ResourceManagerReloadListener} first begins execution.
     * <p>This allows for new reload listeners to still be provided via the supplier implementation up to that stage.
     * This is required for some cases on Forge, namely built-in item renderers,
     * which are constructed during sided setup after the resource manager has already begun the initial resource reload.
     *
     * @param identifier identifier for the new reload listener
     * @param supplier   the reload listeners to wrap
     * @param <T>        reload listener compound type
     * @return new reload listener instance
     */
    public static <T extends ResourceManagerReloadListener & NamedReloadListener> T fromResourceManagerReloadListeners(ResourceLocation identifier, Supplier<Collection<ResourceManagerReloadListener>> supplier) {
        return (T) new ForwardingResourceManagerReloadListener(identifier, supplier);
    }
}
