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
        // TODO use future event method for registering category, add check if category is already registered, probably via boolean flag on this class
        registerKeyCategoryIfNecessary(keyMapping);
        registerKeyActivationHandles(keyMapping, activationHandler);
    }

    private static void registerKeyCategoryIfNecessary(KeyMapping keyMapping) {
        Objects.requireNonNull(keyMapping.getCategory(), "key category is null");
        Objects.requireNonNull(keyMapping.getCategory().id(), "key category id is null");
        if (!KeyMapping.Category.SORT_ORDER.contains(keyMapping.getCategory())) {
            KeyMapping.Category.register(keyMapping.getCategory().id());
        }
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
                    if (keyMapping.matches(event.getKeyEvent())) {
                        ((Consumer<Screen>) activationHandler.screenHandler()).accept(event.getScreen());
                        event.setCanceled(true);
                    }
                }
            });
        }
    }
}
