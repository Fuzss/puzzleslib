package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationHandler;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.fabric.impl.client.key.FabricKeyMappingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;

import java.util.Objects;
import java.util.function.Consumer;

public final class KeyMappingsContextFabricImpl implements KeyMappingsContext {

    @Override
    public void registerKeyMapping(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        Objects.requireNonNull(activationHandler, "activation handler is null");
        KeyBindingHelper.registerKeyBinding(keyMapping);
        ((FabricKeyMappingHelper) KeyMappingHelper.INSTANCE).setKeyActivationContext(keyMapping,
                activationHandler.getActivationContext()
        );
        registerKeyActivationHandles(keyMapping, activationHandler);
    }

    private static void registerKeyActivationHandles(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
        if (activationHandler.gameHandler() != null) {
            ClientTickEvents.START_CLIENT_TICK.register((Minecraft minecraft) -> {
                if (minecraft.player != null) {
                    while (keyMapping.consumeClick()) {
                        activationHandler.gameHandler().accept(minecraft);
                    }
                }
            });
        }
        if (activationHandler.screenHandler() != null) {
            ScreenEvents.BEFORE_INIT.register(
                    (Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) -> {
                        if (activationHandler.screenType().isInstance(screen)) {
                            ScreenKeyboardEvents.allowKeyPress(screen).register(
                                    (Screen currentScreen, int key, int scancode, int modifiers) -> {
                                        if (!(minecraft.screen instanceof KeyBindsScreen) && keyMapping.matches(key,
                                                scancode
                                        )) {
                                            ((Consumer<Screen>) activationHandler.screenHandler()).accept(
                                                    currentScreen);
                                            return false;
                                        } else {
                                            return true;
                                        }
                                    });
                        }
                    });
        }
    }
}
