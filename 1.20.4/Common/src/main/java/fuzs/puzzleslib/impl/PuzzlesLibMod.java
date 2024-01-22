package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.api.init.v3.override.CommandOverrides;
import fuzs.puzzleslib.api.init.v3.override.GameRuleValueOverrides;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.puzzleslib.impl.capability.ClientboundEntityCapabilityMessage;
import fuzs.puzzleslib.impl.core.ClientboundModListMessage;
import fuzs.puzzleslib.impl.core.EventHandlerProvider;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.entity.ClientboundAddEntityDataMessage;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import net.minecraft.SharedConstants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameRules;

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
        setupDevelopmentEnvironment();
    }

    private static void setupDevelopmentEnvironment() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) return;
        SharedConstants.IS_RUNNING_IN_IDE = true;
        ServerLifecycleEvents.STARTING.register((MinecraftServer server) -> {
            if (server instanceof DedicatedServer) {
                server.setUsesAuthentication(false);
            }
        });
        CommandOverrides.registerHandlers();
        initializeGameRules();
        initializeCommands();
    }

    private static void initializeCommands() {
        CommandOverrides.registerServerCommand("time set 4000", false);
        CommandOverrides.registerPlayerCommand("op %s", true);
        CommandOverrides.registerEffectCommand(MobEffects.NIGHT_VISION, MobEffects.DAMAGE_RESISTANCE, MobEffects.FIRE_RESISTANCE, MobEffects.SATURATION, MobEffects.DAMAGE_BOOST, MobEffects.WATER_BREATHING);
    }

    private static void initializeGameRules() {
        GameRuleValueOverrides.setValue(GameRules.RULE_DAYLIGHT, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_WEATHER_CYCLE, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_KEEPINVENTORY, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_DOFIRETICK, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_MOBGRIEFING, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_DOINSOMNIA, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_DO_PATROL_SPAWNING, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_DO_TRADER_SPAWNING, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_DO_VINES_SPREAD, false);
    }
}
