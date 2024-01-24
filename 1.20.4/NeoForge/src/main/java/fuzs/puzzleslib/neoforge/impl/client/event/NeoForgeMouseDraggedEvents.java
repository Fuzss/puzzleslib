package fuzs.puzzleslib.neoforge.impl.client.event;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class NeoForgeMouseDraggedEvents {

    private NeoForgeMouseDraggedEvents() {

    }

    public static class Pre extends ScreenEvent.MouseDragged implements ICancellableEvent {

        public Pre(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
            super(screen, mouseX, mouseY, mouseButton, dragX, dragY);
        }
    }

    public static class Post extends ScreenEvent.MouseDragged {

        public Post(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
            super(screen, mouseX, mouseY, mouseButton, dragX, dragY);
        }
    }
}
