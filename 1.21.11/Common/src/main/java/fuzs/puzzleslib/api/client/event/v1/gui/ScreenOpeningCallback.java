package fuzs.puzzleslib.api.client.event.v1.gui;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ScreenOpeningCallback {
    EventInvoker<ScreenOpeningCallback> EVENT = EventInvoker.lookup(ScreenOpeningCallback.class);

    /**
     * Called just before a new screen is set to {@link net.minecraft.client.Minecraft#screen} in
     * {@link net.minecraft.client.Minecraft#setScreen}, allows for exchanging the new screen with a different one, or
     * can prevent a new screen from opening, by returning the original screen (which will be initialised once again).
     *
     * @param oldScreen the screen that is being removed, which may be {@code null} when opening the screen from
     *                  {@link net.minecraft.client.gui.Gui}, like {@link net.minecraft.client.gui.screens.PauseScreen}
     * @param newScreen the new screen that is being set, which may be {@code null} when closing a screen and returning
     *                  to the in-game hud
     * @return <ul>
     *         <li>{@link EventResultHolder#interrupt(Object)} to set a different new screen, potentially the original screen for no change (except the original screen being initialised again), or {@code null} when closing a screen and returning to the in-game gui</li>
     *         <li>{@link EventResultHolder#pass()} to allow the vanilla screen to be set</li>
     *         </ul>
     */
    EventResultHolder<@Nullable Screen> onScreenOpening(@Nullable Screen oldScreen, @Nullable Screen newScreen);
}
