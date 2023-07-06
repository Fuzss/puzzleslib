package fuzs.puzzleslib.core;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.LogicalSide;

/**
 * converts our custom {@link DistType} from and to Forge equivalent(s)
 */
public class DistTypeConverter {

    public static LogicalSide toLogicalSide(DistType distType) {
        return switch (distType) {
            case CLIENT -> LogicalSide.CLIENT;
            case SERVER -> LogicalSide.SERVER;
        };
    }

    public static DistType fromLogicalSide(LogicalSide logicalSide) {
        return switch (logicalSide) {
            case CLIENT -> DistType.CLIENT;
            case SERVER -> DistType.SERVER;
        };
    }

    public static Dist toDist(DistType distType) {
        return switch (distType) {
            case CLIENT -> Dist.CLIENT;
            case SERVER -> Dist.DEDICATED_SERVER;
        };
    }

    public static DistType fromDist(Dist dist) {
        return switch (dist) {
            case CLIENT -> DistType.CLIENT;
            case DEDICATED_SERVER -> DistType.SERVER;
        };
    }
}
