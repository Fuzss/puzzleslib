package fuzs.puzzleslib.client;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import fuzs.puzzleslib.PuzzlesLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CapeInfoLoader {

    /**
     * json file url
     */
    private static final String CAPE_INFO_URL = "https://raw.githubusercontent.com/Fuzss/modresources/main/capes.json";
    /**
     * defaulr error message
     */
    private static final String ERROR_STRING = "Unable to load cape info";

    /**
     * may contain uuid or player name
     */
    private Map<String, String> uuidToCapeUrl;
    /**
     * player info field cache
     */
    private final Field playerInfoField = ObfuscationReflectionHelper.findField(AbstractClientPlayerEntity.class, "playerInfo");
    /**
     * texture locations field cache
     */
    private final Field textureLocationsField = ObfuscationReflectionHelper.findField(NetworkPlayerInfo.class, "textureLocations");

    /**
     * construct and load json file from web
     */
    public CapeInfoLoader() {

        this.loadInfoFile();
    }

    /**
     * load json file from web
     */
    @SuppressWarnings({"UnstableApiUsage", "unchecked"})
    private void loadInfoFile() {

        String data = null;
        try (InputStream inputStream = new URL(CAPE_INFO_URL).openStream()) {

            data = new String(ByteStreams.toByteArray(inputStream), StandardCharsets.UTF_8);
        } catch (IOException e) {

            PuzzlesLib.LOGGER.warn(ERROR_STRING, e);
        }

        this.uuidToCapeUrl = new Gson().fromJson(data, Map.class);
    }

    /**
     * this is registered in main class
     * @param evt forge event
     */
    public void onRenderPlayer(final RenderPlayerEvent.Post evt) {

        PlayerEntity player = evt.getPlayer();
        if (this.uuidToCapeUrl != null && player instanceof AbstractClientPlayerEntity && ((AbstractClientPlayerEntity) player).isCapeLoaded()) {

            GameProfile profile = player.getGameProfile();
            this.addTextureLocations((AbstractClientPlayerEntity) player, profile.getId().toString(), profile.getName());
        }
    }

    /**
     * @param player render player
     * @param uuid player uuid
     * @param name player name
     */
    @SuppressWarnings("unchecked")
    private void addTextureLocations(AbstractClientPlayerEntity player, String uuid, String name) {

        String capeUrl = this.getCapeUrl(uuid, name);
        if (capeUrl == null) {

            return;
        }

        try {

            Object playerInfo = this.playerInfoField.get(player);
            Map<MinecraftProfileTexture.Type, ResourceLocation> textures = (Map<MinecraftProfileTexture.Type, ResourceLocation>) this.textureLocationsField.get(playerInfo);
            // elytra is unused but why not
            for (MinecraftProfileTexture.Type type : ImmutableList.of(MinecraftProfileTexture.Type.CAPE, MinecraftProfileTexture.Type.ELYTRA)) {

                ResourceLocation location = this.registerSkinTexture(capeUrl, type);
                textures.put(type, location);
            }
        } catch (IllegalAccessException e) {

            PuzzlesLib.LOGGER.warn(ERROR_STRING, e);
        }

        this.uuidToCapeUrl.remove(uuid);
        this.uuidToCapeUrl.remove(name);
    }

    /**
     * get cape url for either uuid or name
     * @param uuid player uuid
     * @param name player name
     * @return url or null
     */
    private String getCapeUrl(String uuid, String name) {

        String capeUrl = this.uuidToCapeUrl.get(uuid);
        return capeUrl != null ? capeUrl : this.uuidToCapeUrl.get(name);
    }

    /**
     * @param url url to load cape texture from
     * @param type skin texture type
     * @return created resource location
     */
    private ResourceLocation registerSkinTexture(String url, MinecraftProfileTexture.Type type) {

        MinecraftProfileTexture profileTexture = new MinecraftProfileTexture(url, null);
        return Minecraft.getInstance().getSkinManager().registerTexture(profileTexture, type);
    }

}
