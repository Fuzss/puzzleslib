package fuzs.puzzleslib.mixin.server;

import com.mojang.logging.LogUtils;
import fuzs.puzzleslib.impl.content.ServerPropertiesHelper;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(DedicatedServerSettings.class)
abstract class DedicatedServerSettingsMixin {
    @Shadow
    private DedicatedServerProperties properties;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Path path, CallbackInfo callback) {
        if (!this.properties.serverIp.isEmpty()) return;
        // will print the FileNotFoundException twice, but ¯\_(ツ)_/¯
        this.properties = ServerPropertiesHelper.createDedicatedServerProperties(path, LogUtils.getLogger());
    }
}
