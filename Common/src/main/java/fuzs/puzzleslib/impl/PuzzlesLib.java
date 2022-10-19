package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.impl.network.S2CSyncCapabilityMessage;
import fuzs.puzzleslib.network.MessageDirection;
import fuzs.puzzleslib.network.NetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuzzlesLib implements ModConstructor {
    public static final String MOD_ID = "puzzleslib";
    public static final String MOD_NAME = "Puzzles Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    // set this to allow client only mods using this library
    public static final NetworkHandler NETWORK = CommonFactories.INSTANCE.network(MOD_ID, true, true);

    @Override
    public void onConstructMod() {
        registerMessages();
    }

    private static void registerMessages() {
        NETWORK.register(S2CSyncCapabilityMessage.class, S2CSyncCapabilityMessage::new, MessageDirection.TO_CLIENT);
    }
}
