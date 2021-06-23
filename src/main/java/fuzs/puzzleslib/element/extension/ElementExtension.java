package fuzs.puzzleslib.element.extension;

import fuzs.puzzleslib.element.EventListener;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

/**
 * abstract template for sided elements complementing a common element
 */
public abstract class ElementExtension<T extends ExtensibleElement<?>> extends EventListener {

    /**
     * common element this belongs to
     */
    public final T parent;

    public ElementExtension(T parent) {

        this.parent = parent;
    }

    @Override
    public final List<EventStorage<? extends Event>> getEventListeners() {

        return this.parent.getEventListeners();
    }

}
