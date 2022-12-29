package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.impl.networking.ClientboundAddEntityDataMessage;
import fuzs.puzzleslib.impl.networking.ClientboundSyncCapabilityMessage;
import fuzs.puzzleslib.network.v2.NetworkHandler;
import fuzs.puzzleslib.network.v2.serialization.MessageSerializers;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuzzlesLib implements ModConstructor {
    public static final String MOD_ID = "puzzleslib";
    public static final String MOD_NAME = "Puzzles Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandler NETWORK;

    static {
        MessageSerializers.registerSerializer(ClientboundAddEntityPacket.class, (friendlyByteBuf, clientboundAddEntityPacket) -> clientboundAddEntityPacket.write(friendlyByteBuf), ClientboundAddEntityPacket::new);
        // allow client only mods using this library
        NETWORK = CommonFactories.INSTANCE.networkV2(MOD_ID).clientAcceptsVanillaOrMissing().serverAcceptsVanillaOrMissing().registerClientbound(ClientboundSyncCapabilityMessage.class).registerClientbound(ClientboundAddEntityDataMessage.class).build();
    }
}
