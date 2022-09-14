package fuzs.puzzleslib.core;

import net.fabricmc.api.EnvType;

/**
 * converts our custom {@link DistType} from and to Fabric equivalent(s)
 */
public class DistTypeConverter {

    /**
     * @param distType distType
     * @return {@link EnvType}
     */
    public static EnvType toEnvType(DistType distType) {
        return switch (distType) {
            case CLIENT -> EnvType.CLIENT;
            case SERVER -> EnvType.SERVER;
        };
    }

    /**
     * @param envType envType
     * @return {@link DistType}
     */
    public static DistType fromEnvType(EnvType envType) {
        return switch (envType) {
            case CLIENT -> DistType.CLIENT;
            case SERVER -> DistType.SERVER;
        };
    }
}
