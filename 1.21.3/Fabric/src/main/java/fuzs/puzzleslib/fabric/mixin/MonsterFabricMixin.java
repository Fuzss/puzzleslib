package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Monster.class)
abstract class MonsterFabricMixin extends PathfinderMob {

    protected MonsterFabricMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(method = "getProjectile", at = @At("RETURN"))
    public ItemStack getProjectile(ItemStack projectileItemStack, ItemStack weaponItemStack) {
        if (weaponItemStack.getItem() instanceof ProjectileWeaponItem) {
            DefaultedValue<ItemStack> projectileItemStackValue = DefaultedValue.fromValue(projectileItemStack);
            FabricLivingEvents.GET_PROJECTILE.invoker()
                    .onGetProjectile(this, weaponItemStack, projectileItemStackValue);
            return projectileItemStackValue.getAsOptional().orElse(projectileItemStack);
        } else {
            return projectileItemStack;
        }
    }
}
