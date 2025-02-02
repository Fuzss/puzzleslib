package fuzs.puzzleslib.mixin.server;

import com.mojang.logging.LogUtils;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.dedicated.Settings;
import net.minecraft.world.level.GameType;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Scanner;

@Mixin(DedicatedServerSettings.class)
abstract class DedicatedServerSettingsMixin {
    @Shadow
    private DedicatedServerProperties properties;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Path path, CallbackInfo callback) {
        if (!this.properties.serverIp.isEmpty()) return;
        Logger logger = LogUtils.getLogger();
        // will print the FileNotFoundException twice, but ¯\_(ツ)_/¯
        this.properties = new DedicatedServerProperties(Settings.loadFromFile(path)) {

            DedicatedServerProperties setProperties() {
                this.properties.put("online-mode", String.valueOf(false));
                this.properties.put("gamemode", GameType.CREATIVE.getName());
                this.properties.put("enable-command-block", String.valueOf(true));
                this.properties.put("max-players", String.valueOf(4));
                this.properties.put("spawn-protection", String.valueOf(0));
                this.properties.put("view-distance", String.valueOf(16));
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    logger.warn("Invalid server ip address!");
                    System.out.print("server-ip=");
                    String input = scanner.next();
                    // from https://www.oreilly.com/library/view/regular-expressions-cookbook/9780596802837/ch07s16.html
                    if (input.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                        this.properties.put("server-ip", input);
                        break;
                    }
                }
                return new DedicatedServerProperties(this.properties);
            }
        }.setProperties();
    }
}
