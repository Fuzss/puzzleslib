package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationHandler;
import fuzs.puzzleslib.neoforge.impl.client.key.NeoForgeKeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Objects;
import java.util.function.Consumer;

public record KeyMappingsContextNeoForgeImpl(RegisterKeyMappingsEvent event) implements KeyMappingsContext {

    @Override
    public void registerKeyMapping(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        Objects.requireNonNull(activationHandler, "activation handler is null");
        this.event.register(keyMapping);
        keyMapping.setKeyConflictContext(NeoForgeKeyMappingHelper.KEY_CONTEXTS.get(activationHandler.getActivationContext()));
        registerKeyActivationHandles(keyMapping, activationHandler);
    }

    private static void registerKeyActivationHandles(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
        if (activationHandler.gameHandler() != null) {
            NeoForge.EVENT_BUS.addListener((final ClientTickEvent.Pre event) -> {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    while (keyMapping.consumeClick()) {
                        activationHandler.gameHandler().accept(minecraft);
                    }
                }
            });
        }
        if (activationHandler.screenHandler() != null) {
            NeoForge.EVENT_BUS.addListener((final ScreenEvent.KeyPressed.Pre event) -> {
                if (activationHandler.screenType().isInstance(event.getScreen())) {
                    if (keyMapping.matches(event.getKeyCode(), event.getScanCode())) {
                        ((Consumer<Screen>) activationHandler.screenHandler()).accept(event.getScreen());
                        event.setCanceled(true);
                    }
                }
            });
        }
    }
}
