package fuzs.puzzleslib.fabric.mixin.client.accessor;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Camera.class)
public interface CameraFabricAccessor {

    @Accessor("xRot")
    void puzzleslib$setXRot(float xRot);

    @Accessor("yRot")
    void puzzleslib$setYRot(float yRot);
}
