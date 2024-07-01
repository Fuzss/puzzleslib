package fuzs.puzzleslib.fabric.api.client.event.v1;

import fuzs.puzzleslib.fabric.impl.client.event.ExtraScreenExtensions;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.gui.screens.Screen;

import java.util.Objects;

public final class ExtraScreenMouseEvents {
    
    private ExtraScreenMouseEvents() {
        
    }

    public static Event<AllowMouseDrag> allowMouseDrag(Screen screen) {
        Objects.requireNonNull(screen, "screen is null");
        return ((ExtraScreenExtensions) screen).puzzleslib$getAllowMouseDragEvent();
    }

    public static Event<BeforeMouseDrag> beforeMouseDrag(Screen screen) {
        Objects.requireNonNull(screen, "screen is null");
        return ((ExtraScreenExtensions) screen).puzzleslib$getBeforeMouseDragEvent();
    }

    public static Event<AfterMouseDrag> afterMouseDrag(Screen screen) {
        Objects.requireNonNull(screen, "screen is null");
        return ((ExtraScreenExtensions) screen).puzzleslib$getAfterMouseDragEvent();
    }

    @FunctionalInterface
    public interface AllowMouseDrag {
        
        /**
         * Checks if the mouse should be allowed to drag in a screen by moving the cursor while a mouse button is held down.
         *
         * @param screen the currently displayed screen
         * @param mouseX mouse x-position
         * @param mouseY mouse y-position
         * @param button mouse button that was clicked
         * @param dragX  how far the cursor has been dragged since last calling this on x
         * @param dragY  how far the cursor has been dragged since last calling this on y
         * @return whether the mouse should be allowed to drag across the screen
         */
        boolean allowMouseDrag(Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY);
    }

    @FunctionalInterface
    public interface BeforeMouseDrag {
        
        /**
         * Called before a mouse is dragged on screen.
         *
         * @param screen the currently displayed screen
         * @param mouseX mouse x-position
         * @param mouseY mouse y-position
         * @param button mouse button that was clicked
         * @param dragX  how far the cursor has been dragged since last calling this on x
         * @param dragY  how far the cursor has been dragged since last calling this on y
         */
        void beforeMouseDrag(Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY);
    }

    @FunctionalInterface
    public interface AfterMouseDrag {
        
        /**
         * Called after a mouse is dragged on screen.
         *
         * @param screen the currently displayed screen
         * @param mouseX mouse x-position
         * @param mouseY mouse y-position
         * @param button mouse button that was clicked
         * @param dragX  how far the cursor has been dragged since last calling this on x
         * @param dragY  how far the cursor has been dragged since last calling this on y
         */
        void afterMouseDrag(Screen screen, double mouseX, double mouseY, int button, double dragX, double dragY);
    }
}
