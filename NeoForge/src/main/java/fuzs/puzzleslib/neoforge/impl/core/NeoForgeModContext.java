package fuzs.puzzleslib.neoforge.impl.core;

import fuzs.puzzleslib.common.api.data.v2.ModPackMetadataProvider;
import fuzs.puzzleslib.common.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.common.impl.core.ModContext;
import fuzs.puzzleslib.common.impl.init.RegistryManagerImpl;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.puzzleslib.neoforge.impl.config.NeoForgeConfigHolderImpl;
import fuzs.puzzleslib.neoforge.impl.init.NeoForgeRegistryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

public final class NeoForgeModContext extends ModContext {

    public NeoForgeModContext(String modId) {
        super(modId);
        DataProviderHelper.registerDataProviders(modId, ModPackMetadataProvider::new);
    }

    @Override
    protected void setupHandshakePayload(String modId, CustomPacketPayload.Type<BrandPayload> payloadType) {
        NeoForgeModContainerHelper.getOptionalModEventBus(modId).ifPresent((IEventBus eventBus) -> {
            eventBus.addListener((final RegisterPayloadHandlersEvent event) -> {
                event.registrar(payloadType.id().toString())
                        .optional()
                        .playBidirectional(payloadType,
                                BrandPayload.STREAM_CODEC,
                                (BrandPayload payload, IPayloadContext context) -> {
                                    // NO-OP
                                },
                                (BrandPayload payload, IPayloadContext context) -> {
                                    // NO-OP
                                });
            });
        });
    }

    @Override
    protected boolean isPresentServerside(CustomPacketPayload.Type<BrandPayload> payloadType) {
        ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
        return clientPacketListener != null && clientPacketListener.hasChannel(payloadType);
    }

    @Override
    protected boolean isPresentClientside(CustomPacketPayload.Type<BrandPayload> payloadType, ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        return serverPlayer.connection.hasChannel(payloadType);
    }

    @Override
    protected ConfigHolderImpl createConfigHolder(String modId) {
        return new NeoForgeConfigHolderImpl(modId);
    }

    @Override
    protected RegistryManagerImpl createRegistryManager(String modId) {
        return new NeoForgeRegistryManager(modId);
    }
}
