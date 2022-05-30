package fuzs.puzzleslib.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.function.BiFunction;

/**
 * utility class for client sided methods
 */
public class PuzzlesLibClient {

    /**
     * register a mod config screen for in-game configuration
     * @param screenFunction factory for mod config
     */
    public static void setConfigScreenFactory(BiFunction<Minecraft, Screen, Screen> screenFunction) {
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory(screenFunction));
    }
}
