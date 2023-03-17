package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.networking.v3.NetworkHandlerV3;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.impl.capability.ClientboundSyncCapabilityMessage;
import fuzs.puzzleslib.impl.entitydata.ClientboundAddEntityDataMessage;
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
            .registerClientbound(ClientboundAddEntityDataMessage.class)
            .build();

    @Override
    public void onConstructMod() {
        NETWORK.initialize();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
