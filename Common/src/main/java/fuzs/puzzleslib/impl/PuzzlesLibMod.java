package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.capability.ClientboundEntityCapabilityMessage;
import fuzs.puzzleslib.impl.core.ClientboundModListMessage;
import fuzs.puzzleslib.impl.core.EventHandlerProvider;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.entity.ClientboundAddEntityDataMessage;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;

/**
 * This has been separated from {@link PuzzlesLib} to prevent issues with static initialization when accessing constants in {@link PuzzlesLib} early.
 */
public class PuzzlesLibMod extends PuzzlesLib implements ModConstructor {
    public static final NetworkHandlerV3 NETWORK = NetworkHandlerV3.builder(MOD_ID).optional()
            .registerSerializer(ClientboundAddEntityPacket.class, (FriendlyByteBuf friendlyByteBuf, ClientboundAddEntityPacket clientboundAddEntityPacket) -> {
                clientboundAddEntityPacket.write(friendlyByteBuf);
            }, ClientboundAddEntityPacket::new)
            .registerClientbound(ClientboundEntityCapabilityMessage.class)
            .registerClientbound(ClientboundAddEntityDataMessage.class)
            .registerClientbound(ClientboundModListMessage.class);

    @Override
    public void onConstructMod() {
        ModContext.registerHandlers();
        EventHandlerProvider.tryRegister(CommonAbstractions.INSTANCE);
        LoadCompleteCallback.EVENT.register(EventInvokerImpl::initialize);
    }
}
