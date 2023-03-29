package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public interface ScreenOpeningCallback {
    EventInvoker<ScreenOpeningCallback> EVENT = EventInvoker.lookup(ScreenOpeningCallback.class);

    /**
     * Called just before a new screen is set to {@link net.minecraft.client.Minecraft#screen} in {@link net.minecraft.client.Minecraft#setScreen},
     * allows for exchanging the new screen with a different one, or can prevent a new screen from opening, effectively forcing the old screen to remain.
     * <p>Do not use {@link net.minecraft.client.Minecraft#setScreen} inside of this event callback, there will be an infinite loop!
     *
     * @param oldScreen the screen that is being removed, may be null when opening the screen from {@link net.minecraft.client.gui.Gui}, like {@link net.minecraft.client.gui.screens.PauseScreen}
     * @param newScreen the new screen that is being set, may be null when closing a screen and returning to {@link net.minecraft.client.gui.Gui}, can be changed
     * @return {@link EventResult#INTERRUPT} to prevent the new screen from opening, the old screen will be kept and no call to {@link Screen#removed()} is made,
     * {@link EventResult#PASS} to allow a new screen to be set, potentially changed via <code>newScreen</code>
     */
    EventResult onScreenOpening(@Nullable Screen oldScreen, MutableValue<Screen> newScreen);
}
