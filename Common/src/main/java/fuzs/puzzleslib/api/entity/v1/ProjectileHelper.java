package fuzs.puzzleslib.api.entity.v1;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * Copied from {@code Projectile} in Minecraft 26.2.
 */
public final class ProjectileHelper {

    private ProjectileHelper() {
        // NO-OP
    }

    public static <T extends Projectile> T spawnProjectileFromRotation(ProjectileFactory<T> creator, ServerLevel serverLevel, ItemStack itemStack, LivingEntity source, float yOffset, float pow, float uncertainty) {
        return spawnProjectile(creator.create(serverLevel, source, itemStack),
                serverLevel,
                itemStack,
                (T shotProjectile) -> {
                    shotProjectile.shootFromRotation(source,
                            source.getXRot(),
                            source.getYRot(),
                            yOffset,
                            pow,
                            uncertainty);
                });
    }

    public static <T extends Projectile> T spawnProjectileUsingShoot(ProjectileFactory<T> creator, ServerLevel serverLevel, ItemStack itemStack, LivingEntity source, double targetX, double targetY, double targetZ, float pow, float uncertainty) {
        return spawnProjectile(creator.create(serverLevel, source, itemStack),
                serverLevel,
                itemStack,
                (T shotProjectile) -> {
                    shotProjectile.shoot(targetX, targetY, targetZ, pow, uncertainty);
                });
    }

    public static <T extends Projectile> T spawnProjectileUsingShoot(T projectile, ServerLevel serverLevel, ItemStack itemStack, double targetX, double targetY, double targetZ, float pow, float uncertainty) {
        return spawnProjectile(projectile, serverLevel, itemStack, (T shotProjectile) -> {
            shotProjectile.shoot(targetX, targetY, targetZ, pow, uncertainty);
        });
    }

    public static <T extends Projectile> T spawnProjectile(T projectile, ServerLevel serverLevel, ItemStack itemStack) {
        return spawnProjectile(projectile, serverLevel, itemStack, (T shotProjectile) -> {
            // NO-OP
        });
    }

    public static <T extends Projectile> T spawnProjectile(T projectile, ServerLevel serverLevel, ItemStack itemStack, Consumer<T> shootFunction) {
        shootFunction.accept(projectile);
        serverLevel.addFreshEntity(projectile);
//        projectile.applyOnProjectileSpawned(serverLevel, itemStack);
        return projectile;
    }

    @FunctionalInterface
    public interface ProjectileFactory<T extends Projectile> {
        T create(ServerLevel level, LivingEntity entity, ItemStack itemStack);
    }
}
