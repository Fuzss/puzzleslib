package fuzs.puzzleslib.fabric.impl.client.core.context;

import com.google.common.collect.Sets;
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
import net.minecraft.client.input.KeyEvent;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class KeyMappingsContextFabricImpl implements KeyMappingsContext {
    private final Set<KeyMapping.Category> keyCategories = Sets.newIdentityHashSet();

    @Override
    public void registerKeyMapping(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        Objects.requireNonNull(activationHandler, "activation handler is null");
        KeyBindingHelper.registerKeyBinding(keyMapping);
        ((FabricKeyMappingHelper) KeyMappingHelper.INSTANCE).setKeyActivationContext(keyMapping,
                activationHandler.getActivationContext());
        this.registerKeyCategoryIfNecessary(keyMapping);
        this.registerKeyActivationHandles(keyMapping, activationHandler);
    }

    private void registerKeyCategoryIfNecessary(KeyMapping keyMapping) {
        Objects.requireNonNull(keyMapping.getCategory(), "key category is null");
        Objects.requireNonNull(keyMapping.getCategory().id(), "key category id is null");
        if (this.keyCategories.add(keyMapping.getCategory())) {
            KeyMapping.Category.register(keyMapping.getCategory().id());
        }
    }

    private void registerKeyActivationHandles(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
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
            ScreenEvents.BEFORE_INIT.register((Minecraft minecraft, Screen screen, int scaledWidth, int scaledHeight) -> {
                if (activationHandler.screenType().isInstance(screen)) {
                    ScreenKeyboardEvents.allowKeyPress(screen).register((Screen currentScreen, KeyEvent keyEvent) -> {
                        if (!(minecraft.screen instanceof KeyBindsScreen) && keyMapping.matches(keyEvent)) {
                            ((Consumer<Screen>) activationHandler.screenHandler()).accept(currentScreen);
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
