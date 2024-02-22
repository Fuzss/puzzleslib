package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.KeyMappingsContext;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationHandler;
import fuzs.puzzleslib.neoforge.impl.client.key.NeoForgeKeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

import java.util.Objects;
import java.util.function.Consumer;

public record KeyMappingsContextNeoForgeImpl(Consumer<KeyMapping> consumer) implements KeyMappingsContext {

    @Override
    public void registerKeyMapping(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        Objects.requireNonNull(activationHandler, "activation handles is null");
        this.consumer.accept(keyMapping);
        keyMapping.setKeyConflictContext(NeoForgeKeyMappingHelper.KEY_CONTEXTS.get(activationHandler.getActivationContext()));
        registerKeyActivationHandles(keyMapping, activationHandler);
    }

    private static void registerKeyActivationHandles(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
        if (activationHandler.gameHandler() != null) {
            NeoForge.EVENT_BUS.addListener((final TickEvent.ClientTickEvent evt) -> {
                if (evt.phase != TickEvent.Phase.START) return;
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    while (keyMapping.consumeClick()) {
                        activationHandler.gameHandler().accept(minecraft);
                    }
                }
            });
        }
        if (activationHandler.screenHandler() != null) {
            NeoForge.EVENT_BUS.addListener((final ScreenEvent.KeyPressed.Pre evt) -> {
                if (activationHandler.screenType().isInstance(evt.getScreen())) {
                    if (keyMapping.matches(evt.getKeyCode(), evt.getScanCode())) {
                        ((Consumer<Screen>) activationHandler.screenHandler()).accept(evt.getScreen());
                        evt.setCanceled(true);
                    }
                }
            });
        }
    }
}
