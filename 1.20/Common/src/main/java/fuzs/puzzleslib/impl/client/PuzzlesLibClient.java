package fuzs.puzzleslib.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.api.client.event.v1.ScreenKeyboardEvents;
import fuzs.puzzleslib.api.client.screen.v2.KeyMappingActivationHelper;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public class PuzzlesLibClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        ClientTickEvents.START.register(KeyBindingHandler::onClientTick$Start);
        ScreenKeyboardEvents.beforeKeyPress(AbstractContainerScreen.class).register((screen, key, scanCode, modifiers) -> {
            if (KeyBindingHandler.TOGGLE_PLAQUES_KEY_MAPPING.matches(key, scanCode)) {
                KeyBindingHandler.setClientMessage(Minecraft.getInstance(), false, 1);
                return EventResult.INTERRUPT;
            }
            if (KeyBindingHandler.TOGGLE_PLAQUES_KEY_MAPPING2.matches(key, scanCode)) {
                KeyBindingHandler.setClientMessage(Minecraft.getInstance(), false, 2);
                return EventResult.INTERRUPT;
            }
            return EventResult.PASS;
        });
    }

    @Override
    public void onRegisterKeyMappings(KeyMappingsContext context) {
        context.registerKeyMapping(KeyBindingHandler.TOGGLE_PLAQUES_KEY_MAPPING, KeyMappingActivationHelper.KeyActivationContext.SCREEN);
        context.registerKeyMapping(KeyBindingHandler.TOGGLE_PLAQUES_KEY_MAPPING2, KeyMappingActivationHelper.KeyActivationContext.GAME);
    }
}
