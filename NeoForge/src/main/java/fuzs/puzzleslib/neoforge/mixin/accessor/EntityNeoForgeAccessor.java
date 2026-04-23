package fuzs.puzzleslib.neoforge.mixin.accessor;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityNeoForgeAccessor {
    @Invoker("canRide")
    boolean puzzleslib$canRide(Entity vehicle);

    @Invoker("canAddPassenger")
    boolean puzzleslib$canAddPassenger(Entity passenger);
}
