package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.networking.v3.NetworkHandler;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.puzzleslib.impl.capability.ClientboundSyncCapabilityMessage;
import fuzs.puzzleslib.impl.entity.ClientboundAddEntityDataMessage;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PuzzlesLib implements ModConstructor {
    public static final String MOD_ID = "puzzleslib";
    public static final String MOD_NAME = "Puzzles Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    // allow client only mods using this library
    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID)
            .registerSerializer(ClientboundAddEntityPacket.class, (friendlyByteBuf, clientboundAddEntityPacket) -> clientboundAddEntityPacket.write(friendlyByteBuf), ClientboundAddEntityPacket::new)
            .allAcceptVanillaOrMissing()
            .registerClientbound(ClientboundSyncCapabilityMessage.class)
            .registerClientbound(ClientboundAddEntityDataMessage.class)
            .build();

    @Override
    public void onRegisterBiomeModifications(BiomeModificationsContext context) {
        context.register(BiomeLoadingPhase.MODIFICATIONS, biomeLoadingContext -> biomeLoadingContext.getResourceKey() == Biomes.PLAINS, biomeModificationContext -> {
            biomeModificationContext.mobSpawnSettings().clearSpawns(MobCategory.CREATURE);
            biomeModificationContext.mobSpawnSettings().clearSpawns(MobCategory.MONSTER);
            biomeModificationContext.mobSpawnSettings().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 10, 4, 4));
        });
    }
}
