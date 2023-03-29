package fuzs.puzzleslib.api.client.event.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

/**
 * Callbacks injected in the render method of all container screens to allow for rendering on background and foreground layers.
 */
public final class ContainerScreenEvents {
    public static final EventInvoker<Background> BACKGROUND = EventInvoker.lookup(Background.class);
    public static final EventInvoker<Foreground> FOREGROUND = EventInvoker.lookup(Foreground.class);

    private ContainerScreenEvents() {

    }

    @FunctionalInterface
    public interface Background {

        /**
         * Called after the screen background is drawn (like menu texture).
         * <p>This event is simply for notifying the foreground has been drawn, nothing can be cancelled.
         *
         * @param screen    the screen being drawn
         * @param poseStack pose stack
         * @param mouseX    mouse x position
         * @param mouseY    mouse y position
         */
        void onDrawBackground(AbstractContainerScreen<?> screen, PoseStack poseStack, int mouseX, int mouseY);
    }

    @FunctionalInterface
    public interface Foreground {

        /**
         * Called after the screen foreground is drawn (like text labels).
         * <p>This event is simply for notifying the foreground has been drawn, nothing can be cancelled.
         *
         * @param screen    the screen being drawn
         * @param poseStack pose stack
         * @param mouseX    mouse x position
         * @param mouseY    mouse y position
         */
        void onDrawForeground(AbstractContainerScreen<?> screen, PoseStack poseStack, int mouseX, int mouseY);
    }
}
