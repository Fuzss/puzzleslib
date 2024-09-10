package fuzs.puzzleslib.fabric.impl.client.key;

import fuzs.puzzleslib.api.client.key.v1.KeyActivationContext;
import fuzs.puzzleslib.api.client.key.v1.KeyMappingHelper;
import fuzs.puzzleslib.impl.core.EventHandlerProvider;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public final class FabricKeyMappingHelper implements KeyMappingHelper, EventHandlerProvider {
    private Map<KeyMapping, KeyActivationContext> keyMappingActivations = new IdentityHashMap<>();

    @Override
    public void registerEventHandlers() {
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft minecraft) -> {
            // copied from Forge, all this does is stop the key bindings screen from yelling at you for setting incompatible keys,
            // this does not invoke any of our other behaviors for actually implementing activation contexts
            // this implementation relies on careful consideration when setting activation contexts for our own keys
            this.setKeyActivationContext(minecraft.options.keyUp, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keyLeft, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keyDown, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keyRight, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keyJump, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keyShift, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keySprint, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keyAttack, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keyChat, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keyPlayerList, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keyCommand, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keyTogglePerspective, KeyActivationContext.GAME);
            this.setKeyActivationContext(minecraft.options.keySmoothCamera, KeyActivationContext.GAME);
            this.keyMappingActivations = Collections.unmodifiableMap(this.keyMappingActivations);
        });
    }

    public void setKeyActivationContext(KeyMapping keyMapping, KeyActivationContext keyActivationContext) {
        this.keyMappingActivations.put(keyMapping, keyActivationContext);
    }

    @Override
    public KeyActivationContext getKeyActivationContext(KeyMapping keyMapping) {
        return this.keyMappingActivations.getOrDefault(keyMapping, KeyActivationContext.UNIVERSAL);
    }
}
