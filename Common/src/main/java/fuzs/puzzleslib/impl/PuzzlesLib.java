package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.impl.network.ClientboundSyncCapabilityMessage;
import fuzs.puzzleslib.network.v2.NetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuzzlesLib implements ModConstructor {
    public static final String MOD_ID = "puzzleslib";
    public static final String MOD_NAME = "Puzzles Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    // set this to allow client only mods using this library
    public static final NetworkHandler NETWORK = CommonFactories.INSTANCE.networkV2(MOD_ID).clientAcceptsVanillaOrMissing().serverAcceptsVanillaOrMissing().registerClientbound(ClientboundSyncCapabilityMessage.class).build();

    @Override
    public void onConstructMod() {
        registerMessages();
    }

    private static void registerMessages() {
        NETWORK.registerClientbound(ClientboundSyncCapabilityMessage.class);
    }
}
