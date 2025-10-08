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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class KeyMappingsContextNeoForgeImpl implements KeyMappingsContext {
    private final RegisterKeyMappingsEvent event;
    private final Set<KeyMapping.Category> keyCategories = new HashSet<>();

    public KeyMappingsContextNeoForgeImpl(RegisterKeyMappingsEvent event) {
        this.event = event;
    }

    @Override
    public void registerKeyMapping(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
        Objects.requireNonNull(keyMapping, "key mapping is null");
        Objects.requireNonNull(activationHandler, "activation handler is null");
        this.event.register(keyMapping);
        keyMapping.setKeyConflictContext(NeoForgeKeyMappingHelper.KEY_CONTEXTS.get(activationHandler.getActivationContext()));
        this.registerKeyCategoryIfNecessary(keyMapping);
        this.registerKeyActivationHandles(keyMapping, activationHandler);
    }

    private void registerKeyCategoryIfNecessary(KeyMapping keyMapping) {
        Objects.requireNonNull(keyMapping.getCategory(), "key category is null");
        Objects.requireNonNull(keyMapping.getCategory().id(), "key category id is null");
        if (this.keyCategories.add(keyMapping.getCategory())) {
            this.event.registerCategory(keyMapping.getCategory());
        }
    }

    private void registerKeyActivationHandles(KeyMapping keyMapping, KeyActivationHandler activationHandler) {
        Consumer<Minecraft> gameConsumer = activationHandler.gameHandler();
        if (gameConsumer != null) {
            NeoForge.EVENT_BUS.addListener((final ClientTickEvent.Pre event) -> {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player != null) {
                    while (keyMapping.consumeClick()) {
                        gameConsumer.accept(minecraft);
                    }
                }
            });
        }

        Consumer<Screen> screenConsumer = (Consumer<Screen>) activationHandler.screenHandler();
        if (screenConsumer != null) {
            NeoForge.EVENT_BUS.addListener((final ScreenEvent.KeyPressed.Pre event) -> {
                if (activationHandler.screenType().isInstance(event.getScreen())) {
                    if (keyMapping.matches(event.getKeyEvent())) {
                        screenConsumer.accept(event.getScreen());
                        event.setCanceled(true);
                    }
                }
            });
        }
    }
}
