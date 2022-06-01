package fuzs.puzzleslib.capability.test;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.capability.data.CapabilityKey;
import fuzs.puzzleslib.capability.FabricCapabilityController;
import fuzs.puzzleslib.capability.data.PlayerRespawnStrategy;

public class CapabilityTest {

    public static void registerCaps() {
        FabricCapabilityController controller = FabricCapabilityController.of(PuzzlesLib.MOD_ID);
        CapabilityKey<AirHopsCapability> AIR_HOPS_CAPABILITY = controller.registerPlayerCapability("air_hops", AirHopsCapability.class, player -> new AirHopsCapabilityImpl(), PlayerRespawnStrategy.NEVER);
    }

}
