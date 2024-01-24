package fuzs.puzzleslib.forge.impl.client.event;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public final class ForgeMouseDraggedEvents {

    private ForgeMouseDraggedEvents() {

    }

    @Cancelable
    public static class Pre extends ScreenEvent.MouseDragged {

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
