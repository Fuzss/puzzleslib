package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.client.event.v1.InventoryMobEffectsCallback;
import fuzs.puzzleslib.api.client.event.v1.MouseScreenEvents;
import fuzs.puzzleslib.api.client.event.v1.RenderGuiElementEvents;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.level.ExplosionEvents;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.capability.ClientboundSyncCapabilityMessage;
import fuzs.puzzleslib.impl.entity.ClientboundAddEntityDataMessage;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuzzlesLib implements ModConstructor {
    public static final String MOD_ID = "puzzleslib";
    public static final String MOD_NAME = "Puzzles Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    // allow client-only mods using this library
    public static final NetworkHandlerV3 NETWORK = NetworkHandlerV3.builder(MOD_ID)
            .registerSerializer(ClientboundAddEntityPacket.class, (friendlyByteBuf, clientboundAddEntityPacket) -> clientboundAddEntityPacket.write(friendlyByteBuf), ClientboundAddEntityPacket::new)
            .allAcceptVanillaOrMissing()
            .registerClientbound(ClientboundSyncCapabilityMessage.class)
            .registerClientbound(ClientboundAddEntityDataMessage.class);

    @Override
    public void onConstructMod() {
        // TODO remove test code
        MouseScreenEvents.beforeMouseScroll(SelectWorldScreen.class).register((SelectWorldScreen screen, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) -> {
            return EventResult.INTERRUPT;
        });
        RenderGuiElementEvents.before(RenderGuiElementEvents.POTION_ICONS).register((poseStack, screenWidth, screenHeight) -> {
            return EventResult.INTERRUPT;
        });
        InventoryMobEffectsCallback.EVENT.register((screen, availableSpace, smallWidgets, horizontalOffset) -> {
            smallWidgets.accept(true);
            return EventResult.PASS;
        });
        ExplosionEvents.START.register((level, explosion) -> {
            return EventResult.INTERRUPT;
        });
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
