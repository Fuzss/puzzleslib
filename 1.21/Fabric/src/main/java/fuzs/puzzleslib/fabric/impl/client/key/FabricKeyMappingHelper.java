package fuzs.puzzleslib.fabric.impl.client.key;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.blaze3d.platform.InputConstants;
import fuzs.puzzleslib.api.client.event.v1.InputEvents;
import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class FabricKeyMappingHelper implements KeyMappingHelper {
    private static final Multimap<KeyActivationContext, KeyMapping> KEY_MAPPINGS_BY_ACTIVATION = Multimaps.newListMultimap(Maps.newEnumMap(KeyActivationContext.class), ArrayList::new);
    private static final Map<KeyMapping, KeyActivationContext> KEY_MAPPINGS_TO_ACTIVATION = Maps.newIdentityHashMap();

    {
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft minecraft) -> {
            // copied from Forge, all this does is stop the key bindings screen from yelling at you for setting incompatible keys,
            // this does not invoke any of our other behaviors for actually implementing activation contexts
            // this implementation relies on careful consideration when setting activation contexts for our own keys
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyUp, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyLeft, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyDown, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyRight, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyJump, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyShift, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keySprint, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyAttack, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyChat, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyPlayerList, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyCommand, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keyTogglePerspective, KeyActivationContext.GAME);
            KEY_MAPPINGS_TO_ACTIVATION.put(minecraft.options.keySmoothCamera, KeyActivationContext.GAME);
        });
        // this would probably be much safer as a mixin on KeyMapping, but this is the way it is for now...
        Map<KeyMapping, InputConstants.Key> keys = Maps.newLinkedHashMap();
        InputEvents.BEFORE_KEY_ACTION.register(EventPhase.LAST, (key, scanCode, action, modifiers) -> {
            this.clearKeys(keys, keyMapping -> keyMapping.matches(key, scanCode), () -> InputConstants.getKey(key, scanCode));
            return EventResult.PASS;
        });
        InputEvents.AFTER_KEY_ACTION.register(EventPhase.FIRST, (key, scanCode, action, modifiers) -> {
            this.setOriginalKeys(keys);
        });
        InputEvents.BEFORE_MOUSE_ACTION.register(EventPhase.LAST, (button, action, modifiers) -> {
            this.clearKeys(keys, keyMapping -> keyMapping.matchesMouse(button), () -> InputConstants.Type.MOUSE.getOrCreate(button));
            return EventResult.PASS;
        });
        InputEvents.AFTER_MOUSE_ACTION.register(EventPhase.FIRST, (button, action, modifiers) -> {
            this.setOriginalKeys(keys);
        });
    }

    public static void setKeyActivationContext(KeyMapping keyMapping, KeyActivationContext keyActivationContext) {
        KEY_MAPPINGS_BY_ACTIVATION.put(keyActivationContext, keyMapping);
        KEY_MAPPINGS_TO_ACTIVATION.put(keyMapping, keyActivationContext);
    }

    @Override
    public KeyActivationContext getKeyActivationContext(KeyMapping keyMapping) {
        return KEY_MAPPINGS_TO_ACTIVATION.getOrDefault(keyMapping, KeyActivationContext.UNIVERSAL);
    }

    private void clearKeys(Map<KeyMapping, InputConstants.Key> keys, Predicate<KeyMapping> filter, Supplier<InputConstants.Key> key) {
        // if after events are unable to run and the map isn't cleared, stop here to not mess everything up completely
        if (!keys.isEmpty()) return;
        Screen screen = Minecraft.getInstance().screen;
        // key bindings screen needs all input, so just exclude that
        if (!(screen instanceof KeyBindsScreen)) {
            KeyActivationContext inactive = screen != null ? KeyActivationContext.GAME : KeyActivationContext.SCREEN;
            KEY_MAPPINGS_BY_ACTIVATION.get(inactive).forEach(keyMapping -> {
                if (filter.test(keyMapping)) {
                    // set key for mapping instance to unknown, so it doesn't run during screen processing
                    keys.put(keyMapping, key.get());
                    keyMapping.setKey(InputConstants.UNKNOWN);
                }
            });
        }
    }

    private void setOriginalKeys(Map<KeyMapping, InputConstants.Key> keys) {
        keys.forEach(KeyMapping::setKey);
        keys.keySet().forEach(keyMapping -> {
            // disable key states for mapping instances processed by client tick methods
            keyMapping.setDown(false);
            while (keyMapping.consumeClick()) {
                // NO-OP
            }
        });
        keys.clear();
    }
}
