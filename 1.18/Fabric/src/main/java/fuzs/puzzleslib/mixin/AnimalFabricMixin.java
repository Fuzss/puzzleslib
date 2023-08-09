package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Animal.class)
abstract class AnimalFabricMixin extends AgeableMob {

    protected AnimalFabricMixin(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "spawnChildFromBreeding", at = @At("STORE"))
    public AgeableMob spawnChildFromBreeding(@Nullable AgeableMob child, ServerLevel serverLevel, Animal partner) {
        MutableValue<AgeableMob> defaultedChild = MutableValue.fromValue(child);
        if (FabricLivingEvents.BABY_ENTITY_SPAWN.invoker().onBabyEntitySpawn(this, partner, defaultedChild).isInterrupt()) {
            this.setAge(6000);
            partner.setAge(6000);
            this.resetLove();
            partner.resetLove();
            return null;
        } else {
            return defaultedChild.get();
        }
    }

    @Shadow
    public abstract void resetLove();
}
