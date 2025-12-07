package fuzs.puzzleslib.impl.client.event;

import fuzs.puzzleslib.api.core.v1.util.TransformingForwardingList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A simple utility class that allows getting a list of all {@link AbstractWidget} instances in screen, but as a view of
 * {@link net.minecraft.client.gui.screens.Screen#renderables}.
 * <p>
 * Intended for use during init events. A view is required so that changes from additions and removals of widgets are
 * reflected.
 * <p>
 * This is basically the same as Fabric Api's <code>net.fabricmc.fabric.impl.client.screen.ButtonList</code>, just much
 * simpler as it does not need to handle additions and removals (Forge has dedicated methods in the init events for
 * that).
 */
public final class ScreenButtonList extends TransformingForwardingList<AbstractWidget, Renderable> {

    public ScreenButtonList(List<Renderable> delegate) {
        super(delegate);
    }

    @Override
    protected @Nullable AbstractWidget getAsElement(@Nullable Renderable element) {
        return element instanceof AbstractWidget abstractWidget ? abstractWidget : null;
    }

    @Override
    protected @Nullable Renderable getAsListElement(@Nullable AbstractWidget abstractWidget) {
        return abstractWidget;
    }
}
