package fuzs.puzzleslib.forge.impl.client.event;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;

import java.util.AbstractList;
import java.util.List;

/**
 * A simple utility class that allows getting a list of all {@link AbstractWidget} instances in screen, but as a view of {@link net.minecraft.client.gui.screens.Screen#renderables}.
 * <p>Intended for use during init events. A view is required so that changes from additions and removals of widgets are reflected.
 * <p>This is basically the same as Fabric Api's <code>net.fabricmc.fabric.impl.client.screen.ButtonList</code>,
 * just much simpler as it does not need to handle additions and removals (Forge has dedicated methods in the init events for that).
 */
final class ForgeButtonList extends AbstractList<AbstractWidget> {
    private final List<Renderable> renderables;

    ForgeButtonList(List<Renderable> renderables) {
        this.renderables = renderables;
    }

    @Override
    public int size() {
        return (int) this.renderables.stream().filter(AbstractWidget.class::isInstance).count();
    }

    @Override
    public AbstractWidget get(int index) {
        return this.renderables.stream().filter(AbstractWidget.class::isInstance).skip(index).findFirst().map(AbstractWidget.class::cast).orElseThrow(() -> new IndexOutOfBoundsException(index));
    }
}
