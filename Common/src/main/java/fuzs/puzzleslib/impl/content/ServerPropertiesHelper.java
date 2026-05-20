package fuzs.puzzleslib.impl.content;

import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.Settings;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;
import org.slf4j.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/**
 * Keep this separate to prevent early class loading issues from the mixin.
 */
public final class ServerPropertiesHelper {

    private ServerPropertiesHelper() {
        // NO-OP
    }

    public static DedicatedServerProperties createDedicatedServerProperties(Path path, Logger logger) {
        return new DedicatedServerProperties(Settings.loadFromFile(path)) {
            DedicatedServerProperties setProperties() {
                this.properties.put("online-mode", String.valueOf(false));
                this.properties.put("difficulty", Difficulty.HARD.getSerializedName());
                this.properties.put("gamemode", GameType.CREATIVE.getSerializedName());
                this.properties.put("max-players", String.valueOf(4));
                this.properties.put("spawn-protection", String.valueOf(0));
                this.properties.put("view-distance", String.valueOf(16));
                Scanner scanner = new Scanner(System.in);
                Optional<String> hostAddress = getHostAddress();
                while (true) {
                    logger.warn("Invalid server ip address! {}",
                            hostAddress.isPresent() ? hostAddress.map((String string) -> {
                                return "Using fallback: " + string;
                            }).get() : "");
                    System.out.print("server-ip=");
                    String input = scanner.nextLine();
                    if (input.isBlank() && hostAddress.isPresent()) {
                        input = hostAddress.get();
                    }

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

    public static Optional<String> getHostAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isLoopback() && networkInterface.isUp()) {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (address instanceof Inet4Address && !Objects.equals(address.getHostAddress(), "127.0.0.1")) {
                            return Optional.of(address.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception exception) {
            // NO-OP
        }

        return Optional.empty();
    }
}
