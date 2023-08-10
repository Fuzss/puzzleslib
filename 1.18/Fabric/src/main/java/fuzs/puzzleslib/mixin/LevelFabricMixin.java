package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Level.class)
abstract class LevelFabricMixin {
    @Nullable
    @Unique
    private Explosion puzzleslib$activeExplosion;

    @ModifyVariable(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)Lnet/minecraft/world/level/Explosion;", at = @At("STORE"), ordinal = 0)
    public Explosion explode$0(Explosion explosion) {
        this.puzzleslib$activeExplosion = explosion;
        return explosion;
    }

    @Inject(method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Explosion$BlockInteraction;)Lnet/minecraft/world/level/Explosion;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Explosion;explode()V"), cancellable = true)
    public void explode$1(@Nullable Entity source, @Nullable DamageSource damageSource, @Nullable ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction explosionInteraction, CallbackInfoReturnable<Explosion> callback) {
        Objects.requireNonNull(this.puzzleslib$activeExplosion, "active explosion is null");
        EventResult result = FabricLevelEvents.EXPLOSION_START.invoker().onExplosionStart(Level.class.cast(this), this.puzzleslib$activeExplosion);
        if (result.isInterrupt()) callback.setReturnValue(this.puzzleslib$activeExplosion);
        this.puzzleslib$activeExplosion = null;
    }
}
