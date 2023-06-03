package fuzs.puzzleslib.impl.client.event;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;

public class ScreenCloseEvent extends ScreenEvent {

    public ScreenCloseEvent(Screen screen) {
        super(screen);
    }
}
